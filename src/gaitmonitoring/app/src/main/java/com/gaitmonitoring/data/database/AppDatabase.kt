package com.gaitmonitoring.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gaitmonitoring.domain.entities.Step
import kotlin.concurrent.Volatile

@Database(entities = [Step::class], version = 2)
@TypeConverters(RoomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getStepsDAO(): StepDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE new_Step (id INTEGER PRIMARY KEY NOT NULL, count INTEGER NOT NULL DEFAULT 0)")
                database.execSQL("DROP TABLE Step")
                database.execSQL("ALTER TABLE new_Step RENAME TO Step")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
