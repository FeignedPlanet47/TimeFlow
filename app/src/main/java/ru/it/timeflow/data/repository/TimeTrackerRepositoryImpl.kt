package ru.it.timeflow.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.it.timeflow.data.local.TimeFlowDatabase
import ru.it.timeflow.data.local.dao.CategoryDao
import ru.it.timeflow.data.local.dao.TaskDao
import ru.it.timeflow.data.local.dao.TimeEntryDao
import ru.it.timeflow.data.local.entity.CategoryEntity
import ru.it.timeflow.data.local.entity.TaskEntity
import ru.it.timeflow.data.local.entity.TimeEntryEntity
import ru.it.timeflow.data.mapper.toDomain
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.model.TimeEntry
import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeTrackerRepositoryImpl @Inject constructor(
    private val database: TimeFlowDatabase,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val timeEntryDao: TimeEntryDao,
) : TimeTrackerRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override fun observeTasksByCategory(
        categoryId: Long,
    ): Flow<List<Task>> =
        taskDao.observeByCategory(categoryId).map { list ->
            list.map { it.toDomain() }
        }

    override fun observeActiveEntry(): Flow<TimeEntry?> =
        timeEntryDao.observeActive().map { it?.toDomain() }

    override fun observeEntriesBetween(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<TimeEntry>> =
        timeEntryDao.observeBetween(
            startMillis = startMillis,
            endMillis = endMillis,
        ).map { rows ->
            rows.map { it.toDomain() }
        }

    override suspend fun startTracking(
        categoryId: Long,
        taskId: Long?,
        taskName: String?,
        note: String?,
    ) {
        val now = System.currentTimeMillis()

        database.withTransaction {

            timeEntryDao.stopAllRunning(now)

            val selectedTask = taskId
                ?.let { taskDao.getById(it) }
                ?.takeIf { it.categoryId == categoryId }

            val resolvedTaskName =
                selectedTask?.name
                    ?: taskName
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }

            timeEntryDao.insert(
                TimeEntryEntity(
                    categoryId = categoryId,
                    taskId = selectedTask?.id,
                    startTimeMillis = now,
                    taskName = resolvedTaskName,
                    note = note?.trim()?.takeIf { it.isNotEmpty() },
                )
            )
        }
    }

    override suspend fun stopTracking() {
        timeEntryDao.stopAllRunning(
            System.currentTimeMillis()
        )
    }

    override suspend fun updateEntryNote(
        entryId: Long,
        note: String?,
    ) {
        timeEntryDao.updateNote(
            entryId = entryId,
            note = note
                ?.trim()
                ?.takeIf { it.isNotEmpty() },
        )
    }

    override suspend fun addCategory(
        name: String,
        emoji: String,
        colorArgb: Long,
    ) {
        categoryDao.insert(
            CategoryEntity(
                name = name.trim(),
                emoji = emoji.trim().ifEmpty { "⏱️" },
                colorArgb = colorArgb,
            )
        )
    }

    override suspend fun addTask(
        categoryId: Long,
        name: String,
    ): Long {
        val normalizedName = name.trim()

        require(normalizedName.isNotEmpty()) {
            "Task name cannot be empty"
        }

        return taskDao.insert(
            TaskEntity(
                categoryId = categoryId,
                name = normalizedName,
            )
        )
    }

    override suspend fun addManualEntry(
        categoryId: Long,
        startMillis: Long,
        endMillis: Long,
        taskName: String?,
        note: String?,
    ) {
        require(endMillis > startMillis) {
            "endMillis must be greater than startMillis"
        }

        timeEntryDao.insert(
            TimeEntryEntity(
                categoryId = categoryId,
                startTimeMillis = startMillis,
                endTimeMillis = endMillis,
                taskName = taskName?.trim()?.takeIf { it.isNotEmpty() },
                note = note?.trim()?.takeIf { it.isNotEmpty() },
            )
        )
    }

    override suspend fun seedDefaults() {
        if (categoryDao.count() > 0) return

        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    name = "Работа",
                    emoji = "💻",
                    colorArgb = 0xFF6750A4
                ),
                CategoryEntity(
                    name = "Учёба",
                    emoji = "📚",
                    colorArgb = 0xFF3F51B5
                ),
                CategoryEntity(
                    name = "Спорт",
                    emoji = "🏋️",
                    colorArgb = 0xFF2E7D32
                ),
                CategoryEntity(
                    name = "Отдых",
                    emoji = "🍿",
                    colorArgb = 0xFFF9A825
                ),
                CategoryEntity(
                    name = "Дорога",
                    emoji = "🚗",
                    colorArgb = 0xFF00838F
                ),
                CategoryEntity(
                    name = "Игры",
                    emoji = "🎮",
                    colorArgb = 0xFFC62828
                ),
            )
        )
    }
}
