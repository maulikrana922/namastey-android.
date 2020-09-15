package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumVideoAdapter
import com.namastey.adapter.UpnextVideoAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumVideoBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnVideoClick
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.uiView.AlbumView
import com.namastey.utils.Constants
import com.namastey.viewModel.AlbumViewModel
import kotlinx.android.synthetic.main.activity_album_video.*
import javax.inject.Inject

class AlbumVideoActivity : BaseActivity<ActivityAlbumVideoBinding>(), AlbumView, OnVideoClick,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var activityAlbumVideoBinding: ActivityAlbumVideoBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumVideoAdapter: AlbumVideoAdapter
    private lateinit var upnextVideoAdapter: UpnextVideoAdapter
    private var videoList = ArrayList<VideoBean>()


    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        TODO("Not yet implemented")
    }

    override fun getViewModel() = albumViewModel

    override fun getLayoutId() = R.layout.activity_album_video

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        albumViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AlbumViewModel::class.java)
        activityAlbumVideoBinding = bindViewData()
        activityAlbumVideoBinding.viewModel = albumViewModel

        initData()

    }

    private fun initData() {

        videoList =
            intent.getParcelableArrayListExtra<VideoBean>(Constants.VIDEO_LIST) as ArrayList<VideoBean>

        val position = intent.getIntExtra("position", 0)
        albumVideoAdapter = AlbumVideoAdapter(videoList, this@AlbumVideoActivity, this)
        viewpagerAlbum.adapter = albumVideoAdapter

        viewpagerAlbum.currentItem = position
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    override fun onItemClick(value: Long, position: Int) {
        viewpagerAlbum.currentItem = position
    }

    override fun onVideoClick() {
        groupUpnext.visibility = View.GONE
    }

    override fun onUpnextClick(position: Int) {
        groupUpnext.visibility = View.VISIBLE
        upnextVideoAdapter =
            UpnextVideoAdapter(videoList, this@AlbumVideoActivity, this@AlbumVideoActivity)
        rvAlbumUpnext.adapter = upnextVideoAdapter

    }
}