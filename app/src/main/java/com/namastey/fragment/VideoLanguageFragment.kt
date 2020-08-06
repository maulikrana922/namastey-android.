package com.namastey.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.SignUpActivity
import com.namastey.adapter.VideoLanguageAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentVideoLanguageBinding
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.VideoLanguageView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.VideoLanguageViewModel
import kotlinx.android.synthetic.main.fragment_video_language.*
import javax.inject.Inject

class VideoLanguageFragment : BaseFragment<FragmentVideoLanguageBinding>(), VideoLanguageView,VideoLanguageAdapter.OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentVideoLanguageBinding: FragmentVideoLanguageBinding
    private lateinit var videoLanguageViewModel: VideoLanguageViewModel
    private lateinit var layoutView: View
    private lateinit var videoLanguageAdapter: VideoLanguageAdapter
    private var selectVideoIdList: ArrayList<Int> = ArrayList()
    private var dob = ""

    override fun onClose() {
        fragmentManager!!.popBackStack()
    }

    override fun onNext() {
        if (selectVideoIdList.size >= 1) {
            (activity as SignUpActivity).addFragment(
                ChooseInterestFragment.getInstance(
                    dob,selectVideoIdList
                ),
                Constants.CHOOSE_INTEREST_FRAGMENT
            )
        }else{
            object : CustomAlertDialog(
                activity!!,
                resources.getString(R.string.msg_min_language), getString(R.string.ok), ""
            ){
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }

    override fun onSuccess(languageList: ArrayList<VideoLanguageBean>) {

        selectVideoIdList = ArrayList()
        rvVideoLanguage.addItemDecoration(GridSpacingItemDecoration(2, 10, false))
        videoLanguageAdapter = VideoLanguageAdapter(languageList, activity!!,this)
        rvVideoLanguage.adapter = videoLanguageAdapter
    }

    override fun getViewModel() = videoLanguageViewModel

    override fun getLayoutId() = R.layout.fragment_video_language

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(dob: String) =
            VideoLanguageFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.DATE_OF_BIRTH, dob)
                }
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

        var locale = ""
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).country
        } else {
            resources.configuration.locale.country
        }
        videoLanguageViewModel.getVideoLanguage(locale)

        if (arguments!!.containsKey(Constants.DATE_OF_BIRTH)){
            dob = arguments!!.getString(Constants.DATE_OF_BIRTH).toString()
        }
    }

    private fun setupViewModel() {
        videoLanguageViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            VideoLanguageViewModel::class.java
        )
        videoLanguageViewModel.setViewInterface(this)

        fragmentVideoLanguageBinding = getViewBinding()
        fragmentVideoLanguageBinding.viewModel = videoLanguageViewModel
    }

    override fun onDestroy() {
        videoLanguageViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean) {
        if (selectVideoIdList.contains(videoLanguageBean.id))
            selectVideoIdList.remove(videoLanguageBean.id)
        else
            selectVideoIdList.add(videoLanguageBean.id)
    }

}