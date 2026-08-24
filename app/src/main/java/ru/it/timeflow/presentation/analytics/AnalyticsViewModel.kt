package ru.it.timeflow.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.it.timeflow.domain.model.CategorySummary
import ru.it.timeflow.domain.usecase.ObserveEntriesBetweenUseCase
import ru.it.timeflow.util.weekBoundsMillis
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    observeEntriesBetween: ObserveEntriesBetweenUseCase,
) : ViewModel() {
    private val bounds = weekBoundsMillis()

    val state = observeEntriesBetween(bounds.first, bounds.second)
        .map { entries ->
            val now = System.currentTimeMillis()
            val summaries = entries
                .groupBy { it.categoryId }
                .map { (_, group) ->
                    val first = group.first()
                    CategorySummary(
                        categoryId = first.categoryId,
                        name = first.categoryName,
                        emoji = first.categoryEmoji,
                        colorArgb = first.categoryColorArgb,
                        durationMillis = group.sumOf { it.durationMillis(now) },
                    )
                }
                .sortedByDescending { it.durationMillis }
            AnalyticsUiState(
                summaries = summaries,
                totalMillis = summaries.sumOf { it.durationMillis },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsUiState())
}
