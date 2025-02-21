package com.example.playlistmaker

import android.view.View

var View.isGone: Boolean
    get() = this.visibility == View.GONE
    set(value) {
        this.visibility = if (value) View.GONE else View.VISIBLE
    }