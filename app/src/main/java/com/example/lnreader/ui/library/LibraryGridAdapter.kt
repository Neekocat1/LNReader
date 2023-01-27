package com.example.lnreader.ui.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.lnreader.R

internal class LibraryGridAdapter(
    private val novelList: List<LibraryGridModal>,
    private val context: Context
) :
    BaseAdapter() {
        private var layoutInflater: LayoutInflater? = null
        private lateinit var courseTV: TextView
        private lateinit var courseIV: ImageView

        override fun getCount(): Int {
            return novelList.size
        }
        // below function is use to return the item of grid view.
        override fun getItem(position: Int): Any? {
            return null
        }

        // below function is use to return item id of grid view.
        override fun getItemId(position: Int): Long {
            return 0
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            // on blow line we are checking if layout inflater
            // is null, if it is null we are initializing it.
            if (layoutInflater == null) {
                layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            // on the below line we are checking if convert view is null.
            // If it is null we are initializing it.
            if (convertView == null) {
                // on below line we are passing the layout file
                // which we have to inflate for each item of grid view.
                convertView = layoutInflater!!.inflate(R.layout.novel_item, null)
            }
            // on below line we are initializing our course image view
            // and course text view with their ids.
            courseIV = convertView!!.findViewById(R.id.idIVCourse)
            courseTV = convertView!!.findViewById(R.id.idTVCourse)
            // on below line we are setting image for our course image view.
            courseIV.setImageDrawable(novelList[position].novelCover)
            // on below line we are setting text in our course text view.
            courseTV.text = novelList[position].novelName
            // at last we are returning our convert view.
            return convertView
        }
    }