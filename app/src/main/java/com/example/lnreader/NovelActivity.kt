package com.example.lnreader

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.lnreader.databinding.ActivityMainBinding

class NovelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novelview)

        var coverImg = findViewById<ImageView>(R.id.coverImg)
        var titleText = findViewById<TextView>(R.id.TitleText)

        coverImg.setImageResource(intent.getIntExtra("Img", R.drawable.test_img_1))
        titleText.text = intent.getStringExtra("Title")


    }
}