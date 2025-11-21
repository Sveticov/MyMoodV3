 package com.svetikov.mymood.ui.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.svetikov.mymood.data.model.MoodSegment
import com.svetikov.mymood.viewmodel.ActionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
fun ActionLogScreen(modifier: Modifier = Modifier, viewModel: ActionViewModel = hiltViewModel()) {

    val listMoodSegments by viewModel.listMoodSegment.collectAsState()
    val emojiWin by viewModel.emojiWin.collectAsState()
    var descriptionEmoji by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var some by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6366F1), Color(0xFF4CAF50))
                )
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(top = 12.dp))
        DateNavigator(modifier = Modifier.padding(top = 45.dp), viewModel = viewModel)

        Spacer(modifier = Modifier.padding(top = 100.dp))

        DonutChat(listMoodSegments) { CenterEmoji(label = emojiWin) }

        Spacer(modifier = Modifier.padding(top = 100.dp))
        Text(descriptionEmoji, fontSize = 22.sp, color = Color.White)
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 50.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(listMoodSegments) { it ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clickable {
                            scope.launch {
                                descriptionEmoji = viewModel.descriptionEmoji(it.label)
                                delay(1500)
                                descriptionEmoji = ""
                            }

                        }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .background(it.color)
                            .padding(end = 2.dp)
                    )
                    Text(it.label, Modifier.padding(2.dp), fontSize = 22.sp)
                    Text(
                        "${(it.percentage * 100).roundToInt()} %",
                        Modifier.padding(2.dp),
                        fontSize = 22.sp
                    )
                }
            }
        }


    }
}


@Composable
fun DonutChat(
    segments: List<MoodSegment>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 250.dp,
    strokeWidth: Dp = 55.dp,
    centerContent: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .size(chartSize)
            .drawBehind {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = (size.minDimension / 2),
                    center = center,
                    style = Fill,
                    // Blur через RenderEffect (Android 12+)
                    blendMode = BlendMode.SrcOver,
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val totalSize = size.width
            val sweepAngleCoefficient = 360f
            val rectSize = Size(totalSize, totalSize)
            var currentStartAngle = 0f

            segments.reversed().forEach { segment ->
                val sweepAngle = segment.percentage * sweepAngleCoefficient

                drawArc(
                    color = segment.color,
                    startAngle = currentStartAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = rectSize,
                    style = Stroke(width = strokeWidth.toPx())
                )
                currentStartAngle += sweepAngle
            }
        }

        centerContent()
    }
}

@Composable
fun CenterEmoji(
    modifier: Modifier = Modifier,
    label: String = "😀",
    viewModel: ActionViewModel = hiltViewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            label, fontSize = 48.sp, style = TextStyle(
                fontSize = 22.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(4f, 4f),
                    blurRadius = 6f
                )
            )
        )
        Text(viewModel.descriptionEmoji(label), color = Color.White)
    }

}

@Composable
fun DateNavigator(modifier: Modifier = Modifier, viewModel: ActionViewModel) {
    val minDate by viewModel.minDate.collectAsState()
    val maxDate by viewModel.maxDate.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    val min = minDate?.toLocalDate()
    val max = maxDate?.toLocalDate()

    val prevEnabled = min != null && selectedDate > min
    val nextEnabled = max != null && selectedDate < max

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            enabled = prevEnabled,
            onClick = {
                selectedDate = selectedDate.minusDays(1)
                if (min != null && selectedDate < min) selectedDate = min!!
                viewModel.getDate(selectedDate.format(dateFormatter))
            }, contentPadding = PaddingValues(16.dp)
        ) {
            Text("<")
        }
        Text(
            text = selectedDate.format(dateFormatter),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Button(
            enabled = nextEnabled,
            onClick = {
                selectedDate = selectedDate.plusDays(1)
                if (max != null && selectedDate > max) selectedDate = max!!
                viewModel.getDate(selectedDate.format(dateFormatter))
            }, contentPadding = PaddingValues(16.dp)
        ) {
            Text(">")
        }
    }

}