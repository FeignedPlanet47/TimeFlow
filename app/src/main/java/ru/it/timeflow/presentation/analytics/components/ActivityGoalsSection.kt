package ru.it.timeflow.presentation.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.model.ActivityTargetType
import ru.it.timeflow.presentation.analytics.GoalProgressItem
import ru.it.timeflow.util.formatDurationCompact
import java.util.Locale

@Composable
fun ActivityGoalsSection(
    items:
    List<GoalProgressItem>,

    canAddTarget:
    Boolean,

    onAddTarget:
        () -> Unit,

    onEditTarget:
        (ActivityTarget) -> Unit,

    modifier:
    Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(
                12.dp
            ),
    ) {
        Text(
            text =
                "Цели и лимиты",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall,

            fontWeight =
                FontWeight.Bold,
        )

        Text(
            text =
                "Контролируйте, чему хотите уделять больше или меньше времени.",

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
        )

        if (
            items.isEmpty()
        ) {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            18.dp
                        ),
                ) {
                    Text(
                        text =
                            "Пока нет целей и лимитов.",

                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Spacer(
                        Modifier.height(
                            4.dp
                        )
                    )

                    Text(
                        text =
                            "Например: 5 часов спорта в неделю или не больше 7 часов игр.",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                    )
                }
            }
        } else {
            items.forEach {
                    item ->

                ActivityGoalCard(
                    item =
                        item,

                    onClick = {
                        onEditTarget(
                            item.target
                        )
                    },
                )
            }
        }

        OutlinedButton(
            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                canAddTarget,

            onClick =
                onAddTarget,
        ) {
            Text(
                if (canAddTarget) {
                    "+ Добавить цель или лимит"
                } else {
                    "Для всех категорий уже задано правило"
                }
            )
        }
    }
}

@Composable
private fun ActivityGoalCard(
    item:
    GoalProgressItem,

    onClick:
        () -> Unit,
) {
    val isLimit =
        item.target.type ==
                ActivityTargetType.LIMIT

    val isExceeded =
        isLimit &&
                item.exceededMillis > 0L

    val progressColor =
        if (isExceeded) {
            MaterialTheme
                .colorScheme
                .error
        } else {
            Color(
                item.categoryColorArgb
            )
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(
                20.dp
            ),

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
                    .padding(
                        16.dp
                    ),
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
                            .size(
                                44.dp
                            )
                            .background(
                                color =
                                    Color(
                                        item.categoryColorArgb
                                    ).copy(
                                        alpha =
                                            0.16f
                                    ),

                                shape =
                                    CircleShape,
                            ),

                    contentAlignment =
                        Alignment.Center,
                ) {
                    Text(
                        text =
                            item.categoryEmoji
                    )
                }

                Spacer(
                    Modifier.size(
                        12.dp
                    )
                )

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        ),
                ) {
                    Text(
                        text =
                            item.categoryName,

                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Text(
                        text =
                            "${item.target.type.title}: ${
                                formatDurationCompact(
                                    item.target.targetMillis
                                )
                            } ${item.target.period.shortTitle}",

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

                Text(
                    text =
                        formatPercent(
                            item.progressPercent
                        ),

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        progressColor,
                )
            }

            Spacer(
                Modifier.height(
                    14.dp
                )
            )

            Text(
                text =
                    "${
                        formatDurationCompact(
                            item.actualMillis
                        )
                    } из ${
                        formatDurationCompact(
                            item.target.targetMillis
                        )
                    }",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                fontWeight =
                    FontWeight.Medium,
            )

            Spacer(
                Modifier.height(
                    8.dp
                )
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            9.dp
                        )
                        .background(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant,

                            shape =
                                RoundedCornerShape(
                                    99.dp
                                ),
                        ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(
                                item.progressFraction
                            )
                            .height(
                                9.dp
                            )
                            .background(
                                color =
                                    progressColor,

                                shape =
                                    RoundedCornerShape(
                                        99.dp
                                    ),
                            ),
                )
            }

            Spacer(
                Modifier.height(
                    8.dp
                )
            )

            Text(
                text =
                    targetStatusText(
                        item
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    if (isExceeded) {
                        MaterialTheme
                            .colorScheme
                            .error
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    },

                fontWeight =
                    if (
                        isExceeded ||
                        (
                                !isLimit &&
                                        item.isReached
                                )
                    ) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
            )
        }
    }
}

private fun targetStatusText(
    item:
    GoalProgressItem,
): String =
    when (
        item.target.type
    ) {
        ActivityTargetType.GOAL -> {
            if (
                item.isReached
            ) {
                "Цель выполнена"
            } else {
                "Осталось ${
                    formatDurationCompact(
                        item.remainingMillis
                    )
                }"
            }
        }

        ActivityTargetType.LIMIT -> {
            if (
                item.exceededMillis >
                0L
            ) {
                "Лимит превышен на ${
                    formatDurationCompact(
                        item.exceededMillis
                    )
                }"
            } else {
                "Можно еще ${
                    formatDurationCompact(
                        item.remainingMillis
                    )
                }"
            }
        }
    }

private fun formatPercent(
    value:
    Float,
): String =
    String.format(
        Locale.US,
        "%.0f%%",
        value,
    )
