package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View, private val onClick: (Track) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val sourceImageView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.trackName)
        artistNameView = itemView.findViewById(R.id.artistName)
        trackTimeView = itemView.findViewById(R.id.trackTime)
        sourceImageView = itemView.findViewById(R.id.sourceImage)
    }

    fun bind(model: Track) {
        itemView.setOnClickListener {
            onClick(model)
        }
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = formatDuration(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(itemView.context.toPx(2)))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(sourceImageView)
    }
    }
        private fun formatDuration (durationMillis: Long): String {
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            return dateFormat.format(durationMillis)
        }


    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()

