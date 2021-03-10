package com.moon.bitpercent.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bit")
class BitEntity(
    @PrimaryKey(autoGenerate = true)
    var idx: Int = 0,
    var type: String,
    var price: Double,
    var percent: Double,
    var result: Double
)