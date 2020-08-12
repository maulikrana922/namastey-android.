package com.namastey.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumDetailAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumDetailBinding
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.CreateAlbumViewModel
import kotlinx.android.synthetic.main.activity_album_detail.*
import javax.inject.Inject

class AlbumDetailActivity : BaseActivity<ActivityAlbumDetailBinding>(), CreateAlbumView,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityAlbumDetailBinding: ActivityAlbumDetailBinding
    private lateinit var albumViewModel: CreateAlbumViewModel
    private lateinit var albumDetailAdapter: AlbumDetailAdapter
    private var isEditAlbum = false
    private var postList = ArrayList<VideoBean>()
    private var albumId = 0L
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        albumViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CreateAlbumViewModel::class.java)
        activityAlbumDetailBinding = bindViewData()
        activityAlbumDetailBinding.viewModel = albumViewModel

        initData()

    }

    private fun initData() {
        if (intent.hasExtra("albumId")) {
            albumId = intent.getLongExtra("albumId", 0)
            albumViewModel.getAlbumDetail(albumId)

            edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_edit_gray,
                0
            )
            edtAlbumName.compoundDrawablePadding = 25
            edtAlbumName.inputType = InputType.TYPE_NULL
            edtAlbumName.setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    val DRAWABLE_RIGHT = 2
                    if (event.action === MotionEvent.ACTION_UP) {
                        if (event.rawX >= edtAlbumName.right - (edtAlbumName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 40)
                        ) {
                            if (isEditAlbum) {
                                isEditAlbum = false
                                edtAlbumName.inputType = InputType.TYPE_NULL
                                edtAlbumName.clearFocus()
                                editAlbumApiCall()
                                edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_edit_gray,
                                    0
                                )
                            } else {
                                isEditAlbum = true
                                edtAlbumName.requestFocus()
                                edtAlbumName.inputType = InputType.TYPE_CLASS_TEXT
                                edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_done_red,
                                    0
                                )
                            }

                            return true
                        }
                    }
                    return false
                }
            })

        }
    }

    private fun editAlbumApiCall() {
        Utils.hideKeyboard(this@AlbumDetailActivity)
        var jsonObject = JsonObject()
        jsonObject.addProperty(Constants.NAME, edtAlbumName.text.toString().trim())
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
        jsonObject.addProperty(Constants.ALBUM_ID, albumId)

        albumViewModel.addEditAlbum(jsonObject)
    }


    override fun onSuccessResponse(albumBean: AlbumBean) {
        isEditAlbum = false
    }

    override fun onSuccessAlbumDetails(arrayList: ArrayList<AlbumBean>) {
        if (arrayList.size > 0) {
            edtAlbumName.setText(arrayList[0].name)
            postList = arrayList[0].post_video_list
            rvAlbumDetail.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
            albumDetailAdapter = AlbumDetailAdapter(postList, this@AlbumDetailActivity, this)
            rvAlbumDetail.adapter = albumDetailAdapter
        }
    }

    override fun onSuccessDeletePost() {
        postList.removeAt(position)
        albumDetailAdapter.notifyItemRemoved(position)
        albumDetailAdapter.notifyItemRangeChanged(position,albumDetailAdapter.itemCount)
    }

    override fun getViewModel() = albumViewModel

    override fun getLayoutId() = R.layout.activity_album_detail

    override fun getBindingVariable() = BR.viewModel

    fun onClickAlbumDetailBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onItemClick(postId: Long, position: Int) {
        this.position = position
        albumViewModel.removePostVideo(postId)
    }
}