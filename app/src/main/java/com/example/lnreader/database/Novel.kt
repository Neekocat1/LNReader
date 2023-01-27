package com.example.lnreader.database

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lnreader.ui.chapter.Chapter
@Entity
data class Novel(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val title : String,
    val website : String,
    val coverLocation : String,
    var autoupdate : Boolean,
    val description : String
) {
}