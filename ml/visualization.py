from ml.train import Result
import matplotlib.pyplot as plt

def plot_results(progress: list[Result], model_name: str, epoch_start: int = 0) -> None:
    epochs = range(1 + epoch_start, len(progress) + 1)
    progress = progress[epoch_start:]
    train_losses = [r.train_loss for r in progress]
    train_accs = [r.train_acc * 100 for r in progress]
    val_losses = [r.val_loss for r in progress]
    val_accs = [r.val_acc * 100 for r in progress]

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize = (14, 5))

    ax1.plot(epochs, train_losses, label = "Train Loss", linestyle = "--", marker = "o")
    ax1.plot(epochs, val_losses, label = "Val Loss")
    ax1.set_xlabel("Epochs")
    ax1.set_ylabel("Loss")
    ax1.set_title(f"{model_name} Loss Curve")
    ax1.grid(linestyle = ":")
    ax1.legend()

    ax2.plot(epochs, train_accs, label = "Train Accuracy", linestyle = "--", marker = "o")
    ax2.plot(epochs, val_accs, label = "Val Accuracy")
    ax2.set_xlabel("Epochs")
    ax2.set_ylabel("Accuracy [%]")
    ax2.set_title(f"{model_name} Accuracy Curve")
    ax2.grid(linestyle = ":")
    ax2.legend()

    plt.tight_layout()
    plt.show()
