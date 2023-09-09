package com.example.lnreader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var novelId: Int,
    val title: String,
    var fileLocation : String,
    var markedAsRead : Boolean,
    var isDownloaded : Boolean
    ) {


}