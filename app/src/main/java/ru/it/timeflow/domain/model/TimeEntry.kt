package ru.it.timeflow.domain.model

data class TimeEntry(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val categoryColorArgb: Long,
    val categoryEmoji: String,
    val taskId: Long?,
    val startTimeMillis: Long,
    val endTimeMillis: Long?,
    val taskName: String?,
    val note: String?,
) {
    val isRunning: Boolean get() = endTimeMillis == null

    fun durationMillis(nowMillis: Long = System.currentTimeMillis()): Long =
        ((endTimeMillis ?: nowMillis) - startTimeMillis).coerceAtLeast(0L)
}
