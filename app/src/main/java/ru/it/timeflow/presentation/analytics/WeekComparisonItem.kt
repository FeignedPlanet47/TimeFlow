package ru.it.timeflow.presentation.analytics

enum class WeekChangeDirection {
    UP,
    DOWN,
    SAME,
    NEW,
}

data class WeekComparisonItem(
    val categoryId: Long,
    val name: String,
    val emoji: String,
    val colorArgb: Long,
    val currentWeekMillis: Long,
    val previousWeekMillis: Long,
    val differenceMillis: Long,
    val changePercent: Float?,
    val direction: WeekChangeDirection,
)
