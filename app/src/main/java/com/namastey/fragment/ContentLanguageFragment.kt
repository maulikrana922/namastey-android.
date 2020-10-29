package com.namastey.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.adapter.ContentLanguageAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentContentLanguageBinding
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.ContentLanguageView
import com.namastey.viewModel.ContentLanguageViewModel
import kotlinx.android.synthetic.main.fragment_content_language.*
import javax.inject.Inject


class ContentLanguageFragment : BaseFragment<FragmentContentLanguageBinding>(), ContentLanguageView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentContentLanguageBinding: FragmentContentLanguageBinding
    private lateinit var contentLanguageViewModel: ContentLanguageViewModel
    private lateinit var layoutView: View
    private lateinit var notificationAdapter: ContentLanguageAdapter


    override fun getViewModel() = contentLanguageViewModel

    override fun getLayoutId() = R.layout.fragment_content_language

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            ContentLanguageFragment().apply {
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

        initUI()
    }

    private fun setupViewModel() {
        contentLanguageViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ContentLanguageViewModel::class.java)
        contentLanguageViewModel.setViewInterface(this)
        fragmentContentLanguageBinding = getViewBinding()
        fragmentContentLanguageBinding.viewModel = contentLanguageViewModel
    }

    private fun initUI() {
        var locale = ""
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).country
        } else {
            resources.configuration.locale.country
        }

        contentLanguageViewModel.getContentLanguage(locale)
    }

    override fun onSuccess(languageList: ArrayList<VideoLanguageBean>) {
        Log.e("ContentLanguage", "onSuccess: \t languageList: $languageList")
        notificationAdapter = ContentLanguageAdapter(requireActivity(), languageList)
        rvContentLanguages.adapter = notificationAdapter
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.content_languages))
    }

    override fun onDestroy() {
        contentLanguageViewModel.onDestroy()
        super.onDestroy()
    }
}