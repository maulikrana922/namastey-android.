package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.LikeProfileAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentLikeUserPostBinding
import com.namastey.listeners.OnItemClick
import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import com.namastey.uiView.ProfileLikeView
import com.namastey.viewModel.LikeProfileViewModel
import kotlinx.android.synthetic.main.fragment_like_user_post.*
import java.util.*
import javax.inject.Inject


class LikeUserPostFragment : BaseFragment<FragmentLikeUserPostBinding>(), ProfileLikeView,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentLikeUserPostBinding: FragmentLikeUserPostBinding
    private lateinit var likeProfileViewModel: LikeProfileViewModel
    private lateinit var layoutView: View
    private lateinit var likeProfileAdapter: LikeProfileAdapter

    override fun getViewModel() = likeProfileViewModel

    override fun getLayoutId() = R.layout.fragment_like_user_post

    override fun getBindingVariable() = BR.viewModel

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
        likeProfileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LikeProfileViewModel::class.java)
        likeProfileViewModel.setViewInterface(this)

        fragmentLikeUserPostBinding = getViewBinding()
        fragmentLikeUserPostBinding.viewModel = likeProfileViewModel
    }

    private fun initUI() {
        likeProfileViewModel.getLikeUserPost()
    }

    override fun onSuccess(data: ArrayList<VideoBean>) {
        Log.e("LikeUserPostFragment", "onSuccess: ${data.size}")
        likeProfileAdapter = LikeProfileAdapter(data, requireActivity(), this)
        rvLikeUserPost.adapter = likeProfileAdapter
    }

    override fun onItemClick(value: Long, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        likeProfileViewModel.onDestroy()
        super.onDestroy()
    }
}