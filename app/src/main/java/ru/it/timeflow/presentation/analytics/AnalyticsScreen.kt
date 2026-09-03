package ru.it.timeflow.presentation.analytics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.model.ActivityTargetPeriod
import ru.it.timeflow.domain.model.ActivityTargetType
import ru.it.timeflow.presentation.analytics.components.ActivityGoalsSection
import ru.it.timeflow.presentation.analytics.components.ActivityTargetBottomSheet
import ru.it.timeflow.presentation.analytics.components.TimeDistributionPieChart
import ru.it.timeflow.presentation.analytics.components.WeekComparisonSection
import ru.it.timeflow.util.formatDurationCompact
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AnalyticsRoute(
    viewModel:
    AnalyticsViewModel =
        hiltViewModel(),
) {
    val state by
    viewModel
        .state
        .collectAsStateWithLifecycle()

    AnalyticsScreen(
        state =
            state,

        onPeriodSelected =
            viewModel::selectPeriod,

        onAddTarget =
            viewModel::openCreateTarget,

        onEditTarget =
            viewModel::openEditTarget,

        onDismissTargetEditor =
            viewModel::closeTargetEditor,

        onSaveTarget =
            viewModel::saveTarget,

        onDeleteTarget =
            viewModel::deleteTarget,
    )
}

@Composable
fun AnalyticsScreen(
    state:
    AnalyticsUiState,

    onPeriodSelected:
        (AnalyticsPeriod) -> Unit,

    onAddTarget:
        () -> Unit,

    onEditTarget:
        (ActivityTarget) -> Unit,

    onDismissTargetEditor:
        () -> Unit,

    onSaveTarget: (
        categoryId: Long,
        type: ActivityTargetType,
        period: ActivityTargetPeriod,
        targetMillis: Long,
    ) -> Unit,

    onDeleteTarget:
        (Long) -> Unit,
) {
    if (
        state.isLoading
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize(),

            contentAlignment =
                Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        return
    }

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(
                20.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(
                16.dp
            ),
    ) {
        item {
            Text(
                text =
                    "Аналитика",

                style =
                    MaterialTheme
                        .typography
                        .headlineLarge,

                fontWeight =
                    FontWeight.Bold,
            )

            Text(
                text =
                    state.periodTitle,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )
        }

        item {
            PeriodSelector(
                selectedPeriod =
                    state.selectedPeriod,

                onPeriodSelected =
                    onPeriodSelected,
            )
        }

        item {
            TotalTimeCard(
                totalMillis =
                    state.totalMillis,
            )
        }

        item {
            Text(
                text =
                    "Распределение времени",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.SemiBold,
            )
        }

        item {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        24.dp
                    ),

                colors =
                    CardDefaults
                        .cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceContainer
                        ),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                20.dp
                            ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                ) {
                    TimeDistributionPieChart(
                        items =
                            state.items,

                        totalMillis =
                            state.totalMillis,
                    )

                    Spacer(
                        Modifier.height(
                            12.dp
                        )
                    )

                    if (
                        state.items
                            .isEmpty()
                    ) {
                        Text(
                            text =
                                "За выбранный период пока нет данных.",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                        )
                    } else {
                        state.items
                            .forEach {
                                    item ->

                                ChartLegendRow(
                                    item =
                                        item
                                )
                            }
                    }
                }
            }
        }

        item {
            Text(
                text =
                    "По категориям",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.SemiBold,
            )
        }

        items(
            items =
                state.items,

            key = {
                it.categoryId
            },
        ) {
                item ->

            CategoryStatisticsCard(
                item =
                    item
            )
        }

        if (
            state.items.isEmpty()
        ) {
            item {
                Text(
                    text =
                        "Статистика появится после первой записи времени.",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )
            }
        }

        item {
            Spacer(
                Modifier.height(
                    4.dp
                )
            )

            WeekComparisonSection(
                items =
                    state.weekComparisonItems,

                summary =
                    state.weekComparisonSummary,
            )
        }

        item {
            Spacer(
                Modifier.height(
                    4.dp
                )
            )

            ActivityGoalsSection(
                items =
                    state.goalProgressItems,

                canAddTarget =
                    state.canAddTarget,

                onAddTarget =
                    onAddTarget,

                onEditTarget =
                    onEditTarget,
            )
        }

        item {
            Spacer(
                Modifier.height(
                    6.dp
                )
            )

            LifeTimeSection(
                items =
                    state.lifeTimeItems,
            )
        }

        item {
            Spacer(
                Modifier.height(
                    12.dp
                )
            )
        }
    }

    if (
        state.isTargetEditorOpen
    ) {
        ActivityTargetBottomSheet(
            categories =
                state.categories,

            editingTarget =
                state.editingTarget,

            usedCategoryIds =
                state
                    .goalProgressItems
                    .map {
                        it.target
                            .categoryId
                    }
                    .toSet(),

            onSave =
                onSaveTarget,

            onDelete =
                onDeleteTarget,

            onDismiss =
                onDismissTargetEditor,
        )
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod:
    AnalyticsPeriod,

    onPeriodSelected:
        (AnalyticsPeriod) -> Unit,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                8.dp
            ),
    ) {
        AnalyticsPeriod
            .entries
            .forEach {
                    period ->

                FilterChip(
                    selected =
                        selectedPeriod ==
                                period,

                    onClick = {
                        onPeriodSelected(
                            period
                        )
                    },

                    label = {
                        Text(
                            period.title
                        )
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        ),
                )
            }
    }
}

