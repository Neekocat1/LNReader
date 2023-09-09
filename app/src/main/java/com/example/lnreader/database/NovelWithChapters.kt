package com.example.lnreader.database

import androidx.room.Embedded
import androidx.room.Relation

data class NovelWithChapters(
    @Embedded val novel: Novel,
    @Relation(
        parentColumn = "id",
        entityColumn = "novelId"
    )
    val chapters: MutableList<Chapter>
    ) {
}