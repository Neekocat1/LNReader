package com.example.lnreader

import android.content.Context
import androidx.room.Room
import com.example.lnreader.database.AppDatabase


class DBSetup(appcontext: Context) {

    var context = appcontext
    fun ImportData()
    {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "Library"
        ).build()
        val novelDao = db.NovelDao()
        val chapterDao = db.ChapterDao()



    }


}