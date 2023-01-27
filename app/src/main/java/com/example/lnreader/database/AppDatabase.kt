package com.example.lnreader.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Novel::class, Chapter::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun NovelDao(): NovelDao
    abstract fun ChapterDao(): ChapterDao
}