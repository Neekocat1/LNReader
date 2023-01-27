package com.example.lnreader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val novelId: Int,
    val title: String,
    val fileLocation : String,
    var markedAsRead : Boolean
    ) {


}