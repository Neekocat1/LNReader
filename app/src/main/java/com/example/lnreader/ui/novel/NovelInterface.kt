package com.example.lnreader.ui.novel

import android.graphics.drawable.Drawable
import com.example.lnreader.ui.chapter.Chapter


interface NovelInterface {
    var id : Int
    var title : String
    var website : String
    var cover : Drawable //TODO: make a img object
    var chapters : MutableList<Chapter>
    var autoupdate : Boolean
    var description : String
    var fileLocation: String

    public fun Save()
    public fun OpenWebsite()
    public fun OpenChapter(chapter : String)
    public fun ToggleUpdate()
    {
        autoupdate = !autoupdate
    }
    public fun MarkAsRead(chapter : String)

}