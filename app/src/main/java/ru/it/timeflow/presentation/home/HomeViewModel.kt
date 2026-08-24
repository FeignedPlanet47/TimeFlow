package ru.it.timeflow.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.usecase.AddManualEntryUseCase
import ru.it.timeflow.domain.usecase.AddTaskUseCase
import ru.it.timeflow.domain.usecase.ObserveActiveEntryUseCase
import ru.it.timeflow.domain.usecase.ObserveCategoriesUseCase
import ru.it.timeflow.domain.usecase.ObserveEntriesBetweenUseCase
import ru.it.timeflow.domain.usecase.ObserveTasksByCategoryUseCase
import ru.it.timeflow.domain.usecase.SeedDefaultCategoriesUseCase
import ru.it.timeflow.domain.usecase.StartTrackingUseCase
import ru.it.timeflow.domain.usecase.StopTrackingUseCase
import ru.it.timeflow.domain.usecase.UpdateTimeEntryNoteUseCase
import ru.it.timeflow.presentation.home.timeline.TimelineItem
import ru.it.timeflow.presentation.home.timeline.buildTodayTimeline
import ru.it.timeflow.util.dayBoundsMillis
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeCategories: ObserveCategoriesUseCase,
    observeActiveEntry: ObserveActiveEntryUseCase,
    observeEntriesBetween: ObserveEntriesBetweenUseCase,
    observeTasksByCategory: ObserveTasksByCategoryUseCase,
    private val startTracking: StartTrackingUseCase,
    private val stopTracking: StopTrackingUseCase,
    private val updateTimeEntryNote: UpdateTimeEntryNoteUseCase,
    private val addTask: AddTaskUseCase,
    private val addManualEntryUseCase:
    AddManualEntryUseCase,
    seedDefaults: SeedDefaultCategoriesUseCase,
) : ViewModel() {

    private val nowMillis =
        MutableStateFlow(
            System.currentTimeMillis()
        )

    private val selectedCategoryId =
        MutableStateFlow<Long?>(null)

    private val selectedManualGap =
        MutableStateFlow<
                TimelineItem.Gap?
                >(null)

    private val dayBounds =
        dayBoundsMillis()

    private val tasksForSelectedCategory =
        selectedCategoryId
            .flatMapLatest {
                    categoryId ->

                if (categoryId == null) {
                    flowOf<List<Task>>(
                        emptyList()
                    )
                } else {
                    observeTasksByCategory(
                        categoryId
                    )
                }
            }

    private val baseState =
        combine(
            observeCategories(),
            observeActiveEntry(),
            observeEntriesBetween(
                dayBounds.first,
                dayBounds.second,
            ),
            nowMillis,
        ) {
                categories,
                active,
                entries,
                now ->

            HomeUiState(
                categories = categories,
                activeEntry = active,
                todayEntries = entries,
                timelineItems =
                    buildTodayTimeline(
                        entries = entries,
                        dayStartMillis =
                            dayBounds.first,
                        nowMillis = now,
                    ),
                nowMillis = now,
                isLoading = false,
            )
        }

    val state =
        combine(
            baseState,
            selectedCategoryId,
            tasksForSelectedCategory,
            selectedManualGap,
        ) {
                base,
                categoryId,
                tasks,
                manualGap ->

            base.copy(
                taskPickerCategory =
                    base.categories
                        .firstOrNull {
                            it.id ==
                                    categoryId
                        },
                tasksForPicker = tasks,
                manualEntryGap =
                    manualGap,
            )
        }.stateIn(
            scope = viewModelScope,
            started =
                SharingStarted
                    .WhileSubscribed(
                        5_000
                    ),
            initialValue =
                HomeUiState(),
        )

    init {
        viewModelScope.launch {
            seedDefaults()
        }

        viewModelScope.launch {
            while (isActive) {

                nowMillis.value =
                    System
                        .currentTimeMillis()

                delay(1_000)
            }
        }
    }

    fun start(
        categoryId: Long
    ) {
        viewModelScope.launch {
            startTracking(
                categoryId =
                    categoryId,
            )
        }
    }

    fun openTaskPicker(
        categoryId: Long
    ) {
        selectedCategoryId.value =
            categoryId
    }

    fun closeTaskPicker() {
        selectedCategoryId.value =
            null
    }

    fun startTask(
        task: Task
    ) {
        viewModelScope.launch {

            startTracking(
                categoryId =
                    task.categoryId,
                taskId =
                    task.id,
            )

            closeTaskPicker()
        }
    }

    fun addTask(
        name: String
    ) {
        val categoryId =
            selectedCategoryId.value
                ?: return

        val normalizedName =
            name.trim()

        if (
            normalizedName.isEmpty()
        ) {
            return
        }

        viewModelScope.launch {
            addTask(
                categoryId =
                    categoryId,
                name =
                    normalizedName,
            )
        }
    }

    fun saveNote(
        entryId: Long,
        note: String,
    ) {
        viewModelScope.launch {
            updateTimeEntryNote(
                entryId = entryId,
                note = note,
            )
        }
    }

    fun openManualEntry(
        gap: TimelineItem.Gap
    ) {
        selectedManualGap.value =
            gap
    }

    fun closeManualEntry() {
        selectedManualGap.value =
            null
    }

    fun addManualEntry(
        categoryId: Long,
        startMillis: Long,
        endMillis: Long,
        taskName: String?,
        note: String?,
    ) {
        val gap =
            selectedManualGap.value
                ?: return

        if (
            endMillis <= startMillis ||
            startMillis <
            gap.startMillis ||
            endMillis >
            gap.endMillis
        ) {
            return
        }

        viewModelScope.launch {

            addManualEntryUseCase(
                categoryId =
                    categoryId,
                startMillis =
                    startMillis,
                endMillis =
                    endMillis,
                taskName =
                    taskName,
                note =
                    note,
            )

            closeManualEntry()
        }
    }

    fun stop() {
        viewModelScope.launch {
            stopTracking()
        }
    }
}
