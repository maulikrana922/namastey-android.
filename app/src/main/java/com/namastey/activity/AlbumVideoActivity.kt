package com.namastey.activity

import android.app.Activity
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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.BuildConfig
import com.namastey.R
import com.namastey.adapter.AlbumVideoAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.UpnextVideoAdapter
import com.namastey.customViews.ExoPlayerRecyclerView
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumVideoBinding
import com.namastey.fragment.ShareAppFragment
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.listeners.OnSocialTextViewClick
import com.namastey.listeners.OnVideoClick
import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import com.namastey.uiView.AlbumView
import com.namastey.utils.*
import com.namastey.viewModel.AlbumViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
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
    OnItemClick, OnSelectUserItemClick, OnSocialTextViewClick {

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
    private var position = -1
    private var editPost = false

    var mRecyclerView: ExoPlayerRecyclerView? = null
    var mLayoutManager: LinearLayoutManager? = null
    private var firstTime = true

    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
    }

    override fun onSuccess(msg: String) {
        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)
        bottomSheetDialogComment.rvPostComment.scrollToPosition(commentAdapter.itemCount - 1);

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

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {

        val videoBean = videoList[position]
        videoBean.is_like = dashboardBean.is_like

        Log.e("AlbumVideoActivity", "videoListDetail: ${dashboardBean.is_like}")
        videoList[position] = videoBean
        albumVideoAdapter.notifyItemChanged(position)
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

        commentAdapter = CommentAdapter(data, this@AlbumVideoActivity, this, this)
        bottomSheetDialogComment.rvPostComment.adapter = commentAdapter

        val params: ViewGroup.LayoutParams =
            bottomSheetDialogComment.rvPostComment.layoutParams

        if (data.size > 6) {
            params.height = 1000
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        }

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
        mRecyclerView = findViewById(R.id.viewpagerAlbum)

        Log.e("AlbumVideoActivity", "videoList: \t ${videoList.size}")


        val position = intent.getIntExtra("position", 0)
        /* albumVideoAdapter =
             AlbumVideoAdapter(videoList, this@AlbumVideoActivity, this, sessionManager)
         viewpagerAlbum.adapter = albumVideoAdapter*/

        getVideoUrl()
        mLayoutManager!!.scrollToPositionWithOffset(position, 0);

        //viewpagerAlbum.currentItem = position

    }

    private fun getVideoUrl() {
        /* mRecyclerView!!.layoutManager =
             LinearLayoutManager(this, LinearLayout.VERTICAL, false)*/
        mLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager

        /* val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
         linearSnapHelper.attachToRecyclerView(mRecyclerView)*/

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)

        //set data object
        mRecyclerView!!.setVideoBeans(videoList)
        albumVideoAdapter =
            AlbumVideoAdapter(videoList, this@AlbumVideoActivity, this, sessionManager)
        mRecyclerView!!.adapter = albumVideoAdapter

        if (firstTime) {
            Handler(Looper.getMainLooper()).post { mRecyclerView!!.playVideo(false) }
            firstTime = false
        }
    }

    override fun onBackPressed() {
        //finishActivity()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    override fun onPostEdit(position: Int, videoBean: VideoBean) {

        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                completeSignUpDialog()
            } else {
                this.position = position
                val intent = Intent(this@AlbumVideoActivity, PostVideoActivity::class.java)
//            intent.putExtra("albumId", videoBean.album_id)
                intent.putExtra("editPost", true)
//            intent.putExtra("albumName", videoBean.album_name)
                intent.putExtra("videoBean", videoBean)
                openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
            }
        }
    }

    override fun onClickLike(position: Int, videoBean: VideoBean, isLike: Int) {
        this.position = position
        Log.e("AlbumVideoActivity", "UserType: \t ${sessionManager.isGuestUser()}")
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(true), Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                completeSignUpDialog()
            } else {
                albumViewModel.likeUserProfile(videoBean.user_id, isLike)
                Log.e("AlbumVideoActivity", "UserType: In")
            }
        }
    }

    private fun getCurrentItem(): Int {
        return mLayoutManager!!.findFirstVisibleItemPosition()
    }

    override fun onItemClick(id: Long, position: Int) {
        //viewpagerAlbum.currentItem = position
        mLayoutManager!!.scrollToPositionWithOffset(position, 0);

        //  tvUpnext.visibility = View.GONE
        //  rvAlbumUpnext.visibility = View.GONE
        groupUpnext.visibility = View.GONE
        albumVideoAdapter.isDisplayDetails = true
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onVideoClick() {
        groupUpnext.visibility = View.GONE
    }

    override fun onPostViewer(postId: Long) {
        albumViewModel.postView(postId)
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
        Log.e("AlbumVideoActivity", "onSelectItemClick: \t userProfileType: $userProfileType")
        if (userProfileType == "1" && sessionManager.isGuestUser()) {
            bottomSheetDialogComment.dismiss()
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@AlbumVideoActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, userId)
            openActivity(intent)
        }
    }

    override fun onUpnextClick(position: Int) {
        groupUpnext.visibility = View.VISIBLE
        upnextVideoAdapter =
            UpnextVideoAdapter(videoList, this@AlbumVideoActivity, this@AlbumVideoActivity)
        rvAlbumUpnext.adapter = upnextVideoAdapter
    }

    override fun onClickSocialText(userName: String) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@AlbumVideoActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USERNAME, userName)
            openActivity(intent)

        }
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

        bottomSheetDialogComment.edtComment.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                bottomSheetDialogComment.dismiss()
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            }
        }

        bottomSheetDialogComment.ivCommentAdd.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                bottomSheetDialogComment.dismiss()
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            } else {
                if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    completeSignUpDialog()
                } else {
                    if (bottomSheetDialogComment.edtComment.text.toString().isNotBlank()) {
                        albumViewModel.addComment(
                            postId,
                            bottomSheetDialogComment.edtComment.text.toString()
                        )
                    }
                }
            }
        }
        bottomSheetDialogComment.ivCloseComment.setOnClickListener {
            bottomSheetDialogComment.dismiss()
        }
        bottomSheetDialogComment.show()
    }

    override fun onShareClick(position: Int, videoBean: VideoBean) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        } else {
            openShareOptionDialog(videoBean)
        }
    }

    private fun postShare(postId: Int) {
        albumViewModel.postShare(postId, 1)
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
        bottomSheetDialogShare.ivShareWhatssapp.setOnClickListener {
            postShare(videoBean.id.toInt())
            shareWhatsApp(videoBean)
        }
        bottomSheetDialogShare.ivShareApp.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            postShare(videoBean.id.toInt())
            shareWithInApp(videoBean)
        }
        bottomSheetDialogShare.ivShareTwitter.setOnClickListener {
            postShare(videoBean.id.toInt())
            shareTwitter(videoBean)
        }
        bottomSheetDialogShare.ivShareFacebook.setOnClickListener {
            postShare(videoBean.id.toInt())
            shareFaceBook(videoBean)
        }
        bottomSheetDialogShare.ivShareInstagram.setOnClickListener {
            postShare(videoBean.id.toInt())
            shareInstagram(videoBean)
        }
        bottomSheetDialogShare.ivShareOther.setOnClickListener {
            postShare(videoBean.id.toInt())
            shareOther(videoBean)
        }

        //Bottom Icon
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

    private fun shareWhatsApp(videoBean: VideoBean) {
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

    private fun shareWithInApp(videoBean: VideoBean) {
        addFragment(
            ShareAppFragment.getInstance(sessionManager.getUserId(), videoBean.cover_image_url, videoBean.video_url),
            Constants.SHARE_APP_FRAGMENT
        )
    }

    private fun shareTwitter(videoBean: VideoBean) {
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

    private fun shareFaceBook(videoBean: VideoBean) {
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

    private fun shareInstagram(videoBean: VideoBean) {
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

    private fun shareOther(videoBean: VideoBean) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sendIntent.putExtra(
            Intent.EXTRA_TEXT, videoBean.video_url
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_POST_VIDEO) {
            if (data != null) {      // Temp need to change
                editPost = true
                val videoBean = data.getParcelableExtra<VideoBean>("videoBean") as VideoBean
                videoList[position] = videoBean
                albumVideoAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun onSuccessPostShare(msg: String) {
        Log.e("DashboardActivity", "onSuccessPostShare: msg:\t  $msg")
        videoList[position].share = videoList[position].share + 1
        albumVideoAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView!!.onRestartPlayer()
    }

    override fun onPause() {
        super.onPause()
        mRecyclerView!!.onPausePlayer()
    }

    override fun onStop() {
        super.onStop()
        mRecyclerView!!.onPausePlayer()
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("DashboardActivity", "onRestart")
        mRecyclerView!!.onRestartPlayer()
    }

    override fun onDestroy() {
        albumViewModel.onDestroy()

        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()

        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }

        super.onDestroy()
    }
}