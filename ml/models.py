from torch import nn
import torch.nn.functional as F
from config import IMAGE_SIZE, NUM_CLASSES

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
