package com.example.trashnetclassifier.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.trashnetclassifier.data.local.Experiment
import com.example.trashnetclassifier.data.local.ExperimentState
import com.example.trashnetclassifier.domain.model.mostTrustedModelResult
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onExperimentClick: (Long) -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val experiments by viewModel.allExperiments.collectAsState()

    Scaffold(
        topBar = {
            TopBar(onClick = onBack)
        }
    ) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            if (experiments.isEmpty()) {
                Text(
                    text = "No experiments logged",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = experiments,
                        key = { it.id }
                    ) { experiment ->
                        ExperimentRowItem(
                            experiment = experiment,
                            onClick = { onExperimentClick.invoke(experiment.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Experiment History", fontWeight = FontWeight.SemiBold)
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.ArrowBackIosNew, "Back")
            }
        }
    )
}

@Composable
private fun ExperimentRowItem(
    experiment: Experiment,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(experiment.localImagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Waste Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = experiment.correctClass?.replaceFirstChar { it.uppercase() }
                        ?: experiment.modelResults?.mostTrustedModelResult()?.bestResult?.className
                        ?: "Awaiting Confirmation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                val subtitle = buildSubtitle(experiment)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTimestamp(experiment.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            ExperimentStateBadge(state = experiment.state)
        }
    }
}

private fun buildSubtitle(experiment: Experiment): String {
    val models = experiment.modelResults
    if (models.isNullOrEmpty()) return "No model inference"

    val topModel = models.mostTrustedModelResult()
    val topPred = topModel.bestResult
    val confPercent = (topPred.confidence * 100).toInt()

    return "${topModel.modelName}: ${topPred.className} ($confPercent%)"
}

private data class StateBadgeConfig(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector
)

@Composable
private fun ExperimentStateBadge(
    state: ExperimentState,
    modifier: Modifier = Modifier
) {
    val config = when (state) {
        ExperimentState.Correct -> StateBadgeConfig(
            label = "Correct",
            containerColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF2E7D32),
            icon = Icons.Default.DoneAll
        )
        ExperimentState.OneModelWasRight -> StateBadgeConfig(
            label = "Partial match",
            containerColor = Color(0xFFFFF3E0),
            contentColor = Color(0xFFE65100),
            icon = Icons.Default.Check
        )
        ExperimentState.Wrong -> StateBadgeConfig(
            label = "Wrong",
            containerColor = Color(0xFFFFEBEE),
            contentColor = Color(0xFFC62828),
            icon = Icons.Default.Close
        )
        ExperimentState.Unconfirmed -> StateBadgeConfig(
            label = "Pending",
            containerColor = Color(0xFFE3F2FD),
            contentColor = Color(0xFF1565C0),
            icon = Icons.Default.HourglassEmpty
        )
        ExperimentState.FailedToPredict -> StateBadgeConfig(
            label = "Failed",
            containerColor = Color(0xFFEEEEEE),
            contentColor = Color(0xFF616161),
            icon = Icons.Default.WarningAmber
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = config.containerColor,
        contentColor = config.contentColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = config.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("d. M. yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
