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
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ContentLanguageViewModel
import kotlinx.android.synthetic.main.fragment_content_language.*
import java.util.*
import javax.inject.Inject


class ContentLanguageFragment : BaseFragment<FragmentContentLanguageBinding>(), ContentLanguageView,
    ContentLanguageAdapter.OnItemClick {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentContentLanguageBinding: FragmentContentLanguageBinding
    private lateinit var contentLanguageViewModel: ContentLanguageViewModel
    private lateinit var layoutView: View
    private lateinit var notificationAdapter: ContentLanguageAdapter
    private var selectLanguageIdList: ArrayList<Int> = ArrayList()
    private var selectedLanguageList: ArrayList<VideoLanguageBean> = ArrayList()

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
        var locale = "IN"
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).country
        } else {
            resources.configuration.locale.country
        }

        selectLanguageIdList = ArrayList()

        for(languageListId in sessionManager.getLanguageList()){
            selectLanguageIdList.add(languageListId.id)
            selectedLanguageList.add(languageListId)
        }

//        Log.e("ContentLanguage", "selectedLanguageList: \t  $selectedLanguageList")
//        Log.e("ContentLanguage", "selectedLanguageId: \t  $selectLanguageIdList")
        contentLanguageViewModel.getContentLanguage(locale)
    }

    override fun onSuccess(languageList: ArrayList<VideoLanguageBean>) {
//        Log.e("ContentLanguage", "onSuccess: \t languageList: $languageList")

        notificationAdapter = ContentLanguageAdapter(requireActivity(), languageList, selectLanguageIdList, this)
        rvContentLanguages.adapter = notificationAdapter
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.content_languages))
    }

    override fun onDestroy() {
        contentLanguageViewModel.onDestroy()
        (activity as AccountSettingsActivity).setVisibilitySave(false)
        super.onDestroy()
    }

    override fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean) {
        (activity as AccountSettingsActivity).setVisibilitySave(true)
        if (selectLanguageIdList.contains(videoLanguageBean.id)) {
//            Log.e("ContentLanguage", "onLanguageItemClick: \t Remove")
            selectLanguageIdList.remove(videoLanguageBean.id)
            val position = selectedLanguageList.indexOfFirst { it.id == videoLanguageBean.id }
            selectedLanguageList.removeAt(position)

        } else {
//            Log.e("ContentLanguage", "onLanguageItemClick: \t Add")
            selectLanguageIdList.add(videoLanguageBean.id)
            selectedLanguageList.add(videoLanguageBean)
        }
        (activity as AccountSettingsActivity).setSelectedLanguage(selectLanguageIdList,selectedLanguageList)
    }
}