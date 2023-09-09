package com.example.lnreader.ui.chapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lnreader.R
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.Chapter

class DownloadChapterAdapter(val context: Context, private val onClick: (chapter : Chapter) -> Unit, var chapters: List<com.example.lnreader.database.Chapter>): RecyclerView.Adapter<DownloadChapterAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View, val onClick: (chapter : Chapter) -> Unit): RecyclerView.ViewHolder(itemView){
        var chapterTitle: TextView
        var img: ImageView
        init {
            chapterTitle = itemView.findViewById(R.id.chapterTitle)
            img = itemView.findViewById(R.id.readImg)

            itemView.setOnClickListener {
                onClick(chapters[adapterPosition])
            }
            itemView.setOnLongClickListener{
                chapters[adapterPosition].markedAsRead = !chapters[adapterPosition].markedAsRead
                AppDatabase.getDatabase(context).ChapterDao().InsertChapter(chapters[adapterPosition])
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chapter_item, parent, false)
        return ViewHolder(v, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chapterTitle.text = chapters[position].title
        updateIcon(holder, position)

    }

    fun updateIcon(holder: ViewHolder, position: Int)
    {
        if(chapters[position].isDownloaded)
        {
            holder.img.setImageResource(R.drawable.baseline_download_done_24)
        }
        else
        {
            holder.img.setImageResource(R.drawable.baseline_download_24)
        }
    }

    override fun getItemCount(): Int {
        return chapters.size;
    }
}