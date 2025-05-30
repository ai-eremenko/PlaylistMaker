package com.example.playlistmaker.domain.models

import androidx.fragment.app.Fragment

data class TabData(
    val titleResId: Int,
    val fragmentCreator: () -> Fragment
)