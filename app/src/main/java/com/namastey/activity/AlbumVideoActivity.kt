package com.namastey.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumVideoAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.UpnextVideoAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumVideoBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnVideoClick
import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import com.namastey.model.VideoBean
import com.namastey.uiView.AlbumView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.AlbumViewModel
import kotlinx.android.synthetic.main.activity_album_video.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import javax.inject.Inject

class AlbumVideoActivity : BaseActivity<ActivityAlbumVideoBinding>(), AlbumView, OnVideoClick,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityAlbumVideoBinding: ActivityAlbumVideoBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumVideoAdapter: AlbumVideoAdapter
    private lateinit var upnextVideoAdapter: UpnextVideoAdapter
    private var videoList = ArrayList<VideoBean>()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var bottomSheetDialogComment: BottomSheetDialog
    private lateinit var deleteIcon: Drawable
    private var colorDrawableBackground = ColorDrawable(Color.RED)

    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(msg: String) {
        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)

        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
    }

    override fun onSuccessGetComment(data: java.util.ArrayList<CommentBean>) {
        bottomSheetDialogComment.tvTotalComment.text =
            data.size.toString().plus(" ").plus(getString(R.string.comments))

        bottomSheetDialogComment.rvPostComment.addItemDecoration(
            DividerItemDecoration(
                this@AlbumVideoActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        commentAdapter = CommentAdapter(data, this@AlbumVideoActivity)
        bottomSheetDialogComment.rvPostComment.adapter = commentAdapter


        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    if (data[viewHolder.adapterPosition].user_id != sessionManager.getUserId()) return 0
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {

                    if (sessionManager.getUserId() == data[viewHolder.adapterPosition].user_id) {
                        albumViewModel.deleteComment(data[viewHolder.adapterPosition].id)
                        data.removeAt(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRangeChanged(
                            viewHolder.adapterPosition,
                            commentAdapter.itemCount
                        )
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(bottomSheetDialogComment.rvPostComment)
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
        albumVideoAdapter =
            AlbumVideoAdapter(videoList, this@AlbumVideoActivity, this, sessionManager)
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

    override fun onCommentClick(postId: Long) {
        bottomSheetDialogComment = BottomSheetDialog(this@AlbumVideoActivity, R.style.dialogStyle)
        bottomSheetDialogComment.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_post_comment,
                null
            )
        )
        bottomSheetDialogComment.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogComment.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogComment.setCancelable(true)
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white)!!

        albumViewModel.getCommentList(postId)

        bottomSheetDialogComment.ivCommentAdd.setOnClickListener {
            if (bottomSheetDialogComment.edtComment.text.toString().isNotBlank()) {
                albumViewModel.addComment(
                    postId,
                    bottomSheetDialogComment.edtComment.text.toString()
                )
            }
        }
        bottomSheetDialogComment.ivCloseComment.setOnClickListener {
            bottomSheetDialogComment.dismiss()
        }
        bottomSheetDialogComment.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

        albumViewModel.onDestroy()
    }
}