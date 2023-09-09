package com.example.lnreader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.lnreader.database.*
import com.example.lnreader.databinding.ActivityMainBinding
import com.example.lnreader.ui.chapter.DownloadChapterAdapter
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.html.HtmlToPdfConverter
import java.io.File

class NovelDownloadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DownloadChapterAdapter.ViewHolder>? = null
    private lateinit var databaseNovelChapters : NovelWithChapters
    private lateinit var py : Python
    private lateinit var novelImg : ByteArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novelview)
        var novelUrl = intent.getStringExtra("novelUrl")
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        py = Python.getInstance()
        val dl = py.getModule("boxnovel")
        var dlReturn = dl.callAttr("GetNovelInfo", novelUrl)
        novelImg = dlReturn.callAttr("get", "img").toJava(ByteArray::class.java)
        val novelDescription = dlReturn.callAttr("get", "desc").toString() //TODO: Clean description
        val novelTitle = dlReturn.callAttr("get", "title").toString()

        //get novel from database or create new novel if not found in database
        var databaseNovel = AppDatabase.getDatabase(this).NovelDao().GetNovelFromTitle(novelTitle)
        if(databaseNovel != null)
        {
            databaseNovelChapters = AppDatabase.getDatabase(this).NovelDao().GetNovelWithChapters(databaseNovel.id)
        }
        else
        {
            val unixTimestamp = System.currentTimeMillis() / 1000
            databaseNovelChapters = NovelWithChapters(Novel(0, novelTitle, novelUrl?: "", "", false, novelDescription, unixTimestamp, unixTimestamp), mutableListOf())
        }
        var chReturn = dl.callAttr("GetChapterList", novelUrl).asList()

        //if chapter is not found in database, create new chapter and insert into database.
        for(item in chReturn)
        {
            var foundChapter = databaseNovelChapters.chapters.firstOrNull{it.title == item.callAttr("get", "title").toString()}
            if(foundChapter == null)
            {
                var chapter = Chapter(0,  databaseNovelChapters.novel.id, item.callAttr("get", "title").toString(), item.callAttr("get", "url").toString(), false, false)
                databaseNovelChapters.chapters.add(chapter)
            }
        }

        var coverImg = findViewById<ImageView>(R.id.coverImg)
        var titleText = findViewById<TextView>(R.id.TitleText)
        var descriptionText = findViewById<TextView>(R.id.DescriptionText)
        var updateButton = findViewById<Button>(R.id.autoUpdateButton)

        coverImg.setImageDrawable(BitmapDrawable(resources, BitmapFactory.decodeByteArray(novelImg, 0, novelImg.size)))
        titleText.text = novelTitle
        descriptionText.text = novelDescription
        updateButton.visibility = View.GONE
        val recyclerView : RecyclerView = findViewById(R.id.ChapterList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = DownloadChapterAdapter(this, { chapter -> onClick(chapter)}, databaseNovelChapters.chapters)
        recyclerView.adapter = adapter

    }

    fun onClick(chapter: com.example.lnreader.database.Chapter){
        Toast.makeText(this, chapter.title, Toast.LENGTH_SHORT).show()
        if(databaseNovelChapters.novel.id == 0) AddToDB()
        if(chapter.id != 0) return
        chapter.novelId = databaseNovelChapters.novel.id
        val dl = py.getModule("boxnovel")
        val chapterHTML = dl.callAttr("GetChapterContent", chapter.fileLocation).toString()
        val filePath =  databaseNovelChapters.novel.coverLocation + "pdf/"
        val fileName = java.util.UUID.randomUUID().toString()
        chapter.fileLocation = databaseNovelChapters.novel.coverLocation + "pdf/" + fileName + ".pdf"
        var chapterFile = File(getExternalFilesDir(null),
            chapter.fileLocation
        )
        chapterFile.createChapterFileAndDirs(getExternalFilesDir(null)?.path + databaseNovelChapters.novel.coverLocation + "pdf/")

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
        val novelDao = db.NovelDao()
        databaseNovelChapters.novel.lastUpdated = System.currentTimeMillis() / 1000
        novelDao.UpdateNovel(databaseNovelChapters.novel)
    }

    private fun AddToDB()
    {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "Library"
        ).allowMainThreadQueries().build()
        val novelDao = db.NovelDao()
        databaseNovelChapters.novel.coverLocation = "/" + java.util.UUID.randomUUID().toString() + "/"
        File((filesDir.absolutePath) + databaseNovelChapters.novel.coverLocation, "cover.png").writeBitmap(BitmapFactory.decodeByteArray(novelImg, 0, novelImg.size), Bitmap.CompressFormat.PNG, 100)
        databaseNovelChapters.novel.id = novelDao.InsertNovel(databaseNovelChapters.novel).toInt()
    }

    private fun File.createChapterFileAndDirs(dir: String) = apply {
        //val test = Files.createDirectories(Paths.get(dir))
        ////val test = parentFile?.mkdirs()
        //createNewFile()
        val dir: File = File(dir)
        if (!dir.exists()) dir.mkdirs()
        createNewFile()
    }
    private fun File.createFileAndDirs() = apply {
        //val test = Files.createDirectories(Paths.get(dir))
        val test = parentFile?.mkdirs()
        createNewFile()
    }
    private fun File.writeBitmap(
        bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 80
    ) = apply {
        createFileAndDirs()
        outputStream().use {
            bitmap.compress(format, quality, it)
            it.flush()
        }
    }
}