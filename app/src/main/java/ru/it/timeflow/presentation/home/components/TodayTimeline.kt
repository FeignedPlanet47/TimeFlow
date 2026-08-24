package ru.it.timeflow.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.it.timeflow.presentation.home.timeline.TimelineItem
import ru.it.timeflow.util.formatClockTime
import ru.it.timeflow.util.formatDurationCompact

@Composable
fun TodayTimeline(
    items: List<TimelineItem>,
    nowMillis: Long,
    onGapClick: (TimelineItem.Gap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(0.dp),
    ) {

        if (items.isEmpty()) {
            Text(
                text =
                    "Сегодня пока нет временных промежутков для отображения.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }

        items.forEachIndexed { index, item ->

            when (item) {

                is TimelineItem.Entry ->
                    TimelineEntryRow(
                        item = item,
                        nowMillis = nowMillis,
                    )

                is TimelineItem.Gap ->
                    TimelineGapRow(
                        gap = item,
                        onClick = {
                            onGapClick(item)
                        },
                    )
            }

            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier =
                        Modifier.padding(
                            start = 76.dp,
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                            .copy(alpha = 0.35f),
                )
            }
        }
    }
}

@Composable
private fun TimelineEntryRow(
    item: TimelineItem.Entry,
    nowMillis: Long,
) {
    val entry = item.entry

    TimelineRow(
        startMillis = item.startMillis,
        endMillis = item.endMillis,
        dotColor =
            Color(
                entry.categoryColorArgb
            ),
    ) {

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(18.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color(
                            entry.categoryColorArgb
                        ).copy(alpha = 0.12f),
                ),
        ) {
            Column(
                modifier =
                    Modifier.padding(14.dp),
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            entry.categoryEmoji,
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            entry.categoryName,
                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Spacer(
                        Modifier.weight(1f)
                    )

                    Text(
                        text =
                            formatDurationCompact(
                                (
                                        item.endMillis -
                                                item.startMillis
                                        ).coerceAtLeast(
                                        0L
                                    )
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        fontWeight =
                            FontWeight.SemiBold,
                    )
                }

                entry.taskName?.let {
                    Text(
                        text = it,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                    )
                }

                entry.note?.let {
                    Text(
                        text = it,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        maxLines = 3,
                    )
                }

                if (entry.isRunning) {
                    Text(
                        text = "Сейчас выполняется",
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineGapRow(
    gap: TimelineItem.Gap,
    onClick: () -> Unit,
) {
    TimelineRow(
        startMillis =
            gap.startMillis,
        endMillis =
            gap.endMillis,
        dotColor =
            MaterialTheme
                .colorScheme
                .outline,
    ) {

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
            shape =
                RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 16.dp,
                    ),
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primaryContainer,
                            CircleShape,
                        ),
                    contentAlignment =
                        Alignment.Center,
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Add,
                        contentDescription =
                            null,
                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary,
                    )
                }

                Spacer(
                    Modifier.width(10.dp)
                )

                Column {
                    Text(
                        text =
                            "Добавить занятие",
                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Text(
                        text =
                            "Пустой промежуток · ${
                                formatDurationCompact(
                                    gap.durationMillis
                                )
                            }",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    startMillis: Long,
    endMillis: Long,
    dotColor: Color,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 82.dp)
            .padding(vertical = 8.dp),
        verticalAlignment =
            Alignment.Top,
    ) {

        Column(
            modifier =
                Modifier.width(58.dp),
            horizontalAlignment =
                Alignment.End,
        ) {
            Text(
                text =
                    formatClockTime(
                        startMillis
                    ),
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                fontWeight =
                    FontWeight.SemiBold,
            )

            Text(
                text =
                    formatClockTime(
                        endMillis
                    ),
                style =
                    MaterialTheme
                        .typography
                        .labelSmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )
        }

        Spacer(
            Modifier.width(10.dp)
        )

        Column(
            modifier =
                Modifier.width(18.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        dotColor,
                        CircleShape,
                    )
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .heightIn(min = 62.dp)
                    .background(
                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                    )
            )
        }

        Spacer(
            Modifier.width(10.dp)
        )

        Box(
            modifier =
                Modifier.weight(1f),
        ) {
            content()
        }
    }
}
