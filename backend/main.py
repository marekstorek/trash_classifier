from ml.dataset import basic_transform
from ml.models import Conv4LayerDropout, MyResNet

import torch
import torch.nn as nn

from fastapi import FastAPI, UploadFile
from PIL import Image
from contextlib import asynccontextmanager
from pathlib import Path
import os, gc

BASIC_MODEL_NAME = "Conv4_32_64_128_256_Augment"
RESNET_MODEL_NAME = "ResNet_p=0.0"

PROJECT_ROOT = Path(__file__).resolve().parent.parent
PTH_SAVE_DIR = PROJECT_ROOT / "ml" / "models" / "saved_models"

CLASS_NAMES = ["cardboard", "glass", "metal", "paper", "plastic", "trash"]

models_used: list[nn.Module] = []
models_used_names: list[str] = []
models_used_product_name: list[str] = []

@asynccontextmanager
async def lifespan(app: FastAPI):
    global models_used, models_used_names, models_used_product_name

    model_basic = load_model(BASIC_MODEL_NAME)
    model_resnet = load_model(RESNET_MODEL_NAME)
    models_used = [model_basic, model_resnet]
    models_used_names = [BASIC_MODEL_NAME, RESNET_MODEL_NAME]
    models_used_product_name = ["Basic", "ResNet16"]

    yield

    del model_basic
    del model_resnet
    models_used = []
    gc.collect()
    if torch.mps.is_available():
        torch.mps.empty_cache()
app = FastAPI(
    title="TrashNet Classification API",
    lifespan = lifespan
)

def get_model_path(model_name: str, model_dir = PTH_SAVE_DIR):
    return os.path.join(model_dir, f"{model_name}.pt")

def load_model(model_name: str) -> torch.nn.Module:
    if model_name == RESNET_MODEL_NAME:
        model_cls = MyResNet
        model_kwargs = {"p" : 0.0}
    elif model_name == BASIC_MODEL_NAME:
        model_cls = Conv4LayerDropout
        model_kwargs = {"n1" : 32,"n2" : 64, "n3" : 128, "n4" : 256, "p" : 0.5,}
    else:
        raise Exception(f"Unsupported model: {model_name}")

    model_path = get_model_path(model_name = model_name)

    model = model_cls(**model_kwargs)
    model.load_state_dict(torch.load(model_path))
    model.train(False)
    return model

@app.get("/")
def root():
    return {"Message": "TrashNet API runs!"}

@app.post("/predict")
async def predict(file: UploadFile) -> dict:
    img = Image.open(file.file).convert('RGB')
    x = basic_transform(img).unsqueeze(0)

    output = {}
    for i, model in enumerate(models_used):
        with torch.no_grad():
            y = model(x)
            probabilities = torch.nn.functional.softmax(y, dim=1).squeeze().tolist()
            result = {
                "probabilities": probabilities,
                "classes": CLASS_NAMES,
            }
            output[models_used_product_name[i]] = result
    return output
