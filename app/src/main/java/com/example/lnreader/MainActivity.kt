package com.example.lnreader

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.Chapter
import com.example.lnreader.database.Novel
import com.example.lnreader.database.NovelWithChapters
import com.example.lnreader.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.html.HtmlToPdfConverter
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_library, R.id.navigation_download
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()

        var novels = this.let { AppDatabase.getDatabase(it).NovelDao().GetNovels() }

        for (novelit in novels)
        {
            if(novelit.autoupdate)
            {
                var novel = this.let { AppDatabase.getDatabase(it).NovelDao().GetNovelWithChapters(novelit.id) }
                if (! Python.isStarted()) {
                    Python.start(AndroidPlatform(this))
                }
                var py = Python.getInstance()
                val dl = py.getModule("boxnovel")

                var chReturn = dl.callAttr("GetChapterList", novel.novel.website).asList()

                //if chapter is not found in database, create new chapter and insert into database.
                for(item in chReturn)
                {
                    var foundChapter = novel.chapters.firstOrNull{it.title == item.callAttr("get", "title").toString()}
                    if(foundChapter == null)
                    {
                        var chapter = Chapter(0,  novel.novel.id, item.callAttr("get", "title").toString(), item.callAttr("get", "url").toString(), false, false)
                        novel.chapters.add(chapter)
                    }
                }
                val db = Room.databaseBuilder(
                    this,
                    AppDatabase::class.java, "Library"
                ).allowMainThreadQueries().build()
                for(chapter in novel.chapters.filter{it.id == 0})
                {
                    val chapterDao = db.ChapterDao()
                    chapterDao.InsertChapter(chapter)
                }
                val novelDao = db.NovelDao()
                novel.novel.lastUpdated = System.currentTimeMillis() / 1000
                novelDao.UpdateNovel(novel.novel)
                /*
                for(chapter in novel.chapters.filter{it.id == 0})
                {
                    chapter.novelId = novel.novel.id
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

                    val db = Room.databaseBuilder(
                        this,
                        AppDatabase::class.java, "Library"
                    ).allowMainThreadQueries().build()
                    val chapterDao = db.ChapterDao()
                    chapterDao.InsertChapter(chapter)
                    val novelDao = db.NovelDao()
                    novel.novel.lastUpdated = System.currentTimeMillis() / 1000
                    novelDao.UpdateNovel(novel.novel)
                }
                 */
            }
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