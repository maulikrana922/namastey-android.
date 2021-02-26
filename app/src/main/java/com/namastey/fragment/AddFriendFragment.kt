package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ProfileViewActivity
import com.namastey.adapter.UserSearchAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddFriendBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.DashboardBean
import com.namastey.uiView.FindFriendView
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import com.namastey.viewModel.FindFriendViewModel
import kotlinx.android.synthetic.main.fragment_add_friend.*
import javax.inject.Inject

class AddFriendFragment : BaseFragment<FragmentAddFriendBinding>(), FindFriendView,
    View.OnClickListener, OnItemClick, OnSelectUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAddFriendBinding: FragmentAddFriendBinding
    private lateinit var findFriendViewModel: FindFriendViewModel
    private lateinit var layoutView: View
    private lateinit var suggestedAdapter: UserSearchAdapter
    private lateinit var userSearchAdapter: UserSearchAdapter
    private var selectUserIdList: ArrayList<Long> = ArrayList()

    override fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>) {
        rvSuggestedUser.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        tvFindMultiple.visibility = View.VISIBLE
        suggestedAdapter = UserSearchAdapter(suggestedList, requireActivity(), false, this, this)
        rvSuggestedUser.adapter = suggestedAdapter
    }

    override fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>) {
        userSearchAdapter =
            UserSearchAdapter(suggestedList, requireActivity(), false, this, this)
        rvSearchUser.adapter = userSearchAdapter
    }

    override fun onSuccess(msg: String) {
        //Log.e("AddFriendFragment", "onSuccess: msg: \t $msg")
        fragmentManager!!.popBackStack()

        /* object : CustomAlertDialog(
             requireActivity(),
             msg, getString(R.string.ok), ""
         ) {
             override fun onBtnClick(id: Int) {
                 dismiss()
                 fragmentManager!!.popBackStack()
             }
         }.show()*/
    }

    override fun getViewModel() = findFriendViewModel

    override fun getLayoutId() = R.layout.fragment_add_friend

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            AddFriendFragment().apply {

            }
    }

    private fun setupViewModel() {
        findFriendViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FindFriendViewModel::class.java)
        findFriendViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = findFriendViewModel

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {

        ivAddFriendClose.setOnClickListener(this)
        tvFindMultiple.setOnClickListener(this)

        searchAddFriend.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty() && newText.trim().length >= 2) {
                    rvSearchUser.visibility = View.VISIBLE
                    findFriendViewModel.getSearchUser(newText.trim())
                } else
                    rvSearchUser.visibility = View.GONE
                return true
            }
        })

        findFriendViewModel.getSuggestedUser()
    }

    override fun onClick(v: View?) {
        when (v) {
            ivAddFriendClose -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            tvFindMultiple -> {
                Utils.hideKeyboard(requireActivity())
                if (tvFindMultiple.text == resources.getString(R.string.multiple)) {
                    tvFindMultiple.text = resources.getString(R.string.done)
                    if (::suggestedAdapter.isInitialized)
                        suggestedAdapter.displayRadioButton()
                } else {
                    if (selectUserIdList.size > 0)
                        findFriendViewModel.sendMultipleFollow(selectUserIdList.joinToString())
                    else
                        fragmentManager!!.popBackStack()
                }
            }
        }
    }

    override fun onItemClick(userId: Long, position: Int) {
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onDestroy() {
        findFriendViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        if (selectUserIdList.contains(userId))
            selectUserIdList.remove(userId)
        else
            selectUserIdList.add(userId)

        Log.d("User ID : ", selectUserIdList.joinToString())
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
    }

}