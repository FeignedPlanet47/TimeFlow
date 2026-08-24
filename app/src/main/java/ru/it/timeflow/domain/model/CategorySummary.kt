package ru.it.timeflow.domain.model

data class CategorySummary(
    val categoryId: Long,
    val name: String,
    val emoji: String,
    val colorArgb: Long,
    val durationMillis: Long,
)
