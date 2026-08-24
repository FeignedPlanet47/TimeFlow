package ru.it.timeflow.presentation.analytics

import ru.it.timeflow.domain.model.CategorySummary

data class AnalyticsUiState(
    val summaries: List<CategorySummary> = emptyList(),
    val totalMillis: Long = 0L,
)
