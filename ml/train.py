from torch import nn
from torch.utils.data import DataLoader
from torch.optim import Optimizer
import torch
import logging
import os

class Trainer:
    def __init__(
            self,
            model: nn.Module,
            model_name: str,
            loss_fn: nn.Module,
            optimizer: Optimizer,
            train_loader: DataLoader,
            val_loader: DataLoader,
            device: torch.device,
            logger: logging.Logger,
            scheduler: torch.optim.lr_scheduler.LRScheduler = None,
            save_dir:str = "models/saved_models",
    ):
        self.model = model.to(device)
        self.model_name = model_name
        self.loss_fn = loss_fn
        self.optimizer = optimizer
        self.train_loader = train_loader
        self.val_loader = val_loader
        self.device = device
        self.logger = logger
        self.scheduler = scheduler
        self.save_dir = save_dir

        self.val_best_loss_total = float("inf")
        self.val_best_acc_total = 0.0

        os.makedirs(self.save_dir, exist_ok=True)

    @property
    def model_path(self) -> str:
        return os.path.join(self.save_dir, f"{self.model_name}.pt")

    def load_best_model(self):
        self.logger.info(f"Loading best model from {self.model_path}")
        try:
            self.model.load_state_dict(
                torch.load(self.model_path, map_location=self.device)
            )
        except FileNotFoundError:
            self.logger.error(f"Model not found at {self.model_path}")
            raise

    def train_one_epoch(self) -> float:
        self.model.train(True)
        train_cum_loss = 0
        for i, data in enumerate(self.train_loader):
            inputs, labels = data
            inputs, labels = inputs.to(self.device), labels.to(self.device)
            self.optimizer.zero_grad()
            outputs = self.model(inputs)
            loss = self.loss_fn(outputs, labels)
            loss.backward()
            self.optimizer.step()
            mean_loss = loss.item()
            train_cum_loss += mean_loss * inputs.shape[0]
        train_avg_loss = train_cum_loss / len(self.train_loader.dataset)
        return train_avg_loss

    def evaluate(
            self,
            loader: DataLoader,
    )  -> (float, float):
        self.model.train(False)
        correct = 0
        cum_loss = 0
        for i, data in enumerate(loader):
            inputs, labels = data
            inputs, labels = inputs.to(self.device), labels.to(self.device)
            with torch.no_grad():
                outputs = self.model(inputs)
                loss = self.loss_fn(outputs, labels)
                mean_loss = loss.item()

            cum_loss += mean_loss * inputs.shape[0]
            correct += (outputs.argmax(1) == labels).sum().item()
        avg_loss = cum_loss / len(loader.dataset)
        accuracy = correct / len(loader.dataset)
        return avg_loss, accuracy

    def validate(self) -> (float, float):
        return self.evaluate(self.val_loader)

    def train_and_validate(
            self,
            max_epochs=20,
            patience=5,
    ):
        epochs_from_best = 0
        val_best_loss = float('inf')
        val_acc_with_best_loss = 0

        self.logger.info(f"####################")
        self.logger.info(f"Training model: {self.model_name}")
        for epoch in range(max_epochs):
            self.logger.info(f"Epoch: {epoch + 1} / {max_epochs}")
            train_avg_loss = self.train_one_epoch()

            val_loss, val_acc = self.validate()
            if self.scheduler is not None:
                self.scheduler.step(val_loss)

            self.logger.info(f"\tTrain loss: {train_avg_loss:.4f}")
            self.logger.info(f"\tVal loss: {val_loss:.4f}")
            self.logger.info(f"\tVal acc: {val_acc:.4f}")

            if val_loss <= val_best_loss:
                val_best_loss = val_loss
                val_acc_with_best_loss = val_acc
                epochs_from_best = 0
                torch.save(self.model.state_dict(), self.model_path)
                self.logger.info(f"Saved model to {self.model_path}")
            else:
                epochs_from_best += 1

            if epochs_from_best > patience:
                self.logger.info(f"Interrupting training - no improvement in {epochs_from_best} epochs")
                break

        self.load_best_model()
        return val_best_loss, val_acc_with_best_loss
