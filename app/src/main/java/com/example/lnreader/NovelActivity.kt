package com.example.lnreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.room.Room
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.Novel
import com.example.lnreader.databinding.ActivityMainBinding
import com.example.lnreader.ui.chapter.Chapter
import com.example.lnreader.ui.chapter.ChapterAdapter
import com.example.lnreader.ui.novel.JSONNovel
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

class NovelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<ChapterAdapter.ViewHolder>? = null
    private lateinit var novel: JSONNovel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novelview)
        var fileLoc = intent.getStringExtra("fileLoc") ?: "Not Found"


        novel = JSONNovel(this, fileLoc)
        var coverImg = findViewById<ImageView>(R.id.coverImg)
        var titleText = findViewById<TextView>(R.id.TitleText)


        coverImg.setImageDrawable(novel.cover)
        titleText.text = novel.title

        val recyclerView : RecyclerView = findViewById(R.id.ChapterList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ChapterAdapter({ chapter -> onClick(chapter)}, novel.chapters)
        recyclerView.adapter = adapter

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "Library"
        ).build()
        val novelDao = db.NovelDao()
        val chapterDao = db.ChapterDao()
        var dbnovel = Novel(0, novel.title, novel.website,
            "temp", novel.autoupdate, novel.description)
        novelDao.InsertNovel(dbnovel)
        for(chpt in novel.chapters)
        {
            var chapter = com.example.lnreader.database.Chapter(0, dbnovel.id, chpt.title,
                "temp", chpt.markedAsRead)
            chapterDao.InsertChapter(chapter)
        }
    }

    fun onClick(chapter: Chapter){
        Toast.makeText(this, chapter.title, Toast.LENGTH_SHORT).show()
        var path = Paths.get("").toAbsolutePath().toString()
        val loc = chapter.fileLocation
        val pdfInputStream = assets.open(loc)

        val pdfFile = File(getExternalFilesDir(null), "example.pdf")
        val fileUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", pdfFile)
        pdfFile.createNewFile()
        val outputStream = FileOutputStream(pdfFile)

        val buffer = ByteArray(1024)
        var read: Int
        while (pdfInputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        pdfInputStream.close()
        outputStream.flush()
        outputStream.close()
        //val pdfFile = File(chapter.fileLocation)
        //val pdfFile = assets.open(chapter.fileLocation)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent)
        /*
        val pdfFile = File("/" + chapter.fileLocation)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)*/

    }
}