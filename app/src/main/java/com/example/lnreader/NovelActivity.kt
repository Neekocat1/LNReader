package com.example.lnreader

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.NovelWithChapters
import com.example.lnreader.databinding.ActivityMainBinding
import com.example.lnreader.ui.chapter.ChapterAdapter
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

class NovelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<ChapterAdapter.ViewHolder>? = null
    private lateinit var novel: NovelWithChapters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novelview)
        var novelId = intent.getIntExtra("novelId", 0)


        //novel = JSONNovel(this, fileLoc)
        novel = AppDatabase.getDatabase(this).NovelDao().GetNovelWithChapters(novelId)

        var coverImg = findViewById<ImageView>(R.id.coverImg)
        var titleText = findViewById<TextView>(R.id.TitleText)


        coverImg.setImageDrawable(BitmapDrawable(resources, BitmapFactory.decodeFile((filesDir.absolutePath) + novel.novel.coverLocation)))
        titleText.text = novel.novel.title

        val recyclerView : RecyclerView = findViewById(R.id.ChapterList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ChapterAdapter(this, { chapter -> onClick(chapter)}, novel.chapters)
        recyclerView.adapter = adapter
        /*
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "Library"
        ).allowMainThreadQueries().build()
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
        }*/
    }

    fun onClick(chapter: com.example.lnreader.database.Chapter){
        //TODO: Change chapter opening to use database file location.
        Toast.makeText(this, chapter.title, Toast.LENGTH_SHORT).show()


        val pdfFile = File(getExternalFilesDir(null), chapter.fileLocation)
        val fileUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", pdfFile)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent)


    }
}