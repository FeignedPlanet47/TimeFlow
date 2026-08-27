package ru.it.timeflow.presentation.analytics

data class LifeTimeItem(
    val categoryId: Long,
    val name: String,
    val emoji: String,
    val colorArgb: Long,
    val last30DaysMillis: Long,
    val projectedYearDays: Double,
)
