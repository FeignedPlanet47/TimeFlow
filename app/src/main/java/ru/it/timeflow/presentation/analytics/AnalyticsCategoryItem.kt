package ru.it.timeflow.presentation.analytics

data class AnalyticsCategoryItem(
    val categoryId: Long,
    val name: String,
    val emoji: String,
    val colorArgb: Long,
    val durationMillis: Long,
    val percentage: Float,
)