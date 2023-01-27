package com.example.lnreader.ui.library

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lnreader.NovelActivity
import com.example.lnreader.R
import com.example.lnreader.database.AppDatabase
import com.example.lnreader.database.Novel
import com.example.lnreader.databinding.FragmentLibraryBinding
import com.example.lnreader.ui.chapter.Chapter
import org.json.JSONArray
import org.json.JSONTokener

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

        libraryGRV = binding.gridLibrary
        novelList = mutableListOf<LibraryGridModal>()

        val jsonString = context?.assets?.open("library.json")?.bufferedReader(Charsets.UTF_8)
            .use { it?.readText() }
        val jsonArray = JSONTokener(jsonString).nextValue() as JSONArray


        for (i in 0 until jsonArray.length())
        {
            var json = jsonArray.getJSONObject(i)
            var novelTitle = json.getString("title")
            var novelId = json.getInt("id")
            var novelCover : Drawable
            context?.assets?.open(json.getString("location") + "cover.png").use {
                novelCover = Drawable.createFromStream(it, null)!!
            }
            novelList.add(LibraryGridModal(novelTitle, novelCover, novelId, json.getString("location")))
        }


        val libraryAdapter = this.context?.let { LibraryGridAdapter(novelList = novelList, it) }

        libraryGRV.adapter = libraryAdapter
        libraryGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.
            Toast.makeText(
                activity, novelList[position].novelName + " selected",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this.context, NovelActivity::class.java)
            intent.putExtra("fileLoc", novelList[position].novelLocation)
            activity?.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}