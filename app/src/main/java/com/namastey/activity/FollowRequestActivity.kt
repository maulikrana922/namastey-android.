package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FollowRequestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFollowRequestBinding
import com.namastey.listeners.OnFollowRequestClick
import com.namastey.model.FollowRequestBean
import com.namastey.uiView.FollowRequestView
import com.namastey.utils.Constants
import com.namastey.viewModel.FollowRequestViewModel
import kotlinx.android.synthetic.main.activity_follow_request.*
import java.util.ArrayList
import javax.inject.Inject

class FollowRequestActivity : BaseActivity<ActivityFollowRequestBinding>(), FollowRequestView,
    OnFollowRequestClick {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFollowRequestBinding: ActivityFollowRequestBinding
    private lateinit var followRequestViewModel: FollowRequestViewModel
    private lateinit var followRequestAdapter: FollowRequestAdapter
    private var position = -1
    private var followRequestList: ArrayList<FollowRequestBean> = ArrayList()


    override fun getViewModel() = followRequestViewModel

    override fun getLayoutId() = R.layout.activity_follow_request

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        followRequestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FollowRequestViewModel::class.java)
        activityFollowRequestBinding = bindViewData()
        activityFollowRequestBinding.viewModel = followRequestViewModel

        initId()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    private fun initId() {
        followRequestViewModel.getFollowRequestList()
    }

    override fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>) {
        Log.e("FollowRequestFragment", "onSuccessFollowRequest: \t $data")

        followRequestList = data

        if (followRequestList.size == 0) {
            tvNoFollowRequest.visibility = View.VISIBLE
            rvFollowRequest.visibility = View.GONE
            tvNoFollowRequest.text = resources.getString(R.string.no_follow_request)
        } else {
            tvNoFollowRequest.visibility = View.GONE
            rvFollowRequest.visibility = View.VISIBLE
            followRequestAdapter = FollowRequestAdapter(followRequestList, this, this)
            rvFollowRequest.adapter = followRequestAdapter
        }
    }

    override fun onSuccess(msg: String) {
        followRequestList.removeAt(position)
        followRequestAdapter.notifyItemRemoved(position)
        followRequestAdapter.notifyItemRangeChanged(position, followRequestAdapter.itemCount)

        if (followRequestList.size == 0) {
            tvNoFollowRequest.visibility = View.VISIBLE
            rvFollowRequest.visibility = View.GONE
            tvNoFollowRequest.text = resources.getString(R.string.no_follow_request)
        }
    }

    override fun onItemAllowDenyClick(userId: Long, isAllow: Int, position: Int) {
        this.position = position
        followRequestViewModel.followRequest(userId, isAllow)
    }

    override fun onFollowRequestItemClick(userId: Long, position: Int) {
        val intent = Intent(this, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onDestroy() {
        followRequestViewModel.onDestroy()
        super.onDestroy()
    }
}
