package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
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
import com.namastey.model.DashboardBean
import com.namastey.uiView.FindFriendView
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import com.namastey.viewModel.FindFriendViewModel
import kotlinx.android.synthetic.main.activity_following_followers.*
import kotlinx.android.synthetic.main.fragment_add_friend.*
import kotlinx.android.synthetic.main.fragment_add_friend.rvSearchUser
import javax.inject.Inject

class AddFriendFragment : BaseFragment<FragmentAddFriendBinding>(), FindFriendView,
    View.OnClickListener, OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAddFriendBinding: FragmentAddFriendBinding
    private lateinit var findFriendViewModel: FindFriendViewModel
    private lateinit var layoutView: View
    private lateinit var suggestedAdapter: UserSearchAdapter
    private lateinit var userSearchAdapter: UserSearchAdapter
    private var userList = ArrayList<DashboardBean>()

    override fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>) {
        rvSuggestedUser.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        suggestedAdapter = UserSearchAdapter(suggestedList, requireActivity(),false,this)
        rvSuggestedUser.adapter = suggestedAdapter
    }

    override fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>) {
        userSearchAdapter =
            UserSearchAdapter(suggestedList, requireActivity(), false, this)
        rvSearchUser.adapter = userSearchAdapter
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

    // Temp for ui
    private fun setUserList(isDisplayCkb: Boolean) {

        userList.clear()
        for (number in 0..10) {
            val dashboardBean = DashboardBean()
            dashboardBean.username = "Ankit Dev"
            userList.add(dashboardBean)
        }
        rvSuggestedUser.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        suggestedAdapter = UserSearchAdapter(userList, requireActivity(),isDisplayCkb,this)
        rvSuggestedUser.adapter = suggestedAdapter

    }

    override fun onClick(v: View?) {
        when (v) {
            ivAddFriendClose -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            tvFindMultiple -> {
                if (tvFindMultiple.text.equals(resources.getString(R.string.multiple))) {
                    tvFindMultiple.text = resources.getString(R.string.done)
                    setUserList(true)   // For UI display entire list update
                } else {
                    Utils.hideKeyboard(requireActivity())
                    fragmentManager!!.popBackStack()
                }
            }
        }
    }

    override fun onItemClick(value: Long, position: Int) {
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, value)
        openActivity(intent)
    }

    override fun onDestroy() {
        findFriendViewModel.onDestroy()
        super.onDestroy()
    }
}