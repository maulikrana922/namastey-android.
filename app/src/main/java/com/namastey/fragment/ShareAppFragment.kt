package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.RecentUserAdapter
import com.namastey.adapter.UserSearchAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentShareAppBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnRecentUserItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.DashboardBean
import com.namastey.roomDB.AppDB
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.RecentUser
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ShareAppViewModel
import kotlinx.android.synthetic.main.dialog_following_user.view.*
import kotlinx.android.synthetic.main.fragment_share_app.*
import org.jetbrains.anko.doAsync
import javax.inject.Inject


class ShareAppFragment : BaseFragment<FragmentShareAppBinding>(),
    FollowingView,
    OnItemClick,
    OnSelectUserItemClick,
    OnRecentUserItemClick,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    //@Inject
    lateinit var dbHelper: DBHelper

    private lateinit var appDb: AppDB

    private lateinit var fragmentShareAppBinding: FragmentShareAppBinding
    private lateinit var shareAppViewModel: ShareAppViewModel
    private lateinit var layoutView: View
    private var followingList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: UserSearchAdapter
    private lateinit var recentUserAdapter: RecentUserAdapter
    private var userId: Long = -1
    private var coverImgUrl: String = ""
    private var recentList: ArrayList<RecentUser> = ArrayList()
    private var selectUserIdList: ArrayList<Long> = ArrayList()

    override fun getViewModel() = shareAppViewModel

    override fun getLayoutId() = R.layout.fragment_share_app

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(userId: Long, coverImage: String) =
            ShareAppFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.USER_ID, userId)
                    putString(Constants.COVER_IMAGE, coverImage)
                }
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
        initUI()
    }

    private fun setupViewModel() {
        // Inflate the layout for this fragment
        shareAppViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ShareAppViewModel::class.java)
        shareAppViewModel.setViewInterface(this)

        fragmentShareAppBinding = getViewBinding()
        fragmentShareAppBinding.viewModel = shareAppViewModel

        appDb = AppDB.getAppDataBase(requireContext())!!
        dbHelper = DBHelper(appDb)
    }

    private fun initUI() {
        ivAddFriendClose.setOnClickListener(this)
        tvFindMultiple.setOnClickListener(this)
        userId = arguments!!.getLong(Constants.USER_ID)
        coverImgUrl = arguments!!.getString(Constants.COVER_IMAGE)!!
        shareAppViewModel.getFollowingList(userId)

        doAsync {
            getRecentUserList()
        }

        searchFollowing()
    }

    private fun getRecentUserList() {
        recentList = dbHelper.getAllRecentUser() as ArrayList<RecentUser>

        if (recentList.size != 0) {

            tvRecent.visibility = View.VISIBLE
            rvRecentUser.visibility = View.VISIBLE

            recentUserAdapter =
                RecentUserAdapter(recentList, requireActivity(), false, this, this, this)
            rvRecentUser.adapter = recentUserAdapter
        } else {
            tvRecent.visibility = View.GONE
            rvRecentUser.visibility = View.GONE
        }
    }

    private fun searchFollowing() {
        searchFriend.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    rvSearchUser.visibility = View.VISIBLE
                    filter(newText.toString().trim())

                } else {
                    filter("")
                    shareAppViewModel.getFollowingList(userId)
                }

                return true
            }
        })

        searchFriend.setOnCloseListener {
            filter("")
            shareAppViewModel.getFollowingList(userId)

            false
        }
    }

    private fun filter(text: String) {
        Log.e("ShareAppFragment", "filter: text: $text")
        val filteredName: ArrayList<DashboardBean> = ArrayList()

        for (following in followingList) {
            if (following.username.toLowerCase().contains(text.toLowerCase())) {
                filteredName.add(following)
            }
        }

        followingAdapter.filterList(filteredName)
    }

    private fun showFollowingUserDialog(dashboardBean: DashboardBean) {
        Log.e("ShareFragment", "user_id: \t ${dashboardBean.username}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        val viewGroup: ViewGroup = activity!!.findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(activity).inflate(R.layout.dialog_following_user, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        GlideLib.loadImageUrlRoundCorner(
            activity!!,
            dialogView.ivFollwing,
            dashboardBean.profile_url
        )
        GlideLib.loadImageUrlRoundCorner(activity!!, dialogView.ivPostImage, coverImgUrl)
        dialogView.tvFollowing.text = dashboardBean.username
        dialogView.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.tvSave.setOnClickListener {
            Log.e("ShareFragment", "user_id: \t ${dashboardBean.id}")

            doAsync {
                if (dbHelper.getExistingRecentUser(dashboardBean.id.toInt())) {
                    dbHelper.deleteExistingUser(dashboardBean.id.toInt())
                    Log.e("ShareFragment", "Exist user_id: \t ${dashboardBean.id}")
                } else {
                    Log.e("ShareFragment", "Not Exist user_id: \t ${dashboardBean.id}")
                }
            }

            val currentTime = System.currentTimeMillis()

            val recentUser = RecentUser(
                dashboardBean.id,
                dashboardBean.user_id,
                dashboardBean.album_id,
                dashboardBean.viewers,
                dashboardBean.description,
                dashboardBean.username,
                dashboardBean.profile_url,
                dashboardBean.cover_image_url,
                dashboardBean.video_url,
                dashboardBean.job,
                dashboardBean.is_comment,
                dashboardBean.share_with,
                dashboardBean.is_download,
                dashboardBean.comments,
                dashboardBean.is_follow,
                dashboardBean.is_like,
                dashboardBean.isChecked,
                dashboardBean.is_match,
                dashboardBean.user_profile_type,
                dashboardBean.is_liked_you,
                currentTime
            )

            doAsync {
                dbHelper.addRecentUser(recentUser)
            }

            getRecentUserList()
            alertDialog.dismiss()
        }
    }

    private fun showRecentUserDialog(recentUser: RecentUser) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        val viewGroup: ViewGroup = activity!!.findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(activity).inflate(R.layout.dialog_following_user, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        GlideLib.loadImageUrlRoundCorner(
            activity!!,
            dialogView.ivFollwing,
            recentUser.profile_url
        )
        GlideLib.loadImageUrlRoundCorner(activity!!, dialogView.ivPostImage, coverImgUrl)
        dialogView.tvFollowing.text = recentUser.username
        dialogView.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.tvSave.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onItemRecentUserClick(recentUser: RecentUser) {
        showRecentUserDialog(recentUser)
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
        tvFindMultiple.visibility = View.VISIBLE

        followingList = list
        if (followingList.size != 0) {
            followingAdapter =
                UserSearchAdapter(followingList, requireActivity(), false, this, this)
            rvFollowingUser.adapter = followingAdapter
        }
    }

    override fun onItemClick(value: Long, position: Int) {
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {
        showFollowingUserDialog(dashboardBean)
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
                    if (::followingAdapter.isInitialized)
                        followingAdapter.displayRadioButton()
                } else if (tvFindMultiple.text == resources.getString(R.string.done)) {
                    if (selectUserIdList.size > 0)
                    else
                        fragmentManager!!.popBackStack()
                }
            }
        }
    }

    override fun onDestroy() {
        shareAppViewModel.onDestroy()
        super.onDestroy()
    }


}