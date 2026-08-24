package ru.it.timeflow.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "time_entries",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("categoryId"),
        Index("taskId"),
        Index("startTimeMillis"),
        Index("endTimeMillis"),
    ],
)
data class TimeEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val taskId: Long? = null,
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null,
    val taskName: String? = null,
    val note: String? = null,
)
