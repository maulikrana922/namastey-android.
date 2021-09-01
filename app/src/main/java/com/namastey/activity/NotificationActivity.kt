package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.NotificationAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityNotificationBinding
import com.namastey.listeners.FragmentRefreshListener
import com.namastey.listeners.OnNotificationClick
import com.namastey.model.*
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.NotificationViewModel
import kotlinx.android.synthetic.main.activity_notification.*
import java.util.*
import javax.inject.Inject

class NotificationActivity : BaseActivity<ActivityNotificationBinding>(), NotificationView,
    FragmentRefreshListener, PurchasesUpdatedListener, OnNotificationClick {

    private val TAG = "NotificationActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityNotificationBinding: ActivityNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private var activityList: ArrayList<ActivityListBean> = ArrayList()
    private var videoBeanList: ArrayList<VideoBean> = ArrayList()
    private var position = -1
    private var isActivityList = 0
    private lateinit var dialog: AlertDialog

    override fun getViewModel() = notificationViewModel

    override fun getLayoutId() = R.layout.activity_notification

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        notificationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
        activityNotificationBinding = bindViewData()
        activityNotificationBinding.viewModel = notificationViewModel

        initUI()

    }


    fun onFollowrequest(view: View) {
        intent = Intent(this@NotificationActivity, FollowRequestActivity::class.java)
        openActivity(intent)
//        finishActivity()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    fun onClickChat(view: View) {
        val intent = Intent()
        setResult(106, intent)
        finishActivity()
    }


    private fun initUI() {

        notificationViewModel.getFollowRequestList()
        val builder = AlertDialog.Builder(this)
        dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setSelectedApi()
    }

    private fun setSelectedApi() {
        Log.e(
            "NotificationFragment",
            "KEY_ALL_ACTIVITY: \t ${sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY)}"
        )
        if (sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) != 0) {
            val allActivity = sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY)
            notificationViewModel.getActivityList(allActivity)
            Log.e("NotificationFragment", "allActivity: \t $allActivity")

            val allActivityTitle = sessionManager.getStringValue(Constants.KEY_ALL_ACTIVITY_TITLE)
            //tvAllActivityTitle.text = allActivityTitle
            tvAllActivityMain.text = allActivityTitle
        } else {
            notificationViewModel.getActivityList(0)
        }
    }

    override fun onRefresh(activityListBean: ActivityListBean?) {
        if (activityListBean != null) {
            rvNotification.visibility = View.VISIBLE
            if (!this::notificationAdapter.isInitialized) {
                val notificationList = arrayListOf<ActivityListBean>()
                notificationList.add(activityListBean)
                notificationAdapter =
                    NotificationAdapter(this, notificationList, isActivityList, this)
                rvNotification.layoutManager = LinearLayoutManager(applicationContext!!)
                rvNotification.adapter = notificationAdapter
            } else {
                //  notificationAdapter.updateNotification(activityListBean)
            }
        }
    }

    override fun onNotificationClick(userId: Long, position: Int) {
        Log.e("NotificationFragment", "user_id: \t $userId")
        val intent = Intent(this, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onClickFollowRequest(
        position: Int,
        userId: Long,
        isFollow: Int
    ) {
        this.position = position
        Log.e("NotificationFragment", "isFollow: \t $isFollow")
        notificationViewModel.followUser(userId, 1)
    }

    override fun onPostVideoClick(position: Int, postId: Long) {
        this.position = position
        videoBeanList.clear()
        notificationViewModel.getPostVideoDetails(postId)
    }

    override fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>) {
        if (data.size >= 1) {
            if (data[0].profile_url != null && data[0].profile_url.isNotEmpty()) {
                // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data.get(0).profile_url)
                Log.e("NotificationFragment", "profile: \t data: ${data[0].profile_url}")
                GlideLib.loadImage(
                    applicationContext,
                    ivFollowRequestFirst,
                    data[0].profile_url
                )
            }
            txtNotification.setText("You have " + data.size + " follow request.")
        }else{
            txtNotification.setText("You have 0 follow request.")
        }
    }

    override fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>) {
        this.activityList = activityList
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        if (activityList.size != 0) {
            rvNotification.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            notificationAdapter =
                NotificationAdapter(this, activityList, isActivityList, this)
            rvNotification.adapter = notificationAdapter
            Log.e(
                "NotificationFragment",
                "onSuccessActivityList: \t data: ${activityList.size}"
            )
        } else {
            rvNotification.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        }
    }

    override fun onSuccessFollow(profileBean: ProfileBean) {
        Log.e("NotificationFragment", "profileBean: \t ${profileBean.is_follow}")
        val activityListBean = activityList[position]
        activityListBean.is_follow = profileBean.is_follow

        Log.e("NotificationFragment", "videoListDetail: ${profileBean.is_follow}")
        activityList[position] = activityListBean
        notificationAdapter.notifyItemChanged(position)

        //notificationAdapter.notifyDataSetChanged()
    }

    override fun onSuccessPostVideoDetailResponse(videoBean: VideoBean) {
        Log.e("NotificationAdapter", "videoBean: ${videoBean.id}")
        videoBeanList.add(videoBean)
        val intent = Intent(this, AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoBeanList)
        intent.putExtra("position", position)
        openActivity(intent)

    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
    }

    override fun onSuccess(msg: String) {

        //showMsg(msg)
        rvNotification.visibility = View.GONE
        tvNoData.visibility = View.VISIBLE
        tvNoData.text = msg

        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        notificationViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
    }

}

