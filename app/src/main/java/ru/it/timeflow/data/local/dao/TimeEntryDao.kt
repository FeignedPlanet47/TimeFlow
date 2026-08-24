package ru.it.timeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.data.local.entity.TimeEntryEntity
import ru.it.timeflow.data.local.entity.TimeEntryRow

@Dao
interface TimeEntryDao {

    @Insert
    suspend fun insert(entry: TimeEntryEntity): Long

    @Query("UPDATE time_entries SET endTimeMillis = :endMillis WHERE endTimeMillis IS NULL")
    suspend fun stopAllRunning(endMillis: Long)

    @Query(
        """
        UPDATE time_entries
        SET note = :note
        WHERE id = :entryId
        """
    )
    suspend fun updateNote(
        entryId: Long,
        note: String?,
    )

    @Query(
        """
        SELECT
            e.id AS entryId,
            e.categoryId AS categoryId,
            c.name AS categoryName,
            c.colorArgb AS categoryColorArgb,
            c.emoji AS categoryEmoji,
            e.taskId AS taskId,
            e.startTimeMillis AS startTimeMillis,
            e.endTimeMillis AS endTimeMillis,
            e.taskName AS taskName,
            e.note AS note
        FROM time_entries e
        JOIN categories c ON c.id = e.categoryId
        WHERE e.endTimeMillis IS NULL
        ORDER BY e.startTimeMillis DESC
        LIMIT 1
        """
    )
    fun observeActive(): Flow<TimeEntryRow?>

    @Query(
        """
        SELECT
            e.id AS entryId,
            e.categoryId AS categoryId,
            c.name AS categoryName,
            c.colorArgb AS categoryColorArgb,
            c.emoji AS categoryEmoji,
            e.taskId AS taskId,
            e.startTimeMillis AS startTimeMillis,
            e.endTimeMillis AS endTimeMillis,
            e.taskName AS taskName,
            e.note AS note
        FROM time_entries e
        JOIN categories c ON c.id = e.categoryId
        WHERE e.startTimeMillis < :endMillis
          AND COALESCE(e.endTimeMillis, :endMillis) > :startMillis
        ORDER BY e.startTimeMillis DESC
        """
    )
    fun observeBetween(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<TimeEntryRow>>
}
