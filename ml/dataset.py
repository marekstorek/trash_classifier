import torch
from torch.utils.data import random_split, DataLoader, Subset
from torchvision import transforms
from torchvision.datasets import ImageFolder
from config import IMAGE_SIZE, DATA_DIR, BATCH_SIZE

basic_transform = transforms.Compose([
    transforms.Resize(IMAGE_SIZE),
    transforms.ToTensor(),
    transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5)),
])

augment_transform = transforms.Compose([
    transforms.RandomResizedCrop(IMAGE_SIZE, scale=(0.8, 1.0)),
    transforms.RandomHorizontalFlip(p=0.5),
    transforms.RandomVerticalFlip(p=0.5),
    transforms.RandomRotation(degrees=180),

    transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.01, ),

    transforms.ToTensor(),
    transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))
])

def get_dataloaders(
    data_dir: str = DATA_DIR,
    batch_size: tuple[int, int, int] = BATCH_SIZE,
    use_data_augmentation: bool = False,
):
    if use_data_augmentation:
        train_transform = augment_transform
    else:
        train_transform = basic_transform
    val_transform = basic_transform

    full_dataset = ImageFolder(root=data_dir, transform=train_transform)
    val_test_dataset = ImageFolder(root=data_dir, transform=val_transform)

    generator = torch.Generator().manual_seed(42)
    train_data, val_data, test_data = random_split(full_dataset, [0.6, 0.2, 0.2], generator=generator)
    val_data = Subset(val_test_dataset, val_data.indices)
    test_data = Subset(val_test_dataset, test_data.indices)

    train_loader = DataLoader(train_data, batch_size=batch_size[0], shuffle=True, generator=generator)
    val_loader = DataLoader(val_data, batch_size=batch_size[1], shuffle=False, generator=generator)
    test_loader = DataLoader(test_data, batch_size=batch_size[2], shuffle=False, generator=generator)

    return train_loader, val_loader, test_loader
