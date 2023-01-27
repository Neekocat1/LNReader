package com.example.lnreader.ui.chapter

import android.content.Context
import android.content.res.AssetManager
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.nio.file.Paths

class Chapter(
    var context: Context,
    override var id: Int,
    override var title: String,
    override var fileLocation: String,
    override var markedAsRead: Boolean,
    var metaLoc: String
) : ChapterInterface {
    fun MarkRead(){
        markedAsRead = true
    }
    override fun Save() {
        val jsonString = context.assets.open(metaLoc).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val jsonObject = JSONTokener(jsonString).nextValue() as JSONObject
        val jsonChapters = jsonObject.getJSONArray("chapters")
        for (i in 0 until jsonChapters.length())
        {
            var chJson = jsonChapters.getJSONObject(i)
            if(chJson.getInt("id") == id)
            {
                chJson.put("read", markedAsRead)
            }
        }
        val output = File(context.applicationInfo.dataDir + "/src/main/assets/$metaLoc")
        val path = Paths.get(context.applicationInfo.dataDir+"/src")
        File(context.getExternalFilesDir(null)?.toURI() ?: null).walk().forEach {
            println(it)
        }
        /*output.bufferedWriter(Charsets.UTF_8).use {
            it.write(jsonObject.toString().toCharArray())
        }*/
    }
}