package com.example.lnreader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Novel(
    @PrimaryKey(autoGenerate = true) var id : Int,
    val title : String,
    val website : String,
    var coverLocation : String,
    var autoupdate : Boolean,
    val description : String,
    var lastAccessed : Long,
    var lastUpdated : Long
    //val author : String
) {
}