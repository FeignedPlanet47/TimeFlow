package ru.it.timeflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.it.timeflow.data.local.dao.ActivityTargetDao
import ru.it.timeflow.data.local.dao.CategoryDao
import ru.it.timeflow.data.local.dao.TaskDao
import ru.it.timeflow.data.local.dao.TimeEntryDao
import ru.it.timeflow.data.local.entity.ActivityTargetEntity
import ru.it.timeflow.data.local.entity.CategoryEntity
import ru.it.timeflow.data.local.entity.TaskEntity
import ru.it.timeflow.data.local.entity.TimeEntryEntity

@Database(
    entities = [
        CategoryEntity::class,
        TaskEntity::class,
        TimeEntryEntity::class,
        ActivityTargetEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class TimeFlowDatabase :
    RoomDatabase() {

    abstract fun categoryDao():
            CategoryDao

    abstract fun taskDao():
            TaskDao

    abstract fun timeEntryDao():
            TimeEntryDao

    abstract fun activityTargetDao():
            ActivityTargetDao
}
