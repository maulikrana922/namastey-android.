package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentContentLanguageBinding
import com.namastey.uiView.ContentLanguageView
import com.namastey.viewModel.ContentLanguageViewModel
import javax.inject.Inject


class ContentLanguageFragment : BaseFragment<FragmentContentLanguageBinding>(), ContentLanguageView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentContentLanguageBinding: FragmentContentLanguageBinding
    private lateinit var contentLanguageViewModel: ContentLanguageViewModel
    private lateinit var layoutView: View

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
    }

    private fun setupViewModel() {
        contentLanguageViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ContentLanguageViewModel::class.java)
        fragmentContentLanguageBinding = getViewBinding()
        fragmentContentLanguageBinding.viewModel = contentLanguageViewModel
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