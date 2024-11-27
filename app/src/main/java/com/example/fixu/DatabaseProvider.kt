package com.example.fixu

import android.content.Context
import androidx.room.Room
import com.example.fixu.database.AppDatabase

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "diagnose_database"
            ).build()
            instance = db
            db
        }
    }
}
