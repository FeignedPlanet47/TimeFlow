package ru.it.timeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.data.local.entity.TaskEntity

@Dao
interface TaskDao {

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE categoryId = :categoryId
        ORDER BY name ASC
        """
    )
    fun observeByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: TaskEntity): Long
}
