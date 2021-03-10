package com.moon.bitpercent.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BitDao {
    @Query("SELECT * FROM bit")
    fun getAllBit(): LiveData<List<BitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bitEntity: BitEntity)
}