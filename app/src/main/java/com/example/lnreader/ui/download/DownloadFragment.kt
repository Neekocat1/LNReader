package com.example.lnreader.ui.download

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.lnreader.NovelActivity
import com.example.lnreader.NovelDownloadActivity
import com.example.lnreader.databinding.FragmentDownloadBinding


class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var downloadGRV: GridView
    lateinit var novelList : MutableList<DownloadGridModal>
    var retNovels = mutableListOf<MutableMap<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val downloadViewModel =
            ViewModelProvider(this).get(DownloadViewModel::class.java)

        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        val root: View = binding.root


        if (! Python.isStarted()) {
            context?.let { AndroidPlatform(it) }?.let { Python.start(it) };
        }
        binding.searchBar.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var temp = binding.searchBar.text.toString()
                if(binding.searchBar.text.toString().matches(Regex("(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")))
                {
                    val intent = Intent(this.context, NovelDownloadActivity::class.java)
                    intent.putExtra("novelUrl", binding.searchBar.text.toString())
                    activity?.startActivity(intent)
                    return@OnEditorActionListener true
                }
                val py = Python.getInstance()
                val dl = py.getModule("boxnovel")
                var dlReturn = dl.callAttr("SearchNovels", binding.searchBar.text.toString()).asList()
                for(item in dlReturn)
                {
                    var novel = mutableMapOf<String, Any>()
                    novel["url"] = item.callAttr("get", "url").toString()
                    novel["coverImg"] = item.callAttr("get", "coverImg").toJava(ByteArray::class.java)
                    novel["title"] = item.callAttr("get", "title").toString()
                    retNovels.add(novel)
                }
                downloadGRV = binding.gridDownload
                novelList = mutableListOf<DownloadGridModal>()

                if (retNovels.isNotEmpty()) {
                    for(novel in retNovels)
                    {
                        val bytes = novel["coverImg"] as ByteArray
                        val novelCover = BitmapDrawable(resources, BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                        var libNovel = DownloadGridModal(novel["title"] as String, novelCover, novel["url"] as String)
                        novelList.add(libNovel)
                    }
                }

                val downloadAdapter = this.context?.let { DownloadGridAdapter(novelList = novelList, it) }

                downloadGRV.adapter = downloadAdapter
                downloadGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this.context, NovelDownloadActivity::class.java)
                    intent.putExtra("novelUrl", novelList[position].novelUrl)
                    activity?.startActivity(intent)
                }
                return@OnEditorActionListener true
            }
            false
        })







        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}