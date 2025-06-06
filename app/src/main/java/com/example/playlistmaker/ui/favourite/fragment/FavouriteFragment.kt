package com.example.playlistmaker.ui.favourite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.ui.favourite.view_model.FavouriteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteTracksBinding
    private val viewModel by viewModel<FavouriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favouriteTracksEmptyPlaceholder.visibility = View.VISIBLE
    }

    companion object {
        private const val ARGS_FAVOURITE_ID = "favourite_id"

        fun createArgs(favouriteId: String): Bundle =
            bundleOf(ARGS_FAVOURITE_ID to favouriteId)

        fun newInstance() = FavouriteFragment()
    }
}