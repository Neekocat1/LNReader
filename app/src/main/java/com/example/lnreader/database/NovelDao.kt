package com.example.lnreader.database

import androidx.room.*

@Dao
interface NovelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertNovel(novel: Novel) : Long

    @Delete
    fun DeleteNovel(novel: Novel)

    @Update
    fun UpdateNovel(novel: Novel)

    @Query("SELECT * FROM Novel")
    fun GetNovels(): List<Novel>

    @Query("SELECT * FROM Novel WHERE id = :id")
    fun GetNovelWithChapters(id: Int) : NovelWithChapters

    @Query("SELECT * FROM Novel WHERE title = :title")
    fun GetNovelFromTitle(title: String) : Novel?
}