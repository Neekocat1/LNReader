package com.example.lnreader.database

import androidx.room.*

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertChapter(chapter: Chapter)

    @Delete
    fun DeleteChapter(chapter: Chapter)

    @Update
    fun UpdateChapter(chapter: Chapter)


}