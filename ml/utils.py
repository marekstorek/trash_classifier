import json
from dataclasses import asdict

from ml.train import Experiment, Result

def save_experiments(
        experiments: list[Experiment],
        path: str = "experiments.json",
):
    data = []
    for exp in experiments:
        data.append(
            {
                "model_name" : exp.model_name,
                "model_cls" : exp.model_cls.__name__ if exp.model_cls is not None else None,
                "kwargs" : exp.kwargs,
                "progress" : [
                    asdict(r) for r in exp.progress
                ],
            }
        )

    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent = 4)
    print(f"{len(experiments)} Experiments saved to {path} successfully")

def load_experiments(
        path: str = "experiments.json",
) -> list[Experiment]:
    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)
    experiments = []
    for item in data:
        progress = [Result(**r) for r in item["progress"]]
        exp = Experiment(
            model_name = item["model_name"],
            model_cls = None,
            kwargs = item["kwargs"],
            progress = progress,
        )
        experiments.append(exp)
    print(f"{len(experiments)} Experiments loaded from {path} successfully")
    return experiments
