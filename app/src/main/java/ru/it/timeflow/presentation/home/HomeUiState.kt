package ru.it.timeflow.presentation.home

import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.model.TimeEntry
import ru.it.timeflow.presentation.home.timeline.TimelineItem

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val activeEntry: TimeEntry? = null,
    val todayEntries: List<TimeEntry> = emptyList(),
    val timelineItems: List<TimelineItem> = emptyList(),
    val nowMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val taskPickerCategory: Category? = null,
    val tasksForPicker: List<Task> = emptyList(),
    val manualEntryGap: TimelineItem.Gap? = null,
) {
    val todayTrackedMillis: Long
        get() = todayEntries.sumOf {
            it.durationMillis(nowMillis)
        }
}
