package ru.it.timeflow.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.it.timeflow.data.local.TimeFlowDatabase
import ru.it.timeflow.data.local.dao.CategoryDao
import ru.it.timeflow.data.local.dao.TaskDao
import ru.it.timeflow.data.local.dao.TimeEntryDao
import ru.it.timeflow.data.local.migration.MIGRATION_1_2
import ru.it.timeflow.data.repository.TimeTrackerRepositoryImpl
import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TimeFlowDatabase =
        Room.databaseBuilder(
            context,
            TimeFlowDatabase::class.java,
            "timeflow.db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideCategoryDao(
        database: TimeFlowDatabase,
    ): CategoryDao = database.categoryDao()

    @Provides
    fun provideTaskDao(
        database: TimeFlowDatabase,
    ): TaskDao = database.taskDao()

    @Provides
    fun provideTimeEntryDao(
        database: TimeFlowDatabase,
    ): TimeEntryDao = database.timeEntryDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimeTrackerRepository(
        impl: TimeTrackerRepositoryImpl,
    ): TimeTrackerRepository
}