@Composable
private fun TotalTimeCard(
    totalMillis:
    Long,
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                22.dp
            ),

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
                Modifier.padding(
                    20.dp
                ),
        ) {
            Text(
                text =
                    "Учтено времени",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )

            Spacer(
                Modifier.height(
                    4.dp
                )
            )

            Text(
                text =
                    formatDurationCompact(
                        totalMillis
                    ),

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,

                fontWeight =
                    FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ChartLegendRow(
    item:
    AnalyticsCategoryItem,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical =
                        7.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(
                        12.dp
                    )
                    .background(
                        color =
                            Color(
                                item.colorArgb
                            ),

                        shape =
                            CircleShape,
                    )
        )

        Spacer(
            Modifier.size(
                10.dp
            )
        )

        Text(
            text =
                "${item.emoji} ${item.name}",

            modifier =
                Modifier.weight(
                    1f
                ),

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
        )

        Text(
            text =
                formatPercentage(
                    item.percentage
                ),

            fontWeight =
                FontWeight.Bold,
        )
    }
}

@Composable
private fun CategoryStatisticsCard(
    item:
    AnalyticsCategoryItem,
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
                                42.dp
                            )
                            .background(
                                color =
                                    Color(
                                        item.colorArgb
                                    ).copy(
                                        alpha =
                                            0.15f
                                    ),

                                shape =
                                    CircleShape,
                            ),

                    contentAlignment =
                        Alignment.Center,
                ) {
                    Text(
                        text =
                            item.emoji
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
                            item.name,

                        fontWeight =
                            FontWeight.SemiBold,
                    )

                    Text(
                        text =
                            formatDurationCompact(
                                item.durationMillis
                            ),

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                    )
                }

                Text(
                    text =
                        formatPercentage(
                            item.percentage
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(
                            item.colorArgb
                        ),
                )
            }

            Spacer(
                Modifier.height(
                    14.dp
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
                                (
                                        item.percentage /
                                                100f
                                        ).coerceIn(
                                        0f,
                                        1f
                                    )
                            )
                            .height(
                                9.dp
                            )
                            .background(
                                color =
                                    Color(
                                        item.colorArgb
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        99.dp
                                    ),
                            ),
                )
            }
        }
    }
}

