package com.namastey.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.BuildConfig
import com.namastey.R
import com.namastey.adapter.AlbumVideoAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.UpnextVideoAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumVideoBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.listeners.OnVideoClick
import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import com.namastey.model.VideoBean
import com.namastey.uiView.AlbumView
import com.namastey.utils.*
import com.namastey.viewModel.AlbumViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.android.synthetic.main.activity_album_video.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AlbumVideoActivity : BaseActivity<ActivityAlbumVideoBinding>(), AlbumView, OnVideoClick,
    OnItemClick, OnSelectUserItemClick {

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
    private lateinit var bottomSheetDialogShare: BottomSheetDialog
    private var fileUrl = ""
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

    override fun onSuccessSavePost(msg: String) {
        object : CustomAlertDialog(
            this@AlbumVideoActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessBlockUser(msg: String) {
        object : CustomAlertDialog(
            this@AlbumVideoActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                val intent = Intent(this@AlbumVideoActivity, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                openActivity(intent)
            }
        }.show()

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

        commentAdapter = CommentAdapter(data, this@AlbumVideoActivity, this)
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

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
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

    override fun onPostViewer(postId: Long) {
        albumViewModel.postView(postId)
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        val intent = Intent(this@AlbumVideoActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
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

    override fun onShareClick(videoBean: VideoBean) {
        if (sessionManager.isGuestUser()) {
            // Need to add data
        } else {
            openShareOptionDialog(videoBean)
        }
    }

    /**
     * Share option with video
     * Need to reduce this code
     */
    private fun openShareOptionDialog(videoBean: VideoBean) {
        bottomSheetDialogShare = BottomSheetDialog(this@AlbumVideoActivity, R.style.dialogStyle)
        bottomSheetDialogShare.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_feed,
                null
            )
        )
        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)

        bottomSheetDialogShare.tvShareCancel.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }

        // Share on Twitter app if install otherwise web link
        bottomSheetDialogShare.ivShareTwitter.setOnClickListener {
            val tweetUrl =
                StringBuilder("https://twitter.com/intent/tweet?text=")
            tweetUrl.append(videoBean.video_url)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(tweetUrl.toString())
            )
            val matches: List<ResolveInfo> =
                packageManager.queryIntentActivities(intent, 0)
            for (info in matches) {
                if (info.activityInfo.packageName.toLowerCase(Locale.ROOT)
                        .startsWith("com.twitter")
                ) {
                    intent.setPackage(info.activityInfo.packageName)
                }
            }
            startActivity(intent)
        }

        bottomSheetDialogShare.ivShareFacebook.setOnClickListener {
            var facebookAppFound = false
            var shareIntent =
                Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoBean.video_url)
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoBean.video_url))

            val pm: PackageManager = packageManager
            val activityList: List<ResolveInfo> = pm.queryIntentActivities(shareIntent, 0)
            for (app in activityList) {
                if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                    val activityInfo: ActivityInfo = app.activityInfo
                    val name =
                        ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.component = name
                    facebookAppFound = true
                    break
                }
            }
            if (!facebookAppFound) {
                val sharerUrl =
                    "https://www.facebook.com/sharer/sharer.php?u=${videoBean.video_url}"
                shareIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(sharerUrl)
                )
            }
            startActivity(shareIntent)
        }

        bottomSheetDialogShare.ivShareInstagram.setOnClickListener {
            var intent =
                packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (intent != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.setPackage("com.instagram.android")
                try {

                    val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val fileName = "share_video.mp4"
                    val file = File(folder, fileName)
                    val uri = let {
                        FileProvider.getUriForFile(
                            it,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            file
                        )
                    }
                    fileUrl = videoBean.video_url
                    downloadFile(this@AlbumVideoActivity, fileUrl, uri)

//                    val videoPath = File(applicationContext.filesDir, "")
//
//                    val newFile = File(videoPath, Uri.parse(videoBean.video_url).path)
//                    shareIntent.type = "video/*"
//                    val contentUri =
//                        FileProvider.getUriForFile(
//                            applicationContext,
//                            "com.namastey.provider",
//                            newFile
//                        );
//
//                    shareIntent.putExtra(
//                        Intent.EXTRA_STREAM,
//                        contentUri
//                    )
                } catch (e: Exception) {
                    Log.e("ERROR", e.printStackTrace().toString())
                }
//                startActivity(shareIntent)
            } else {
                intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=" + "com.instagram.android")
                startActivity(intent)
            }
        }
        bottomSheetDialogShare.ivShareWhatssapp.setOnClickListener {
            try {
                val pm: PackageManager = packageManager
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.type = "text/plain"
                sendIntent.setPackage("com.whatsapp")

                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    Uri.parse(videoBean.video_url)
                )
//                sendIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    Uri.parse(videoBean.video_url)
//                )
                startActivity(sendIntent)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    this@AlbumVideoActivity,
                    getString(R.string.whatsapp_not_install_message),
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }

        bottomSheetDialogShare.ivShareOther.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, videoBean.video_url
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        bottomSheetDialogShare.ivShareSave.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            albumViewModel.savePost(videoBean.id)
        }
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            displayReportUserDialog(videoBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            displayReportUserDialog(videoBean)
        }

        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(videoBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(videoBean)
        }

        bottomSheetDialogShare.show()
    }

    /**
     * Download video from url
     */
    private fun downloadFile(context: Context, url: String, file: Uri) {
        val ktor = HttpClient(Android)
        albumViewModel.setDownloading(true)
        bottomSheetDialogShare.progress_bar.visibility = View.VISIBLE
        bottomSheetDialogShare.ivShareInstagram.visibility = View.INVISIBLE
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                albumViewModel.setDownloading(false)
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                if (::bottomSheetDialogShare.isInitialized)
                                    bottomSheetDialogShare.dismiss()
                                bottomSheetDialogShare.progress_bar.progress = 0
                                openInstagram(file)
                            }
                            is DownloadResult.Error -> {
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                albumViewModel.setDownloading(false)
                                Toast.makeText(
                                    context,
                                    "Error while downloading file",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is DownloadResult.Progress -> {
                                bottomSheetDialogShare.progress_bar.progress = it.progress
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openInstagram(uri: Uri) {
        let { context ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.instagram.android")
            shareIntent.type = "video/*"

            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
            startActivity(shareIntent)
        }
    }

    private fun displayBlockUserDialog(videoBean: VideoBean) {
        object : CustomCommonAlertDialog(
            this@AlbumVideoActivity,
            videoBean.username,
            getString(R.string.msg_block_user),
            videoBean.profile_url,
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        albumViewModel.blockUser(videoBean.user_id, 1)
                    }
                }
            }
        }.show()

    }

    /**
     * Display dialog of report user
     */
    private fun displayReportUserDialog(videoBean: VideoBean) {
        object : CustomCommonAlertDialog(
            this@AlbumVideoActivity,
            videoBean.username,
            getString(R.string.msg_report_user),
            videoBean.profile_url,
            getString(R.string.report_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        showReportTypeDialog(videoBean.user_id)
                    }
                }
            }
        }.show()
    }

    /**
     * Display dialog with option (Reason)
     */
    private fun showReportTypeDialog(reportUserId: Long) {
        val bottomSheetReport: BottomSheetDialog =
            BottomSheetDialog(this@AlbumVideoActivity, R.style.dialogStyle)
        bottomSheetReport.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_pick,
                null
            )
        )
        bottomSheetReport.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetReport.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetReport.setCancelable(false)

        bottomSheetReport.tvPhotoTake.text = getString(R.string.its_spam)
        bottomSheetReport.tvPhotoTake.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoChoose.text = getString(R.string.its_inappropriate)
        bottomSheetReport.tvPhotoChoose.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoCancel.setTextColor(Color.BLUE)

        bottomSheetReport.tvPhotoTake.setOnClickListener {
            bottomSheetReport.dismiss()
            albumViewModel.reportUser(reportUserId, getString(R.string.its_spam))
        }
        bottomSheetReport.tvPhotoChoose.setOnClickListener {
            bottomSheetReport.dismiss()
            albumViewModel.reportUser(reportUserId, getString(R.string.its_inappropriate))
        }
        bottomSheetReport.tvPhotoCancel.setOnClickListener {
            bottomSheetReport.dismiss()
        }
        bottomSheetReport.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()
        albumViewModel.onDestroy()
    }
}