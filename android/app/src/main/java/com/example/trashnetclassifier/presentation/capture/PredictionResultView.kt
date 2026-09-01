package com.example.trashnetclassifier.presentation.capture

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trashnetclassifier.domain.model.ClassPrediction
import com.example.trashnetclassifier.domain.model.ModelResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PredictionResultView(
    state: CaptureUiState.ClassificationResult,
    onConfirmPrediction: () -> Unit,
    onCorrectLabelSelected: (String) -> Unit,
    onScanAgain: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Image(
                bitmap = state.bitmap.asImageBitmap(),
                contentDescription = "Captured Waste",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Prediction Results",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        state.modelResults.forEach { modelResult ->
            ModelResultCard(modelResult)
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(24.dp))
        PredictionUserFeedBackView(
            state = state,
            onConfirmPrediction = onConfirmPrediction,
            onCorrectLabelSelected = onCorrectLabelSelected,
            onScanAgain = onScanAgain,
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ModelResultCard(
    modelResult: ModelResult,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by remember { mutableStateOf(false) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Memory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = modelResult.modelName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val n = if (expanded) modelResult.results.size else 2
            modelResult.results.take(n = n).forEachIndexed { index, prediction ->
                ClassPredictionProgressIndicator(index, prediction)
            }
        }
    }
}

@Composable
private fun ClassPredictionProgressIndicator(
    index: Int,
    prediction: ClassPrediction,
) {
    val isTopPrediction = index == 0

    Column(modifier = Modifier.padding(bottom = if (isTopPrediction) 6.dp else 0.dp)) {
        if (!isTopPrediction) {
            Spacer(Modifier.size(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = prediction.className,
                fontWeight = if (isTopPrediction) FontWeight.Bold else FontWeight.Normal,
                color = if (isTopPrediction) MaterialTheme.colorScheme.onSurface else Color.Gray,
            )
            Text(
                text = "${(prediction.confidence * 100).toInt()}%",
                fontWeight = if (isTopPrediction) FontWeight.Bold else FontWeight.Normal,
                color = if (isTopPrediction) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { prediction.confidence.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTopPrediction) 8.dp else 4.dp)
                .clip(RoundedCornerShape(50)),
            color = if (isTopPrediction) MaterialTheme.colorScheme.primary else Color.LightGray,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        )
    }
}

@Composable
private fun PredictionUserFeedBackView(
    state: CaptureUiState.ClassificationResult,
    onConfirmPrediction: () -> Unit,
    onCorrectLabelSelected: (String) -> Unit,
    onScanAgain: () -> Unit,
) {
    var isWrong by remember { mutableStateOf(false) }
    val submitted = state.correctClass != null

    if (submitted) {
        UserConfirmedView(
            isWrong = isWrong,
            onScanAgain = onScanAgain,
        )
    } else if (!isWrong) {
        WaitingForUserConfirmationView(
            mostTrustedClassName = state.mostTrustedClassName,
            onSetIsWrong = {
                isWrong = true
            },
            onConfirmPrediction = onConfirmPrediction,
        )
    } else {
        val trashNetClasses = state.modelResults.firstOrNull()?.results?.map { it.className } ?: emptyList()
        WaitingForUserCorrectionView(
            trashNetClasses = trashNetClasses,
            mostTrustedClassName = state.mostTrustedClassName,
            onCorrectLabelSelected = onCorrectLabelSelected,
        )
    }
}

@Composable
private fun UserConfirmedView(
    isWrong: Boolean,
    onScanAgain: () -> Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Text(
            text = if (!isWrong) "Thank you for your confirmation!" else "Thank you for your correction!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onScanAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Another Item", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WaitingForUserConfirmationView(
    mostTrustedClassName: String,
    onSetIsWrong: () -> Unit,
    onConfirmPrediction: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Is this ${mostTrustedClassName}?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onSetIsWrong() },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Incorrect")
            }

            Button(
                onClick = {
                    onConfirmPrediction()
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Correct")
            }
        }
    }
}

@Composable
private fun WaitingForUserCorrectionView(
    trashNetClasses: List<String>,
    mostTrustedClassName: String,
    onCorrectLabelSelected: (String) -> Unit,
) {
    var selectedLabel by remember { mutableStateOf<String?>(null) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "What is the actual material?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            trashNetClasses.forEach { actualClass ->
                val isOriginalWrong = actualClass.equals(mostTrustedClassName, ignoreCase = true)
                val isSelected = selectedLabel.equals(actualClass, ignoreCase = true)

                FilterChip(
                    selected = isSelected,
                    enabled = !isOriginalWrong,
                    onClick = { selectedLabel = actualClass },
                    label = { Text(actualClass) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedLabel?.let { label ->
                    onCorrectLabelSelected(label.lowercase())
                }
            },
            enabled = selectedLabel != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Rounded.CloudUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submit Correction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
