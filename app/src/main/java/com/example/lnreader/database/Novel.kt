package com.example.lnreader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Novel(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val title : String,
    val website : String,
    val coverLocation : String,
    var autoupdate : Boolean,
    val description : String,
    //val author : String
) {
}