package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FollowRequestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFollowRequestBinding
import com.namastey.listeners.OnFollowRequestClick
import com.namastey.model.DashboardBean
import com.namastey.model.FollowRequestBean
import com.namastey.uiView.FollowRequestView
import com.namastey.viewModel.FollowRequestViewModel
import kotlinx.android.synthetic.main.fragment_follow_request.*
import javax.inject.Inject


class FollowRequestFragment : BaseFragment<FragmentFollowRequestBinding>(), FollowRequestView,
OnFollowRequestClick{

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentFollowRequestBinding: FragmentFollowRequestBinding
    private lateinit var followRequestViewModel: FollowRequestViewModel
    private lateinit var layoutView: View
    private lateinit var followRequestAdapter: FollowRequestAdapter
    private var followRequestLength = 0
    private var position = -1
    private var followRequestList: ArrayList<FollowRequestBean> = ArrayList()

    companion object {
        fun getInstance() =
            FollowRequestFragment().apply {
            }
    }


    override fun getViewModel() = followRequestViewModel

    override fun getLayoutId() = R.layout.fragment_follow_request

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

        initId()
    }

    private fun setupViewModel() {
        followRequestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FollowRequestViewModel::class.java)
        followRequestViewModel.setViewInterface(this)

        fragmentFollowRequestBinding = getViewBinding()
        fragmentFollowRequestBinding.viewModel = followRequestViewModel
    }

    private fun initId() {
        followRequestViewModel.getFollowRequestList()

        ivFollowRequestClose.setOnClickListener {
            fragmentManager!!.popBackStack()
        }

        /*  followRequestAdapter = FollowRequestAdapter(requireActivity())
          rvFollowRequest.adapter = followRequestAdapter*/

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
            followRequestAdapter = FollowRequestAdapter(followRequestList, requireActivity(),this)
            rvFollowRequest.adapter = followRequestAdapter
        }
    }

    override fun onSuccess(msg: String) {
        followRequestList.removeAt(position)
        followRequestAdapter.notifyItemRemoved(position)
        followRequestAdapter.notifyItemRangeChanged(position,followRequestAdapter.itemCount)

        if (followRequestList.size == 0){
            tvNoFollowRequest.visibility = View.VISIBLE
            rvFollowRequest.visibility = View.GONE
            tvNoFollowRequest.text = resources.getString(R.string.no_follow_request)
        }

    }

    override fun onItemAllowDenyClick(userId: Long, isAllow: Int, position: Int) {
        this.position = position
        followRequestViewModel.followRequest(userId,isAllow)
    }

    override fun onDestroy() {
        followRequestViewModel.onDestroy()
        super.onDestroy()
    }
}