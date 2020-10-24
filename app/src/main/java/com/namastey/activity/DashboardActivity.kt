package com.namastey.activity

import android.Manifest
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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
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
import com.namastey.adapter.CategoryAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.FeedAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.listeners.OnFeedItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.uiView.DashboardView
import com.namastey.utils.*
import com.namastey.viewModel.DashboardViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.android.synthetic.main.activity_dashboard.*
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


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView, OnFeedItemClick,
    OnSelectUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var commentAdapter: CommentAdapter
    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var bottomSheetDialogShare: BottomSheetDialog
    private lateinit var bottomSheetDialogComment: BottomSheetDialog
    private var colorDrawableBackground = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private var position = -1
    private var commentCount = -1
    private var isUpdateComment = false
    private var fileUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        dashboardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        activityDashboardBinding = bindViewData()
        activityDashboardBinding.viewModel = dashboardViewModel

        initData()
    }

    private fun initData() {
        sessionManager.setLoginUser(true)

        Utils.rectangleShapeGradient(
            tvDiscover, intArrayOf(
                ContextCompat.getColor(this, R.color.color_spotify),
                ContextCompat.getColor(this, R.color.color_instagram)
            )
        )
        if (sessionManager.getUserGender() == Constants.Gender.female.name)
            ivUser.setImageResource(R.drawable.ic_female_user)
        else
            ivUser.setImageResource(R.drawable.ic_top_profile)

        dashboardViewModel.getCategoryList()
//        setDashboardList()

        setupPermissions()

        dashboardViewModel.getFeedList(0)
    }

    private fun setupPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.location_permission_message))
                    .setTitle(getString(R.string.permission_required))

                builder.setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, id ->
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else
                makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Display share option if user login
     */
    private fun openShareOptionDialog(dashboardBean: DashboardBean) {
        bottomSheetDialogShare = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
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
            tweetUrl.append(dashboardBean.video_url)
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, dashboardBean.video_url)
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(dashboardBean.video_url))

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
                    "https://www.facebook.com/sharer/sharer.php?u=${dashboardBean.video_url}"
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

                    fileUrl = dashboardBean.video_url

                    downloadFile(this@DashboardActivity, fileUrl, uri)

                } catch (e: Exception) {
                    Log.e("ERROR", e.printStackTrace().toString())
                }

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
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                sendIntent.type = "*/*"
                sendIntent.type = "text/plain"
                sendIntent.setPackage("com.whatsapp")

//                sendIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    Uri.parse(dashboardBean.video_url)

                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    Uri.parse(dashboardBean.video_url)
                )
                startActivity(sendIntent)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    this@DashboardActivity,
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
                Intent.EXTRA_TEXT, dashboardBean.video_url
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        bottomSheetDialogShare.ivShareSave.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            dashboardViewModel.savePost(dashboardBean.id)
        }
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }

        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }

        bottomSheetDialogShare.show()
    }
