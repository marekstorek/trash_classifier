from torch import nn
import torch.nn.functional as F
from torchvision.models import ResNet18_Weights, resnet18
from ml.config import IMAGE_SIZE, NUM_CLASSES

class Conv1Layer(nn.Module):
    def __init__(self, n1: int, kernel_size: int = 3):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.fco = nn.Linear(n1 * (IMAGE_SIZE[0] // 2) * (IMAGE_SIZE[1] // 2), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)
        x = x.flatten(1)
        x = self.fco(x)
        return x

class Conv2Layer(nn.Module):
    def __init__(self, n1:int, n2:int, kernel_size: int = 3):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.fco = nn.Linear(n2 * (IMAGE_SIZE[0] // 4) * (IMAGE_SIZE[1] // 4), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)

        x = F.relu(self.conv2(x))
        x = self.pool2(x)
        x = x.flatten(1)

        x = self.fco(x)
        return x

class Conv3Layer(nn.Module):
    def __init__(self, n1:int, n2:int, n3:int, kernel_size: int = 3):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv3 = nn.Conv2d(in_channels=n2, out_channels=n3, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool3 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.fco = nn.Linear(n3 * (IMAGE_SIZE[0] // 8) * (IMAGE_SIZE[1] // 8), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)

        x = F.relu(self.conv2(x))
        x = self.pool2(x)

        x = F.relu(self.conv3(x))
        x = self.pool3(x)

        x = x.flatten(1)
        x = self.fco(x)
        return x


class Conv4Layer(nn.Module):
    def __init__(self, n1: int, n2: int, n3: int, n4: int, kernel_size: int = 3):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv3 = nn.Conv2d(in_channels=n2, out_channels=n3, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool3 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv4 = nn.Conv2d(in_channels=n3, out_channels=n4, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool4 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.fco = nn.Linear(n4 * (IMAGE_SIZE[0] // 16) * (IMAGE_SIZE[1] // 16), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)

        x = F.relu(self.conv2(x))
        x = self.pool2(x)

        x = F.relu(self.conv3(x))
        x = self.pool3(x)

        x = F.relu(self.conv4(x))
        x = self.pool4(x)

        x = x.flatten(1)
        x = self.fco(x)
        return x

class Conv5Layer(nn.Module):
    def __init__(self, n1: int, n2: int, n3: int, n4: int, n5: int, kernel_size: int = 3):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv3 = nn.Conv2d(in_channels=n2, out_channels=n3, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool3 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv4 = nn.Conv2d(in_channels=n3, out_channels=n4, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool4 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv5 = nn.Conv2d(in_channels=n4, out_channels=n5, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool5 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.fco = nn.Linear(n5 * (IMAGE_SIZE[0] // 32) * (IMAGE_SIZE[1] // 32), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)

        x = F.relu(self.conv2(x))
        x = self.pool2(x)

        x = F.relu(self.conv3(x))
        x = self.pool3(x)

        x = F.relu(self.conv4(x))
        x = self.pool4(x)

        x = F.relu(self.conv5(x))
        x = self.pool5(x)

        x = x.flatten(1)
        x = self.fco(x)
        return x

class Conv4LayerDropout(nn.Module):
    def __init__(self, n1:int, n2:int, n3:int, n4:int, kernel_size: int = 3, p: float = 0.0):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv3 = nn.Conv2d(in_channels=n2, out_channels=n3, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool3 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv4 = nn.Conv2d(in_channels=n3, out_channels=n4, kernel_size=kernel_size, padding=kernel_size // 2)
        self.pool4 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.dropout = nn.Dropout(p=p)
        self.fco = nn.Linear(n4 * (IMAGE_SIZE[0] // 16) * (IMAGE_SIZE[1] // 16), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool1(x)

        x = F.relu(self.conv2(x))
        x = self.pool2(x)

        x = F.relu(self.conv3(x))
        x = self.pool3(x)

        x = F.relu(self.conv4(x))
        x = self.pool4(x)

        x = x.flatten(1)
        x = self.dropout(x)
        x = self.fco(x)
        return x

class Conv4LayerDropoutBN(nn.Module):
    def __init__(self, n1:int, n2:int, n3:int, n4:int, kernel_size: int = 3, p: float = 0.0):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=n1, kernel_size=kernel_size, padding=kernel_size // 2)
        self.bn1 = nn.BatchNorm2d(num_features=n1)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv2 = nn.Conv2d(in_channels=n1, out_channels=n2, kernel_size=kernel_size, padding=kernel_size // 2)
        self.bn2 = nn.BatchNorm2d(num_features=n2)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv3 = nn.Conv2d(in_channels=n2, out_channels=n3, kernel_size=kernel_size, padding=kernel_size // 2)
        self.bn3 = nn.BatchNorm2d(num_features=n3)
        self.pool3 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.conv4 = nn.Conv2d(in_channels=n3, out_channels=n4, kernel_size=kernel_size, padding=kernel_size // 2)
        self.bn4 = nn.BatchNorm2d(num_features=n4)
        self.pool4 = nn.MaxPool2d(kernel_size=2, stride=2)

        self.dropout = nn.Dropout(p=p)
        self.fco = nn.Linear(n4 * (IMAGE_SIZE[0] // 16) * (IMAGE_SIZE[1] // 16), NUM_CLASSES)

    def forward(self, x):
        x = F.relu(self.bn1(self.conv1(x)))
        x = self.pool1(x)

        x = F.relu(self.bn2(self.conv2(x)))
        x = self.pool2(x)

        x = F.relu(self.bn3(self.conv3(x)))
        x = self.pool3(x)

        x = F.relu(self.bn4(self.conv4(x)))
        x = self.pool4(x)

        x = x.flatten(1)
        x = self.dropout(x)
        x = self.fco(x)
        return x

class MyResNet(nn.Module):
    def __init__(self, p: float):
        super().__init__()
        weights = ResNet18_Weights.DEFAULT
        self.model = resnet18(weights=weights)

        for param in self.model.parameters():
            param.requires_grad = False

        in_features: int = self.model.fc.in_features
        self.model.fc = nn.Sequential(
            nn.Dropout(p=p), nn.Linear(in_features, NUM_CLASSES)
        )

    def forward(self, x):
        return self.model(x)
    