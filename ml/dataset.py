import torch
from torch.utils.data import random_split, DataLoader, Subset
from torchvision import transforms
from torchvision.datasets import ImageFolder

transform = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor(),
    transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5)),
])

def get_dataloaders(
    data_dir: str,
    batch_size: tuple[int, int, int]
):
    full_dataset = ImageFolder(root=data_dir, transform=transform)

    generator = torch.Generator().manual_seed(42)
    train_data, val_data, test_data = random_split(full_dataset, [0.6, 0.2, 0.2], generator=generator)

    train_loader = DataLoader(train_data, batch_size=batch_size[0], shuffle=True)
    val_loader = DataLoader(val_data, batch_size=batch_size[1], shuffle=False)
    test_loader = DataLoader(test_data, batch_size=batch_size[2], shuffle=False)

    return train_loader, val_loader, test_loader
