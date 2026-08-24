package ru.it.timeflow.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `tasks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `categoryId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                FOREIGN KEY(`categoryId`)
                    REFERENCES `categories`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_tasks_categoryId`
            ON `tasks` (`categoryId`)
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE `time_entries`
            ADD COLUMN `taskId` INTEGER
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_time_entries_taskId`
            ON `time_entries` (`taskId`)
            """.trimIndent()
        )
    }
}
