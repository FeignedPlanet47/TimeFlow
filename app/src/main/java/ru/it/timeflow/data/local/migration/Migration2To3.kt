package ru.it.timeflow.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 =
    object : Migration(
        2,
        3,
    ) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `activity_targets` (
                    `categoryId` INTEGER NOT NULL,
                    `type` TEXT NOT NULL,
                    `period` TEXT NOT NULL,
                    `targetMinutes` INTEGER NOT NULL,
                    PRIMARY KEY(`categoryId`),
                    FOREIGN KEY(`categoryId`)
                        REFERENCES `categories`(`id`)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                )
                """.trimIndent()
            )
        }
    }
