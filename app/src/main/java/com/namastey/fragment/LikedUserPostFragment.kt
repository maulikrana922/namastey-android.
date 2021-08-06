package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AlbumVideoActivity
import com.namastey.activity.ProfileViewActivity
import com.namastey.adapter.LikeProfileAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentLikedUserPostBinding
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.VideoBean
import com.namastey.uiView.ProfileLikeView
import com.namastey.utils.Constants
import com.namastey.viewModel.LikeProfileViewModel
import kotlinx.android.synthetic.main.fragment_liked_user_post.*
import java.util.*
import javax.inject.Inject


class LikedUserPostFragment : BaseFragment<FragmentLikedUserPostBinding>(), ProfileLikeView,
    OnSelectUserItemClick, OnPostImageClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentLikeUserPostBinding: FragmentLikedUserPostBinding
    private lateinit var likeProfileViewModel: LikeProfileViewModel
    private lateinit var layoutView: View
    private lateinit var likeProfileAdapter: LikeProfileAdapter

    override fun getViewModel() = likeProfileViewModel

    override fun getLayoutId() = R.layout.fragment_liked_user_post

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
        likeProfileViewModel.getLikedUserPost()
    }

    override fun onSuccess(data: ArrayList<VideoBean>) {
        Log.e("LikedUserPostFragment", "onSuccess: ${data.size}")
        likeProfileAdapter = LikeProfileAdapter(data, requireActivity(), this, this,resources.getString(R.string.received))
        rvLikeUserPost.adapter = likeProfileAdapter
    }

    override fun onSelectItemClick(
        userId: Long,
        position: Int
    ) {

        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
        TODO("Not yet implemented")
    }

    override fun onItemPostImageClick(position: Int, videoList: ArrayList<VideoBean>) {
        val intent = Intent(requireActivity(), AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoList)
        intent.putExtra("position", position)
        openActivity(intent)
    }

    override fun onDestroy() {
        likeProfileViewModel.onDestroy()
        super.onDestroy()
    }
}