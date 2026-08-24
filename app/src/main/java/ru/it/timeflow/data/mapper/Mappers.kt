package ru.it.timeflow.data.mapper

import ru.it.timeflow.data.local.entity.CategoryEntity
import ru.it.timeflow.data.local.entity.TaskEntity
import ru.it.timeflow.data.local.entity.TimeEntryRow
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.model.TimeEntry

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    colorArgb = colorArgb,
    emoji = emoji,
)

fun TaskEntity.toDomain() = Task(
    id = id,
    categoryId = categoryId,
    name = name,
)

fun TimeEntryRow.toDomain() = TimeEntry(
    id = entryId,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryColorArgb = categoryColorArgb,
    categoryEmoji = categoryEmoji,
    taskId = taskId,
    startTimeMillis = startTimeMillis,
    endTimeMillis = endTimeMillis,
    taskName = taskName,
    note = note,
)