@Composable
private fun LifeTimeSection(
    items:
    List<LifeTimeItem>,
) {
    Column(
        modifier =
            Modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(
                12.dp
            ),
    ) {
        Text(
            text =
                "Время жизни",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall,

            fontWeight =
                FontWeight.Bold,
        )

        Text(
            text =
                "За последние 30 дней вы потратили:",

            style =
                MaterialTheme
                    .typography
                    .bodyLarge,

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
                Text(
                    text =
                        "Пока недостаточно данных. Начните отслеживать занятия, и здесь появится прогноз.",

                    modifier =
                        Modifier.padding(
                            18.dp
                        ),

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
                RoundedCornerShape(
                    22.dp
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
                            18.dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    ),
            ) {
                items.forEach {
                        item ->

                    LifeTimeLast30DaysRow(
                        item =
                            item
                    )
                }
            }
        }

        Spacer(
            Modifier.height(
                4.dp
            )
        )

        Text(
            text =
                "Если продолжить в таком темпе, за год:",

            style =
                MaterialTheme
                    .typography
                    .bodyLarge,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    22.dp
                ),

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
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            18.dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    ),
            ) {
                items.forEach {
                        item ->

                    LifeTimeYearProjectionRow(
                        item =
                            item
                    )
                }
            }
        }

        Text(
            text =
                "Прогноз рассчитан по среднему темпу последних 30 дней.",

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

@Composable
private fun LifeTimeLast30DaysRow(
    item:
    LifeTimeItem,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        LifeTimeCategoryMarker(
            item =
                item
        )

        Spacer(
            Modifier.size(
                10.dp
            )
        )

        Text(
            text =
                item.name,

            modifier =
                Modifier.weight(
                    1f
                ),

            fontWeight =
                FontWeight.Medium,
        )

        Text(
            text =
                formatLifeTimeHours(
                    item.last30DaysMillis
                ),

            fontWeight =
                FontWeight.Bold,
        )
    }
}

@Composable
private fun LifeTimeYearProjectionRow(
    item:
    LifeTimeItem,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        LifeTimeCategoryMarker(
            item =
                item
        )

        Spacer(
            Modifier.size(
                10.dp
            )
        )

        Text(
            text =
                item.name,

            modifier =
                Modifier.weight(
                    1f
                ),

            fontWeight =
                FontWeight.Medium,
        )

        Text(
            text =
                formatProjectedDays(
                    item.projectedYearDays
                ),

            fontWeight =
                FontWeight.Bold,
        )
    }
}

@Composable
private fun LifeTimeCategoryMarker(
    item:
    LifeTimeItem,
) {
    Box(
        modifier =
            Modifier
                .size(
                    36.dp
                )
                .background(
                    color =
                        Color(
                            item.colorArgb
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
                item.emoji
        )
    }
}

private fun formatPercentage(
    value:
    Float,
): String {

    return if (
        value >= 10f ||
        value % 1f ==
        0f
    ) {
        String.format(
            Locale.US,
            "%.0f%%",
            value,
        )
    } else {
        String.format(
            Locale.US,
            "%.1f%%",
            value,
        )
    }
}

private fun formatLifeTimeHours(
    millis:
    Long,
): String {

    val hours =
        millis.toDouble() /
                MILLIS_PER_HOUR

    return if (
        hours >= 10.0
    ) {
        "${hours.roundToInt()} ч"
    } else {
        String.format(
            Locale.US,
            "%.1f ч",
            hours,
        )
    }
}

private fun formatProjectedDays(
    days:
    Double,
): String {

    return when {
        days >= 10.0 ->
            "${days.roundToInt()} дн."

        days >= 1.0 ->
            String.format(
                Locale.US,
                "%.1f дн.",
                days,
            )

        else -> {
            val hours =
                days * 24.0

            String.format(
                Locale.US,
                "%.1f ч",
                hours,
            )
        }
    }
}

private const val
        MILLIS_PER_HOUR =
    3_600_000.0
