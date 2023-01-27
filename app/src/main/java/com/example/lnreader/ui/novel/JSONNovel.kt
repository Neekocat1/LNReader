package com.example.lnreader.ui.novel

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.lnreader.ui.chapter.Chapter
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.InputStream
import kotlin.properties.Delegates

class JSONNovel(context: Context, fileLoc: String) : NovelInterface {


    override var id by Delegates.notNull<Int>()
    override lateinit var title: String
    override lateinit var website: String
    override lateinit var cover: Drawable
    override lateinit var chapters: MutableList<Chapter>
    override var autoupdate: Boolean = false
    override lateinit var description: String
    override var fileLocation = fileLoc

    init
    {

        val jsonString = context.assets.open(fileLoc + "meta.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val jsonObject = JSONTokener(jsonString).nextValue() as JSONObject
        website = jsonObject.getString("url")
        title = jsonObject.getString("title")
        context.assets.open(fileLoc + "cover.png").use {
            cover = Drawable.createFromStream(it, null)!!
        }
        description = jsonObject.getString("author")
        autoupdate = false
        chapters = mutableListOf()
        val jsonChapters = jsonObject.getJSONArray("chapters")
        for (i in 0 until jsonChapters.length())
        {
            var chJson = jsonChapters.getJSONObject(i)
            var chId = chJson.getInt("id")
            var chName = chJson.getString("title")
            var chLoc = fileLoc + "pdf/" + chJson.getString("location")
            var chMAR = chJson.getBoolean("read")
            chapters.add(Chapter(context, chId, chName, chLoc, chMAR, fileLoc + "meta.json"))
        }

    }

    public override fun Save()
    {

    }

    public override fun OpenWebsite()
    {

    }

    public override fun OpenChapter(chapter : String)
    {

    }

    public override fun ToggleUpdate()
    {
        autoupdate = !autoupdate
    }

    public override fun MarkAsRead(chapter : String)
    {

    }

}