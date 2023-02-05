package com.example.lnreader.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.lnreader.databinding.FragmentDownloadBinding

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val downloadViewModel =
            ViewModelProvider(this).get(DownloadViewModel::class.java)

        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textDownload
        downloadViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        if (! Python.isStarted()) {
            context?.let { AndroidPlatform(it) }?.let { Python.start(it) };
        }
        val py = Python.getInstance()



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}