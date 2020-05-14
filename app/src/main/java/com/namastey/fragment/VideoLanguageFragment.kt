package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.SignUpActivity
import com.namastey.adapter.VideoLanguageAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentVideoLanguageBinding
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.VideoLanguageView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.VideoLanguageViewModel
import kotlinx.android.synthetic.main.fragment_video_language.*
import javax.inject.Inject

class VideoLanguageFragment : BaseFragment<FragmentVideoLanguageBinding>(), VideoLanguageView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentVideoLanguageBinding: FragmentVideoLanguageBinding
    private lateinit var videoLanguageViewModel: VideoLanguageViewModel
    private lateinit var layoutView: View
    private val languageList: ArrayList<VideoLanguageBean> = ArrayList()
    private lateinit var videoLanguageAdapter: VideoLanguageAdapter

    override fun onClose() {
        fragmentManager!!.popBackStack()
    }

    override fun onNext() {
        (activity as SignUpActivity).addFragment(
            ChooseInterestFragment.getInstance(
                "user"
            ),
            Constants.CHOOSE_INTEREST_FRAGMENT
        )
    }

    override fun getViewModel() = videoLanguageViewModel

    override fun getLayoutId() = R.layout.fragment_video_language

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            VideoLanguageFragment().apply {
                arguments = Bundle().apply {
                    putString("user", title)
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

        setLanguageList()

    }

    private fun setLanguageList() {

//        Set static data for UI
        for (int in 0..10) {
            var languageBean: VideoLanguageBean = VideoLanguageBean()
            languageBean.first_language = "English"
            languageBean.second_language = "english"
            languageList.add(languageBean)
        }
//        Set static data for UI

        rvVideoLanguage.addItemDecoration(GridSpacingItemDecoration(2, 10, false))
        videoLanguageAdapter = VideoLanguageAdapter(languageList, activity!!)
        rvVideoLanguage.adapter = videoLanguageAdapter
    }

    private fun setupViewModel() {
        videoLanguageViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            VideoLanguageViewModel::class.java
        )
        videoLanguageViewModel.setViewInterface(this)

        fragmentVideoLanguageBinding = getViewBinding()
        fragmentVideoLanguageBinding.viewModel = videoLanguageViewModel
    }
}