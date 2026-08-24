package ru.it.timeflow.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.it.timeflow.domain.usecase.ObserveEntriesBetweenUseCase
import ru.it.timeflow.util.dayBoundsMillis
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeEntriesBetween: ObserveEntriesBetweenUseCase,
) : ViewModel() {
    private val bounds = dayBoundsMillis()
    val entries = observeEntriesBetween(bounds.first, bounds.second)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
