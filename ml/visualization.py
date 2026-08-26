from ml.train import Result, Experiment
import matplotlib.pyplot as plt
import pandas as pd

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

def compare_models(
        experiments: list[Experiment],
        show_train: bool = False,
        epoch_start: int = 0,
        title: str = None
) -> None:

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize = (14, 5))
    if title is not None:
        title = title if not show_train else f"{title}\n Full line - validation, Dotted line - training"
        fig.suptitle(title, fontsize = 15)



    for exp in experiments:
        epochs = range(1 + epoch_start, len(exp.progress) + 1)
        progress = exp.progress[epoch_start:]
        train_losses = [r.train_loss for r in progress]
        train_accs = [r.train_acc * 100 for r in progress]
        val_losses = [r.val_loss for r in progress]
        val_accs = [r.val_acc * 100 for r in progress]

        best_acc = max(val_accs)
        label = f"{exp.model_name}  ({best_acc:.2f}%)"

        line = ax1.plot(epochs, val_losses, label = label, linestyle = "-", marker = "o")[0]
        color = line.get_color()
        ax2.plot(epochs, val_accs, label = label, linestyle = "-", marker = "o", color = color)

        if show_train:
            ax1.plot(epochs, train_losses, linestyle = "--", alpha = 0.5, color = color)
            ax2.plot(epochs, train_accs, linestyle = "--", alpha = 0.5, color = color)

    ax1.set_title("Validation Loss Comparison")
    ax1.set_xlabel("Epochs")
    ax1.set_ylabel("Loss")
    ax1.grid(linestyle = ":")
    ax1.legend(framealpha = 0.5)

    ax2.set_title("Validation Accuracy Comparison")
    ax2.set_xlabel("Epochs")
    ax2.set_ylabel("Accuracy [%]")
    ax2.grid(linestyle = ":")
    ax2.legend(framealpha = 0.5)

    plt.tight_layout()
    plt.show()


def summarize_results(
        experiments: list[Experiment]
) -> pd.DataFrame:
    summary = []
    for exp in experiments:
        name = getattr(exp, "model_name", getattr(exp, "name", "Model"))
        summary.append(
            {
                "Model": name,
                "Best Val Acc [%]": round(max(r.val_acc for r in exp.progress) * 100, 2),
                "Min Val Loss": round(min(r.val_loss for r in exp.progress), 4),
                "Final Train Loss": round(exp.progress[-1].train_loss, 4),
                "Final Train Acc [%]": round(exp.progress[-1].train_acc * 100, 2),
                "Epochs": len(exp.progress),
            }
        )
    df_results = pd.DataFrame(summary).sort_values(by="Best Val Acc [%]", ascending=False)
    return df_results
