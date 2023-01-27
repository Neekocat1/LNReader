package com.example.lnreader.ui.chapter

interface ChapterInterface {
    var id : Int
    var title: String
    var fileLocation : String
    var markedAsRead : Boolean

    fun Save()
}