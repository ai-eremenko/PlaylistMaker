package com.example.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.app.MyApplication
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    companion object {
        fun newInstance() = SettingsFragment()
        const val TAG = "SettingsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareAppButton.setOnClickListener {
            viewModel.shareApp(getString(R.string.share_app_message))
        }

        binding.writeToSupportButton.setOnClickListener {
            viewModel.openSupport(
                EmailData(
                    email = getString(R.string.mail),
                    subject = getString(R.string.support_email_subject),
                    body = getString(R.string.support_email_body)
                )
            )
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms(getString(R.string.user_agreement_url))
        }
    }

    private fun observeViewModel() {
        viewModel.themeLiveData.observe(viewLifecycleOwner) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkTheme
            (requireActivity().applicationContext as MyApplication).switchTheme(themeSettings.isDarkTheme)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}