package com.example.lnreader

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.chaquo.python.Python
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.NovelWithChapters
import com.example.lnreader.databinding.ActivityMainBinding
import com.example.lnreader.ui.chapter.ChapterAdapter
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.html.HtmlToPdfConverter
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
        supportActionBar?.hide()


        //novel = JSONNovel(this, fileLoc)
        novel = AppDatabase.getDatabase(this).NovelDao().GetNovelWithChapters(novelId)

        var coverImg = findViewById<ImageView>(R.id.coverImg)
        var titleText = findViewById<TextView>(R.id.TitleText)
        var descriptionText = findViewById<TextView>(R.id.DescriptionText)

        coverImg.setImageDrawable(BitmapDrawable(resources, BitmapFactory.decodeFile((filesDir.absolutePath) + novel.novel.coverLocation + "cover.png")))
        titleText.text = novel.novel.title
        descriptionText.text = novel.novel.description

        val recyclerView : RecyclerView = findViewById(R.id.ChapterList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ChapterAdapter(this, { chapter -> onClick(chapter)}, novel.chapters)
        recyclerView.adapter = adapter

        var updaterButton = findViewById<Button>(R.id.autoUpdateButton)
        if(novel.novel.autoupdate)
        {
            updaterButton.text = "Updates Enabled"
        }
        else
        {
            updaterButton.text = "Updates Disabled"
        }
        updaterButton.setOnClickListener {
            val db = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "Library"
            ).allowMainThreadQueries().build()
            val novelDao = db.NovelDao()
            novel.novel.autoupdate = !novel.novel.autoupdate
            if(novel.novel.autoupdate)
            {
                updaterButton.text = "Updates Enabled"
            }
            else
            {
                updaterButton.text = "Updates Disabled"
            }
            novelDao.UpdateNovel(novel.novel)
        }

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
        if(!chapter.isDownloaded)
        {
            chapter.novelId = novel.novel.id
            var py = Python.getInstance()
            val dl = py.getModule("boxnovel")
            val chapterHTML = dl.callAttr("GetChapterContent", chapter.fileLocation).toString()
            val filePath =  novel.novel.coverLocation + "pdf/"
            val fileName = java.util.UUID.randomUUID().toString()
            chapter.fileLocation = novel.novel.coverLocation + "pdf/" + fileName + ".pdf"
            var chapterFile = File(getExternalFilesDir(null),
                chapter.fileLocation
            )
            chapterFile.createChapterFileAndDirs(getExternalFilesDir(null)?.path + novel.novel.coverLocation + "pdf/")

            HtmlToPdfConverter.fromHTMLString(this, chapterHTML)
                // Configure title for the created document.
                .title("Converted document")
                // Perform the conversion.
                .convertToPdfAsync(chapterFile)
                .subscribe({
                    // Open and process the converted document.
                    val document = PdfDocumentLoader.openDocument(this, Uri.fromFile(chapterFile))
                }, {
                    // Handle the error.
                })
            chapter.isDownloaded = true

            val db = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "Library"
            ).allowMainThreadQueries().build()
            val chapterDao = db.ChapterDao()
            chapterDao.InsertChapter(chapter)
            novel.novel.lastAccessed = System.currentTimeMillis() / 1000
            val novelDao = db.NovelDao()
            novelDao.UpdateNovel(novel.novel)
            Toast.makeText(this, chapter.title + " Downloaded", Toast.LENGTH_SHORT).show()
        }
        else {
            chapter.markedAsRead = true
            val db = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "Library"
            ).allowMainThreadQueries().build()
            val chapterDao = db.ChapterDao()
            chapterDao.InsertChapter(chapter)
            novel.novel.lastAccessed = System.currentTimeMillis() / 1000
            val novelDao = db.NovelDao()
            novelDao.UpdateNovel(novel.novel)

            val pdfFile = File(getExternalFilesDir(null), chapter.fileLocation)
            val fileUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }


    }
    private fun File.createChapterFileAndDirs(dir: String) = apply {
        //val test = Files.createDirectories(Paths.get(dir))
        ////val test = parentFile?.mkdirs()
        //createNewFile()
        val dir: File = File(dir)
        if (!dir.exists()) dir.mkdirs()
        createNewFile()
    }
}