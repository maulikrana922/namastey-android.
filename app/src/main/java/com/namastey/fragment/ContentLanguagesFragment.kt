package com.namastey.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.adapter.VideoLanguageAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentContentLanguagesBinding
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.ContentLanguageView
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ContentLanguageViewModel
import kotlinx.android.synthetic.main.activity_language.*
import java.util.ArrayList
import javax.inject.Inject


class ContentLanguagesFragment : BaseFragment<FragmentContentLanguagesBinding>(),
    ContentLanguageView,
    VideoLanguageAdapter.OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentContentLanguagesBinding: FragmentContentLanguagesBinding
    private lateinit var contentLanguageViewModel: ContentLanguageViewModel
    private lateinit var videoLanguageAdapter: VideoLanguageAdapter
    private lateinit var layoutView: View
    private var selectLanguageIdList: ArrayList<Int> = ArrayList()
    private var selectedLanguageList: ArrayList<VideoLanguageBean> = ArrayList()

    override fun getViewModel() = contentLanguageViewModel

    override fun getLayoutId() = R.layout.fragment_content_languages

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            ContentLanguagesFragment().apply {
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

    private fun initUI() {
      //  Log.d("selectedlanguage",selectLanguageIdList.toString())

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

      //  sessionManager.setLanguageIdList(selectLanguageIdList)

       Log.e("ContentLanguage", "selectedLanguageList: \t  $selectedLanguageList")
       Log.e("ContentLanguage", "selectedLanguageId: \t  $selectLanguageIdList")
        contentLanguageViewModel.getContentLanguage(locale)
    }

    private fun setupViewModel() {
        contentLanguageViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ContentLanguageViewModel::class.java)
        contentLanguageViewModel.setViewInterface(this)
        fragmentContentLanguagesBinding = getViewBinding()
        fragmentContentLanguagesBinding.viewModel = contentLanguageViewModel
    }

    override fun onSuccess(languageList: ArrayList<VideoLanguageBean>) {
        (activity as AccountSettingsActivity).setVisibilitySave(true)
        selectLanguageIdList = ArrayList()
        rvVideoLanguage.addItemDecoration(GridSpacingItemDecoration(2, 15, false))
        videoLanguageAdapter = VideoLanguageAdapter(languageList,requireActivity(),selectLanguageIdList,this,sessionManager)
        rvVideoLanguage.adapter = videoLanguageAdapter    }

    override fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean) {
        for (i in selectedLanguageList){
            selectLanguageIdList.add(i.id)
        }
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

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.content_languages))
    }

    override fun onDestroy() {
        contentLanguageViewModel.onDestroy()
        (activity as AccountSettingsActivity).setVisibilitySave(false)
        super.onDestroy()
    }

}
