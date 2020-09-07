package com.namastey.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.namastey.utils.SessionManager
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import javax.inject.Inject


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
    private fun openShareOptionDialog() {
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
        bottomSheetDialogShare.show()
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
        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this,sessionManager )
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
        dashboardViewModel.followUser(userId,isFollow)
    }

    override fun onItemClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser()) {
            // Need to add data
        } else {
            openShareOptionDialog()
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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {

                    if (sessionManager.getUserId() == data[viewHolder.adapterPosition].user_id){
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
}
