package ru.it.timeflow.presentation.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.it.timeflow.presentation.analytics.AnalyticsCategoryItem
import ru.it.timeflow.util.formatDurationCompact

@Composable
fun TimeDistributionPieChart(
    items: List<AnalyticsCategoryItem>,
    totalMillis: Long,
    modifier: Modifier = Modifier,
) {
    val emptyColor =
        MaterialTheme
            .colorScheme
            .surfaceVariant

    val centerColor =
        MaterialTheme
            .colorScheme
            .surface

    Box(
        modifier =
            modifier.size(250.dp),
        contentAlignment =
            Alignment.Center,
    ) {

        Canvas(
            modifier =
                Modifier.fillMaxSize(),
        ) {
            val strokeWidth =
                34.dp.toPx()

            val diameter =
                size.minDimension -
                        strokeWidth

            val topLeft =
                Offset(
                    x =
                        (
                                size.width -
                                        diameter
                                ) / 2f,
                    y =
                        (
                                size.height -
                                        diameter
                                ) / 2f,
                )

            val arcSize =
                Size(
                    width =
                        diameter,
                    height =
                        diameter,
                )

            if (
                items.isEmpty() ||
                totalMillis <= 0L
            ) {
                drawArc(
                    color =
                        emptyColor,
                    startAngle =
                        -90f,
                    sweepAngle =
                        360f,
                    useCenter =
                        false,
                    topLeft =
                        topLeft,
                    size =
                        arcSize,
                    style =
                        Stroke(
                            width =
                                strokeWidth,
                            cap =
                                StrokeCap.Round,
                        ),
                )
            } else {
                var startAngle =
                    -90f

                items.forEach { item ->

                    val sweep =
                        360f *
                                (
                                        item.percentage /
                                                100f
                                        )

                    if (sweep > 0f) {
                        drawArc(
                            color =
                                Color(
                                    item.colorArgb
                                ),
                            startAngle =
                                startAngle,
                            sweepAngle =
                                sweep,
                            useCenter =
                                false,
                            topLeft =
                                topLeft,
                            size =
                                arcSize,
                            style =
                                Stroke(
                                    width =
                                        strokeWidth,
                                    cap =
                                        StrokeCap.Butt,
                                ),
                        )

                        startAngle +=
                            sweep
                    }
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .size(142.dp)
                    .background(
                        color =
                            centerColor,
                        shape =
                            CircleShape,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
            ) {
                Text(
                    text =
                        "Всего",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )

                Text(
                    text =
                        formatDurationCompact(
                            totalMillis
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    fontWeight =
                        FontWeight.Bold,
                )
            }
        }
    }
}
