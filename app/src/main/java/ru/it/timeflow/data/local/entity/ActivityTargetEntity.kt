package ru.it.timeflow.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "activity_targets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class ActivityTargetEntity(
    @androidx.room.PrimaryKey
    val categoryId: Long,
    val type: String,
    val period: String,
    val targetMinutes: Long,
)
