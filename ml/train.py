from dataclasses import dataclass, field
from torch import nn
from torch.utils.data import DataLoader
from torch.optim import Optimizer
import torch
import logging
import os
from ml.config import PTH_SAVE_DIR

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
            save_dir:str = PTH_SAVE_DIR,
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
        self.progress: list[Result] = []

        os.makedirs(self.save_dir, exist_ok=True)

    @property
    def model_path(self) -> str:
        return get_model_path(self.save_dir, self.model_name)

    def load_best_model(self):
        self.logger.info(f"Loading best model from {self.model_path}")
        try:
            self.model.load_state_dict(
                torch.load(self.model_path, map_location=self.device)
            )
        except FileNotFoundError:
            self.logger.error(f"Model not found at {self.model_path}")
            raise

    def train_one_epoch(self) -> tuple[float, float]:
        self.model.train(True)
        correct = 0
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
            correct += (outputs.argmax(1) == labels).sum().item()
        train_avg_loss = train_cum_loss / len(self.train_loader.dataset)
        train_acc = correct / len(self.train_loader.dataset)
        return train_avg_loss, train_acc

    def validate(self) -> (float, float):
        return evaluate(self.model, self.val_loader, self.loss_fn, self.device)

    def train_and_validate(
            self,
            max_epochs=20,
            patience=5,
    ):
        epochs_from_best = 0
        val_best_loss = float('inf')
        val_acc_with_best_loss = 0

        self.progress: list[Result] = []

        self.logger.info(f"####################")
        self.logger.info(f"Training model: {self.model_name}")
        for epoch in range(max_epochs):
            self.logger.info(f"Epoch: {epoch + 1} / {max_epochs}")
            train_avg_loss, train_acc = self.train_one_epoch()

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

            result = Result(train_acc, train_avg_loss, val_acc, val_loss)
            self.progress.append(result)

            if epochs_from_best > patience:
                self.logger.info(f"Interrupting training - no improvement in {epochs_from_best} epochs")
                break

        self.load_best_model()
        return val_best_loss, val_acc_with_best_loss

def get_model_path(model_name: str, model_dir = PTH_SAVE_DIR):
    return os.path.join(model_dir, f"{model_name}.pt")

def evaluate(
        model: nn.Module,
        loader: DataLoader,
        loss_fn: nn.Module,
        device: torch.device,
)  -> (float, float):
    model = model.to(device)
    model.train(False)
    correct = 0
    cum_loss = 0
    for i, data in enumerate(loader):
        inputs, labels = data
        inputs, labels = inputs.to(device), labels.to(device)
        with torch.no_grad():
            outputs = model(inputs)
            loss = loss_fn(outputs, labels)
            mean_loss = loss.item()

        cum_loss += mean_loss * inputs.shape[0]
        correct += (outputs.argmax(1) == labels).sum().item()
    avg_loss = cum_loss / len(loader.dataset)
    accuracy = correct / len(loader.dataset)
    return avg_loss, accuracy

def predict(
        model: nn.Module,
        loader: DataLoader,
        device: torch.device,
) -> tuple[list[int], list[int]]:
    model = model.to(device)
    model.train(False)

    all_y_pred = []
    all_y_true = []

    with torch.no_grad():
        for inputs, labels in loader:
            inputs = inputs.to(device)
            labels = labels.to(device)

            outputs = model(inputs)
            preds = outputs.argmax(dim=1)

            all_y_pred.extend(preds.cpu().numpy())
            all_y_true.extend(labels.cpu().numpy())

    return all_y_pred, all_y_true

@dataclass
class Result:
    train_acc: float
    train_loss: float
    val_acc: float
    val_loss: float

@dataclass
class Experiment:
    model_name: str
    model_cls: type[nn.Module]
    kwargs: dict = field(default_factory = dict)
    progress: list[Result] = field(default_factory = list)
