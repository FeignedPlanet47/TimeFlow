package ru.it.timeflow.data.local.entity

data class TimeEntryRow(
    val entryId: Long,
    val categoryId: Long,
    val categoryName: String,
    val categoryColorArgb: Long,
    val categoryEmoji: String,
    val taskId: Long?,
    val startTimeMillis: Long,
    val endTimeMillis: Long?,
    val taskName: String?,
    val note: String?,
)
