package ru.it.timeflow.presentation.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.it.timeflow.presentation.analytics.WeekChangeDirection
import ru.it.timeflow.presentation.analytics.WeekComparisonItem
import ru.it.timeflow.util.formatDurationCompact
import java.util.Locale
import kotlin.math.abs

@Composable
fun WeekComparisonSection(
    items: List<WeekComparisonItem>,
    summary: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text =
                "Эта неделя против прошлой",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold,
        )

        Text(
            text =
                "Сравнивается одинаковый отрезок недели.",
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
        )

        if (items.isEmpty()) {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(20.dp),
            ) {
                Text(
                    text =
                        "Пока недостаточно данных для сравнения.",
                    modifier =
                        Modifier.padding(18.dp),
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )
            }
            return@Column
        }

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(22.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .surfaceContainer,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp),
            ) {
                items.forEach {
                    WeekComparisonRow(it)
                }
            }
        }

        summary?.let {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer,
                    ),
            ) {
                Column(
                    modifier =
                        Modifier.padding(18.dp),
                ) {
                    Text(
                        text =
                            "Короткий вывод",
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Spacer(
                        Modifier.size(6.dp)
                    )

                    Text(
                        text = it,
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        fontWeight =
                            FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekComparisonRow(
    item: WeekComparisonItem,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .background(
                        Color(
                            item.colorArgb
                        ).copy(alpha = 0.15f),
                        CircleShape,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Text(item.emoji)
        }

        Spacer(
            Modifier.size(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f),
        ) {
            Text(
                text = item.name,
                fontWeight =
                    FontWeight.SemiBold,
            )

            Text(
                text =
                    "${
                        formatDurationCompact(
                            item.previousWeekMillis
                        )
                    }  →  ${
                        formatDurationCompact(
                            item.currentWeekMillis
                        )
                    }",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )
        }

        ChangeBadge(item)
    }
}

@Composable
private fun ChangeBadge(
    item: WeekComparisonItem,
) {
    val tint =
        when (item.direction) {
            WeekChangeDirection.UP,
            WeekChangeDirection.NEW ->
                MaterialTheme
                    .colorScheme
                    .primary

            WeekChangeDirection.DOWN ->
                MaterialTheme
                    .colorScheme
                    .error

            WeekChangeDirection.SAME ->
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        }

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(3.dp),
    ) {
        when (item.direction) {
            WeekChangeDirection.UP ->
                Icon(
                    Icons.Default.ArrowUpward,
                    contentDescription = null,
                    modifier =
                        Modifier.size(17.dp),
                    tint = tint,
                )

            WeekChangeDirection.DOWN ->
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier =
                        Modifier.size(17.dp),
                    tint = tint,
                )

            WeekChangeDirection.SAME ->
                Icon(
                    Icons.Default.Remove,
                    contentDescription = null,
                    modifier =
                        Modifier.size(17.dp),
                    tint = tint,
                )

            WeekChangeDirection.NEW ->
                Unit
        }

        Text(
            text =
                changeText(item),
            fontWeight =
                FontWeight.Bold,
            color = tint,
        )
    }
}

private fun changeText(
    item: WeekComparisonItem
): String =
    when (item.direction) {
        WeekChangeDirection.NEW ->
            "Новое"

        WeekChangeDirection.SAME ->
            "0%"

        WeekChangeDirection.UP,
        WeekChangeDirection.DOWN -> {
            val percent =
                item.changePercent
                    ?: return "—"

            String.format(
                Locale.US,
                "%.0f%%",
                abs(percent),
            )
        }
    }
