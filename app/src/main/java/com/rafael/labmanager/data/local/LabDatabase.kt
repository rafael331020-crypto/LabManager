package com.rafael.labmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rafael.labmanager.data.model.*

@Database(
    entities = [
        PatientEntity::class,
        OrderEntity::class,
        SampleEntity::class,
        ResultEntity::class,
        StockItemEntity::class,
        AuditEventEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class LabDatabase : RoomDatabase() {
    abstract fun labDao(): LabDao

    companion object {
        @Volatile private var INSTANCE: LabDatabase? = null

        fun get(context: Context): LabDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                LabDatabase::class.java,
                "labmanager.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