//    fun download(link: String, path: String) {
//        URL(link).openStream().use { input ->
//            FileOutputStream(File(path)).use { output ->
//                input.copyTo(output)
//            }
//        }
//    }
//
//    fun downloadFile(uRl: String) {
//        val direct = File(getExternalFilesDir(null), "/namastey")
//
//        if (!direct.exists()) {
//            direct.mkdirs()
//        }
//
//        val mgr = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//        val downloadUri = Uri.parse(uRl)
//        val request = DownloadManager.Request(
//            downloadUri
//        )
////    Environment.getExternalStorageDirectory()
////        .toString() + File.separator + "temp" + File.separator + "Videos" + File.separator
//        request.setAllowedNetworkTypes(
//            DownloadManager.Request.NETWORK_WIFI or
//                    DownloadManager.Request.NETWORK_MOBILE
//        )
//            .setAllowedOverRoaming(false).setTitle("namastey") //Download Manager Title
//            .setDescription("Downloading...") //Download Manager description
////            .setDestinationUri(Uri.parse("file://" + Environment.DIRECTORY_PICTURES + "/myfile.mp4"));
//
//            .setDestinationInExternalPublicDir(
//                Constants.FILE_PATH,
//                "temp5.mp4"
//            )
//
//        mgr.enqueue(request)
//
//    }


    /**
     * Display dialog of report user
     */
    private fun displayReportUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonAlertDialog(
            this@DashboardActivity,
            dashboardBean.username,
            getString(R.string.msg_report_user),
            dashboardBean.profile_url,
            getString(R.string.report_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        showReportTypeDialog(dashboardBean.user_id)
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
            BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
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
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_spam))
        }
        bottomSheetReport.tvPhotoChoose.setOnClickListener {
            bottomSheetReport.dismiss()
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_inappropriate))
        }
        bottomSheetReport.tvPhotoCancel.setOnClickListener {
            bottomSheetReport.dismiss()
        }
        bottomSheetReport.show()
    }

    private fun displayBlockUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonAlertDialog(
            this@DashboardActivity,
            dashboardBean.username,
            getString(R.string.msg_block_user),
            dashboardBean.profile_url,
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        dashboardViewModel.blockUser(dashboardBean.user_id)
                    }
                }
            }
        }.show()

    }

    /**
     * Success of get category list
     */
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        this.categoryBeanList = categoryBeanList
        tvDiscover.visibility = View.VISIBLE
        val categoryAdapter = CategoryAdapter(this.categoryBeanList, this)
        val horizontalLayout = LinearLayoutManager(
            this@DashboardActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategory.layoutManager = horizontalLayout
        rvCategory.adapter = categoryAdapter

//        setDashboardList()

    }

    override fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>) {
        feedList = dashboardList
        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this, sessionManager)
        viewpagerFeed.adapter = feedAdapter
    }

    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_dashboard

    override fun getBindingVariable() = BR.viewModel


    //    Temp open this activity
    fun onClickUser(view: View) {
        openActivity(this, ProfileActivity())
    }

    override fun onDestroy() {
        dashboardViewModel.onDestroy()
        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()

        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

        super.onDestroy()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Constants.REQUEST_CODE && data != null) {
            when {
                data.hasExtra("fromSubCategory") -> {
                    with(dashboardViewModel) { getFeedList(data.getIntExtra("subCategoryId", 0)) }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.FILTER_OK) {
            if (data != null && data.hasExtra("fromSubCategory")) {
                supportFragmentManager.popBackStack()
                with(dashboardViewModel) { getFeedList(data.getIntExtra("subCategoryId", 0)) }
            } else {
                supportFragmentManager.popBackStack()
            }
        }

    }

    private fun downloadFile(context: Context, url: String, file: Uri) {
        val ktor = HttpClient(Android)
        dashboardViewModel.setDownloading(true)
        bottomSheetDialogShare.progress_bar.visibility = View.VISIBLE
        bottomSheetDialogShare.ivShareInstagram.visibility = View.INVISIBLE
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                dashboardViewModel.setDownloading(false)
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
                                dashboardViewModel.setDownloading(false)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.location_permission_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog, id ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_CODE
                            )
                        }

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.permission_denied_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.go_to_settings)
                        ) { dialog, id ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                        val dialog = builder.create()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                    }

                }
            }

        }

    }

    override fun onClickFollow(position: Int, userId: Long, isFollow: Int) {
        this.position = position
        dashboardViewModel.followUser(userId, isFollow)
    }

    override fun onItemClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser()) {
            // Need to add data
        } else {
            openShareOptionDialog(dashboardBean)
        }
    }

    override fun onProfileLikeClick(position: Int, likedUserId: Long, isLike: Int) {
        this.position = position
        dashboardViewModel.likeUserProfile(likedUserId, isLike)
    }

    /**
     * Click on commnet count display list of comment and add comment dialog
     */
    override fun onCommentClick(position: Int, postId: Long) {
        this.position = position
        bottomSheetDialogComment = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
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

        dashboardViewModel.getCommentList(postId)

        bottomSheetDialogComment.ivCommentAdd.setOnClickListener {
            if (bottomSheetDialogComment.edtComment.text.toString().isNotBlank()) {
                dashboardViewModel.addComment(
                    postId,
                    bottomSheetDialogComment.edtComment.text.toString()
                )
            }
        }
        bottomSheetDialogComment.ivCloseComment.setOnClickListener {
            bottomSheetDialogComment.dismiss()
        }

        bottomSheetDialogComment.setOnDismissListener {
            if (isUpdateComment) {
                isUpdateComment = false
                val dashboardBean = feedList[position]
                dashboardBean.comments = commentCount
                feedAdapter.notifyItemChanged(position)
            }
        }
        bottomSheetDialogComment.show()
    }

    override fun onUserProfileClick(userId: Long) {
        val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onPostViewer(postId: Long) {
        dashboardViewModel.postView(postId)
    }

    override fun onSuccessGetComment(data: ArrayList<CommentBean>) {
        bottomSheetDialogComment.tvTotalComment.text =
            data.size.toString().plus(" ").plus(getString(R.string.comments))

        bottomSheetDialogComment.rvPostComment.addItemDecoration(
            DividerItemDecoration(
                this@DashboardActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        commentAdapter = CommentAdapter(data, this@DashboardActivity, this)
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
                        dashboardViewModel.deleteComment(data[viewHolder.adapterPosition].id)
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

    override fun onSuccess(msg: String) {
        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

        isUpdateComment = true
        commentCount = commentAdapter.itemCount
    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)

        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
        commentCount = commentAdapter.itemCount
        isUpdateComment = true
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
        if (dashboardBean.is_like == 1)
            dashboardBean.is_like = 0
        else
            dashboardBean.is_like = 1

        if (dashboardBean.is_match == 1) {
            Log.e("DashboardActivity", "userName: \t ${dashboardBean.username}")
            Log.e("DashboardActivity", "userName: \t ${dashboardBean.profile_url}")
            Log.e(
                "DashboardActivity",
                "userName: \t ${sessionManager.getStringValue(Constants.KEY_PROFILE_URL)}"
            )
            Log.e(
                "DashboardActivity",
                "userName: \t ${sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)}"
            )
            val intent = Intent(this@DashboardActivity, MatchesScreenActivity::class.java)
            intent.putExtra("username", dashboardBean.username);
            intent.putExtra("profile_url", dashboardBean.profile_url);
            openActivity(intent)
        }

        feedList[position] = dashboardBean
        feedAdapter.notifyItemChanged(position)
    }

    override fun onSuccessFollow(msg: String) {
        val dashboardBean = feedList[position]
        if (dashboardBean.is_follow == 1)
            dashboardBean.is_follow = 0
        else
            dashboardBean.is_follow = 1

        feedList[position] = dashboardBean
        feedAdapter.notifyItemChanged(position)
    }

    override fun onSuccessReport(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessSavePost(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessBlockUser(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                feedList.clear()
                dashboardViewModel.getFeedList(0)
            }
        }.show()

    }

    fun onClickDiscover(view: View) {
        dashboardViewModel.getFeedList(0)
        val intent = Intent(this@DashboardActivity, FilterActivity::class.java)
        intent.putExtra("categoryList", categoryBeanList)
        openActivityForResult(intent, Constants.FILTER_OK)
    }
}
