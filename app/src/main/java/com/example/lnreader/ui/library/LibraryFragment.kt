package com.example.lnreader.ui.library

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.lnreader.NovelActivity
import com.example.lnreader.NovelDownloadActivity
import com.example.lnreader.R
import com.example.lnreader.SettingsActivity
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var libraryGRV: GridView
    lateinit var novelList : MutableList<LibraryGridModal>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //setup filter popup
        var filterButton = root.findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener{
            var popup = PopupMenu(context, filterButton)
            popup.inflate(R.menu.filter_menu)
            popup.setOnMenuItemClickListener {
                buildLibrary(it.title.toString())
                true
            }
            popup.show()
        }
        //setup settings button
        var settingsButton = root.findViewById<ImageButton>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this.context, SettingsActivity::class.java)
            activity?.startActivity(intent)

        }
        val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        if (sharedPreferences != null) {
            sharedPreferences.getString("filterDefault", "Title")?.let { buildLibrary(it) }
        }
        else
        {
            buildLibrary("Title")
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildLibrary(filterOrder : String)
    {
        libraryGRV = binding.gridLibrary
        novelList = mutableListOf<LibraryGridModal>()
        var novels = this.context?.let { AppDatabase.getDatabase(it).NovelDao().GetNovels() }
        if (novels != null) {
            for(novel in novels)
            {
                val novelCover = BitmapDrawable(resources, BitmapFactory.decodeFile((context?.filesDir?.absolutePath ?: "") + novel.coverLocation + "cover.png"))
                var libNovel = LibraryGridModal(novel.title, novelCover,novel.id, novel.lastUpdated, novel.lastAccessed)
                novelList.add(libNovel)
            }
        }
        when(filterOrder)
        {
            "Title" -> novelList = novelList.sortedBy { it.novelName }.toMutableList()
            "Recent" -> novelList = novelList.sortedByDescending { it.novelAccessed }.toMutableList()
            "Newest" -> novelList = novelList.sortedByDescending { it.novelUpdated }.toMutableList()
        }
        val libraryAdapter = this.context?.let { LibraryGridAdapter(novelList = novelList, it) }

        libraryGRV.adapter = libraryAdapter
        libraryGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this.context, NovelActivity::class.java)
            intent.putExtra("novelId", novelList[position].novelId)
            activity?.startActivity(intent)
        }
    }

}