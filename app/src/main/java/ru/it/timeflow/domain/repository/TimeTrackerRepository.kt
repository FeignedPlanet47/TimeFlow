package ru.it.timeflow.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.model.TimeEntry

interface TimeTrackerRepository {

    fun observeCategories(): Flow<List<Category>>

    fun observeTasksByCategory(categoryId: Long): Flow<List<Task>>

    fun observeActiveEntry(): Flow<TimeEntry?>

    fun observeEntriesBetween(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<TimeEntry>>

    suspend fun startTracking(
        categoryId: Long,
        taskId: Long? = null,
        taskName: String? = null,
        note: String? = null,
    )

    suspend fun stopTracking()

    suspend fun updateEntryNote(
        entryId: Long,
        note: String?,
    )

    suspend fun addCategory(
        name: String,
        emoji: String,
        colorArgb: Long,
    )

    suspend fun addTask(
        categoryId: Long,
        name: String,
    ): Long

    suspend fun addManualEntry(
        categoryId: Long,
        startMillis: Long,
        endMillis: Long,
        taskName: String? = null,
        note: String? = null,
    )

    suspend fun seedDefaults()
}
