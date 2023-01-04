package com.example.lnreader.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lnreader.R
import com.example.lnreader.databinding.FragmentLibraryBinding
import com.example.lnreader.library.LibraryGridAdapter
import com.example.lnreader.library.LibraryGridModal

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var libraryGRV: GridView
    lateinit var novelList : List<LibraryGridModal>

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
        novelList = ArrayList<LibraryGridModal>()
        novelList += LibraryGridModal("Test1", R.drawable.test_img_1)
        novelList += LibraryGridModal("Test2", R.drawable.test_img_2)
        novelList += LibraryGridModal("Test3", R.drawable.test_img_3)
        novelList += LibraryGridModal("Test4", R.drawable.test_img_4)
        novelList += LibraryGridModal("Test5", R.drawable.test_img_5)
        novelList += LibraryGridModal("Test6", R.drawable.test_img_6)
        novelList += LibraryGridModal("Test7", R.drawable.test_img_7)


        val libraryAdapter = this.context?.let { LibraryGridAdapter(novelList = novelList, it) }

        libraryGRV.adapter = libraryAdapter
        libraryGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.
            Toast.makeText(
                activity, novelList[position].novelName + " selected",
                Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}