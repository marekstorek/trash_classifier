from ml.train import Result, Experiment
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay

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

        line = ax1.plot(epochs, val_losses, label = label, linestyle = "-", marker = ".")[0]
        color = line.get_color()
        ax2.plot(epochs, val_accs, label = label, linestyle = "-", marker = ".", color = color)

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

def plot_dropout_visualization(
        experiments_bn: list[Experiment],
        experiments_no_bn: list[Experiment],
        title: str = "Comparison of Dropout Probability\non models with and without Batch Normalization"
) -> None:
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize = (14, 5))
    fig.suptitle(title, fontsize = 15)

    ps_BN = [exp.kwargs.get("p") for exp in experiments_bn]
    val_losses_BN = [min(r.val_loss for r in exp.progress) for exp in experiments_bn]
    val_accs_BN = [max(r.val_acc for r in exp.progress) * 100 for exp in experiments_bn]

    ps_no_BN = [exp.kwargs.get("p") for exp in experiments_no_bn]
    val_losses_no_BN = [min(r.val_loss for r in exp.progress) for exp in experiments_no_bn]
    val_accs_no_BN = [max(r.val_acc for r in exp.progress) * 100 for exp in experiments_no_bn]

    ax1.plot(ps_BN, val_losses_BN, label = "with BN", linestyle = "-", marker = "o")
    ax1.plot(ps_no_BN, val_losses_no_BN, label = "without BN", linestyle = "-", marker = "o")
    ax1.set_xlabel("p (Dropout Probability)")
    ax1.set_ylabel("Min Val Loss")
    ax1.set_title("Dropout Probability vs Min Val Loss")
    ax1.grid(linestyle = ":")
    ax1.legend(framealpha = 0.5)

    ax2.plot(ps_BN, val_accs_BN, label = "with BN", linestyle = "-", marker = "o")
    ax2.plot(ps_no_BN, val_accs_no_BN, label = "without BN", linestyle = "-", marker = "o")
    ax2.set_xlabel("p (Dropout Probability)")
    ax2.set_ylabel("Max Val Accuracy [%]")
    ax2.set_title("Dropout Probability vs Max Val Accuracy")
    ax2.grid(linestyle = ":")
    ax2.legend(framealpha = 0.5)

    plt.tight_layout()
    plt.show()

def plot_confusion_matrix(y_true, y_pred, class_names, title: str = "Confusion Matrix"):
    fig, ax = plt.subplots(figsize=(7, 5))
    cm = confusion_matrix(y_true, y_pred)
    disp = ConfusionMatrixDisplay(
        confusion_matrix=cm,
        display_labels=class_names
    )
    disp.plot(ax=ax)
    fig.suptitle(title)
    plt.tight_layout()
    plt.show()
