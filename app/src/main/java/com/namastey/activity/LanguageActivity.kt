package com.namastey.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.VideoLanguageAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityLanguageBinding
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.ContentLanguageView
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ContentLanguageViewModel
import kotlinx.android.synthetic.main.activity_language.*
import java.util.*
import javax.inject.Inject

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(), ContentLanguageView,
    VideoLanguageAdapter.OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var contentLanguageViewModel: ContentLanguageViewModel
    private lateinit var activityLanguageBinding: ActivityLanguageBinding
    private lateinit var videoLanguageAdapter: VideoLanguageAdapter
    private var selectVideoIdList: ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

        initData()
    }

    private fun setupViewModel() {
        contentLanguageViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ContentLanguageViewModel::class.java)
        contentLanguageViewModel.setViewInterface(this)
        activityLanguageBinding = bindViewData()
        activityLanguageBinding.viewModel = contentLanguageViewModel
    }

    private fun initData() {
        var locale = "IN"
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).country
        } else {
            resources.configuration.locale.country
        }

        contentLanguageViewModel.getContentLanguage(locale)
    }

    override fun getViewModel() = contentLanguageViewModel


    override fun getLayoutId() = R.layout.activity_language

    override fun getBindingVariable() = BR.viewModel

    override fun onSuccess(languageList: ArrayList<VideoLanguageBean>) {

        selectVideoIdList = ArrayList()
        rvVideoLanguage.addItemDecoration(GridSpacingItemDecoration(2, 15, false))
        videoLanguageAdapter = VideoLanguageAdapter(languageList, this@LanguageActivity, selectVideoIdList,this,sessionManager)
        rvVideoLanguage.adapter = videoLanguageAdapter
    }

    override fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean) {
        if (selectVideoIdList.contains(videoLanguageBean.id)) {
            selectVideoIdList.remove(videoLanguageBean.id)
        } else {
            selectVideoIdList.add(videoLanguageBean.id)
        }
    }

    override fun onDestroy() {
        contentLanguageViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickContinue(view: View) {
        if (selectVideoIdList.size >= 1) {
            sessionManager.setVideoLanguageList(ArrayList())
            sessionManager.setVideoLanguageList(selectVideoIdList)

            openActivity(this@LanguageActivity, InterestActivity())
        } else {
            object : CustomAlertDialog(
                this,
                resources.getString(R.string.msg_min_language), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }

    }
}