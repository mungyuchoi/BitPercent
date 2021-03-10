package com.moon.bitpercent.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BitEntity::class], version = 1)
abstract class BitDatabase : RoomDatabase() {
    abstract fun bitDao(): BitDao

    companion object {
        private var INSTANCE: BitDatabase? = null
        fun getInstance(context: Context): BitDatabase {
            if (INSTANCE == null) {
                synchronized(BitDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        BitDatabase::class.java,
                        "bit.db"
                    ).fallbackToDestructiveMigration()
                        .addCallback(object : RoomDatabase.Callback() {})
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}