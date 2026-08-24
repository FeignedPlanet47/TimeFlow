package ru.it.timeflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.it.timeflow.data.local.dao.CategoryDao
import ru.it.timeflow.data.local.dao.TaskDao
import ru.it.timeflow.data.local.dao.TimeEntryDao
import ru.it.timeflow.data.local.entity.CategoryEntity
import ru.it.timeflow.data.local.entity.TaskEntity
import ru.it.timeflow.data.local.entity.TimeEntryEntity

@Database(
    entities = [
        CategoryEntity::class,
        TaskEntity::class,
        TimeEntryEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class TimeFlowDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun taskDao(): TaskDao

    abstract fun timeEntryDao(): TimeEntryDao
}
