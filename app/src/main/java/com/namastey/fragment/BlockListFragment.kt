package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.activity.ProfileViewActivity
import com.namastey.adapter.BlockUserAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentBlockListBinding
import com.namastey.listeners.OnBlockUserClick
import com.namastey.model.BlockUserListBean
import com.namastey.uiView.BlockListView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BlockListViewModel
import kotlinx.android.synthetic.main.fragment_block_list.*
import java.util.*
import javax.inject.Inject


class BlockListFragment : BaseFragment<FragmentBlockListBinding>(), BlockListView,
    OnBlockUserClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentBlockListBinding: FragmentBlockListBinding
    private lateinit var blockListViewModel: BlockListViewModel
    private lateinit var layoutView: View
    private lateinit var blockUserAdapter: BlockUserAdapter
    private var isMyProfile = false
    private var blockUserList: ArrayList<BlockUserListBean> = ArrayList()
    private var position = -1

    override fun getViewModel() = blockListViewModel

    override fun getLayoutId() = R.layout.fragment_block_list

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            BlockListFragment().apply {
            }
    }

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
        blockListViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BlockListViewModel::class.java)
        blockListViewModel.setViewInterface(this)
        fragmentBlockListBinding = getViewBinding()
        fragmentBlockListBinding.viewModel = blockListViewModel
    }


    private fun initId() {
        blockListViewModel.getBlockUserList()
    }

    override fun onSuccessBlockUserList(data: ArrayList<BlockUserListBean>) {

        blockUserList = data
        if (blockUserList.size == 0) {
            rvBlockList.visibility = View.GONE
            llNoUserInList.visibility = View.VISIBLE
        } else {
            rvBlockList.visibility = View.VISIBLE
            llNoUserInList.visibility = View.GONE

            blockUserAdapter = BlockUserAdapter(
                blockUserList,
                requireActivity(),
                this,
                sessionManager.getUserId()
            )
            rvBlockList.adapter = blockUserAdapter
        }
    }

    override fun onSuccessBlockUser(msg: String) {
        blockUserList.removeAt(position)
        blockUserAdapter.notifyItemRemoved(position)
        blockUserAdapter.notifyItemRangeChanged(position,blockUserAdapter.itemCount)

        if (blockUserList.size == 0){
            rvBlockList.visibility = View.GONE
            llNoUserInList.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.block_list))
    }

    override fun onDestroy() {
        blockListViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onUnblockUserClick(userId: Long, position: Int) {
        Log.e("BlockListFragment", "onBlockUserClick: \t userId: $userId")
        Log.e("BlockListFragment", "onBlockUserClick: \t position: $position")
        this.position = position
        blockListViewModel.blockUser(userId)
    }

    override fun onUserItemClick(userId: Long) {
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }
}