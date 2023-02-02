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

class ChapterAdapter(val context: Context, private val onClick: (chapter : com.example.lnreader.database.Chapter) -> Unit, var chapters: List<com.example.lnreader.database.Chapter>): RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View, val onClick: (chapter : com.example.lnreader.database.Chapter) -> Unit): RecyclerView.ViewHolder(itemView){
        var chapterTitle: TextView
        var img: ImageView
        init {
            chapterTitle = itemView.findViewById(R.id.chapterTitle)
            img = itemView.findViewById(R.id.readImg)

            itemView.setOnClickListener {
                chapters[adapterPosition].let {
                    onClick(it)
                }
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
        if(chapters[position].markedAsRead)
        {
            holder.img.setImageResource(R.drawable.ic_home_black_24dp)
        }
        else
        {
            holder.img.setImageResource(R.drawable.ic_notifications_black_24dp)
        }

    }

    override fun getItemCount(): Int {
        return chapters.size;
    }
}