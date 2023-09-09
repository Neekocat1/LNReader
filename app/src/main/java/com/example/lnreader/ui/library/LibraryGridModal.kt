package com.example.lnreader.ui.library

import android.graphics.drawable.Drawable

data class LibraryGridModal(
    val novelName: String,
    val novelCover: Drawable,
    val novelId: Int,
    val novelUpdated: Long,
    val novelAccessed: Long
)
