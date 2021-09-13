package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AlbumDetailActivity
import com.namastey.activity.CreateAlbumActivity
import com.namastey.adapter.AlbumListAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAlbumBinding
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.uiView.AlbumView
import com.namastey.utils.Constants
import com.namastey.viewModel.AlbumViewModel
import kotlinx.android.synthetic.main.fragment_album.*
import java.util.*
import javax.inject.Inject

class AlbumFragment : BaseFragment<FragmentAlbumBinding>(), AlbumView, View.OnClickListener,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAlbumBinding: FragmentAlbumBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var layoutView: View

    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        rvAlbumList.apply {
            adapter = AlbumListAdapter(arrayList, requireActivity(), this@AlbumFragment)
        }

    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
    }

    override fun onSuccessGetComment(data: ArrayList<CommentBean>) {
    }

    override fun onSuccessSavePost(msg: String) {
    }

    override fun onSuccessReport(msg: String) {

    }

    override fun onSuccessBlockUser(msg: String) {
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
    }

    override fun onFailedMaxLike(msg: String, error: Int) {

    }

    override fun onSuccessPostShare(msg: String) {
    }

    override fun onSuccessDeletePost() {

    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {

    }

    override fun onSuccessStartChat(msg: String) {
        TODO("Not yet implemented")
    }

    override fun getViewModel() = albumViewModel

    override fun getLayoutId() = R.layout.fragment_album

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initData()
    }

    private fun initData() {

        btnAddAlbum.setOnClickListener(this)
//        albumViewModel.getAlbumList()
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume : ", "onresume")
        albumViewModel.getAlbumList()

    }
    private fun setupViewModel() {
        albumViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            AlbumViewModel::class.java
        )
        albumViewModel.setViewInterface(this)

        fragmentAlbumBinding = getViewBinding()
        fragmentAlbumBinding.viewModel = albumViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        albumViewModel.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v) {
            btnAddAlbum -> {
                val intent = Intent(requireActivity(), CreateAlbumActivity::class.java)
                intent.putExtra("fromAlbumList", true)
                openActivity(intent)
            }
        }
    }

    override fun onItemClick(id: Long, position: Int) {
        val intent = Intent(requireActivity(), AlbumDetailActivity::class.java)
        intent.putExtra("albumId", id)
        intent.putExtra(Constants.FROM_EDIT, true)
        intent.putExtra("isShowMenu", true)
        openActivity(intent)
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

}