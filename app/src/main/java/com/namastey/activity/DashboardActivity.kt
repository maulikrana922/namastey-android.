package com.namastey.activity

import android.Manifest
import android.content.ComponentName
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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CategoryAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.FeedAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView, OnFeedItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var commentAdapter: CommentAdapter
    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var bottomSheetDialogShare: BottomSheetDialog
    private lateinit var bottomSheetDialogComment: BottomSheetDialog
    private var colorDrawableBackground = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private var position = -1

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

        if (sessionManager.getUserGender() == Constants.Gender.female.name)
            ivUser.setImageResource(R.drawable.ic_female_user)
        else
            ivUser.setImageResource(R.drawable.ic_top_profile)

        dashboardViewModel.getCategoryList()
//        setDashboardList()

        setupPermissions()

        dashboardViewModel.getFeedList()
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
     * Temp set feed list
     */
//    private fun setDashboardList() {
//        for (number in 0..10) {
//            val dashboardBean = DashboardBean()
//            dashboardBean.username = "NamasteyApp"
//            feedList.add(dashboardBean)
//        }
//        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this)
//        viewpagerFeed.adapter = feedAdapter
//
//    }

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
                StringBuilder("https://ic_twitter_share.com/intent/tweet?text=")
            tweetUrl.append(dashboardBean.video_url)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(tweetUrl.toString())
            )
            val matches: List<ResolveInfo> =
                packageManager.queryIntentActivities(intent, 0)
            for (info in matches) {
                if (info.activityInfo.packageName.toLowerCase(Locale.ROOT)
                        .startsWith("com.ic_twitter_share")
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
//                    val resolver: ContentResolver = contentResolver
//                    val contentValues = ContentValues()
//                    contentValues.put(
//                        MediaStore.MediaColumns.RELATIVE_PATH,
//                        Environment.DIRECTORY_DOWNLOADS.toString()
//                    )
//                    val path: String = java.lang.String.valueOf(
//                        resolver.insert(
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                            contentValues
//                        )
//                    )


                    val videoPath = File(applicationContext.filesDir, "")

                    val newFile = File(videoPath, Uri.parse(dashboardBean.video_url).path)
                    shareIntent.type = "video/*"
                    val contentUri =
                        getUriForFile(applicationContext, "com.namastey.provider", newFile);

                    shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        contentUri
                    )
                } catch (e: Exception) {
                    Log.e("ERROR", e.printStackTrace().toString())
                }
                startActivity(shareIntent)
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
                pm.getPackageInfo("com.ic_whatsapp", PackageManager.GET_ACTIVITIES)
                val sendIntent = Intent(Intent.ACTION_VIEW)
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(
                        dashboardBean.video_url,
                        sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                    )
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
        val categoryAdapter = CategoryAdapter(categoryBeanList, this)
        val horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
            this@DashboardActivity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.FILTER_OK) {
            supportFragmentManager.popBackStack()
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
    override fun onCommentClick(postId: Long) {
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
        bottomSheetDialogComment.show()
    }

    override fun onUserProfileClick(userId: Long) {
        val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
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

        commentAdapter = CommentAdapter(data, this@DashboardActivity)
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

    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)

        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
    }

    override fun onSuccessProfileLike(data: Any) {
        val dashboardBean = feedList[position]
        if (dashboardBean.is_like == 1)
            dashboardBean.is_like = 0
        else
            dashboardBean.is_like = 1

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
                dashboardViewModel.getFeedList()
            }
        }.show()

    }
}
