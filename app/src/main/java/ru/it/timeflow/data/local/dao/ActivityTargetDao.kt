package ru.it.timeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.data.local.entity.ActivityTargetEntity

@Dao
interface ActivityTargetDao {

    @Query(
        """
        SELECT *
        FROM activity_targets
        ORDER BY categoryId ASC
        """
    )
    fun observeAll():
            Flow<List<ActivityTargetEntity>>

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )
    suspend fun save(
        target: ActivityTargetEntity
    )

    @Query(
        """
        DELETE FROM activity_targets
        WHERE categoryId = :categoryId
        """
    )
    suspend fun deleteByCategory(
        categoryId: Long
    )
}
