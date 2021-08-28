package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumDetailAdapter
import com.namastey.adapter.FilterCategoryAdapter
import com.namastey.adapter.FilterSubcategoryAdapter
import com.namastey.adapter.UserSearchAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import com.namastey.uiView.FilterView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.row_filter_category.view.*
import javax.inject.Inject


class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView,
    OnCategoryItemClick, OnItemClick, OnSelectUserItemClick, OnPostImageClick {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var categoryAdapter: FilterCategoryAdapter
    private lateinit var filterSubcategoryAdapter: FilterSubcategoryAdapter
    private lateinit var userSearchAdapter: UserSearchAdapter
    private var userList = ArrayList<DashboardBean>()
    private lateinit var albumDetailAdapter: AlbumDetailAdapter
    private var postList = ArrayList<VideoBean>()
    private var followingClick = false

    private var isFollowingSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        filterViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FilterViewModel::class.java)

        activityFilterBinding = bindViewData()
        activityFilterBinding.viewModel = filterViewModel

        initData()
    }

    private fun initData() {
        deselectFollowing()

        searchFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (sessionManager.isGuestUser()) {
                    Utils.hideKeyboard(this@FilterActivity)
                    addFragment(
                        SignUpFragment.getInstance(
                            true
                        ),
                        Constants.SIGNUP_FRAGMENT
                    )
                } else {
                    if (newText!!.isNotEmpty() && newText.trim().length >= 2) {
                        rvFilterSearch.visibility = View.VISIBLE
                        filterViewModel.getSearchUser(newText.trim())
                    } else
                        rvFilterSearch.visibility = View.GONE
                }
                return true
            }
        })
        rvFilterTranding.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
        followingClick = false
        val jsonObject = JsonObject()
        jsonObject.addProperty("sub_cat_id", "")
        filterViewModel.getTredingVideoList(jsonObject)
        setCategoryList()

    }

    override fun onSuccessSearchList(arrayList: java.util.ArrayList<DashboardBean>) {
        this.userList = arrayList
        userSearchAdapter =
            UserSearchAdapter(this.userList, this@FilterActivity, false, this, this)
        rvFilterSearch.adapter = userSearchAdapter
    }

    override fun onSuccessTreding(data: java.util.ArrayList<VideoBean>) {
        postList = data

        if (postList.size <= 0) {
            if (followingClick) {
                llEmpty.visibility = View.VISIBLE
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_following), getString(R.string.you)
                )
                tvEmptyFollow.text = String.format(
                    getString(R.string.msg_empty_following_title),
                    getString(R.string.you)
                )
            } else {
                tvNoFilterVideo.visibility = View.VISIBLE
            }

            rvFilterTranding.visibility = View.GONE
        } else {
            llEmpty.visibility = View.GONE
            tvNoFilterVideo.visibility = View.GONE
            rvFilterTranding.visibility = View.VISIBLE
        }
        albumDetailAdapter =
            AlbumDetailAdapter(
                postList,
                this@FilterActivity,
                this,
                this,
                this,
                false,
                true,
                false
            )
        rvFilterTranding.adapter = albumDetailAdapter
        Log.e("FilterActivity", "onSuccessTreding: ${data[0].is_like}")
    }

    override fun getViewModel() = filterViewModel

    override fun getLayoutId() = R.layout.activity_filter

    override fun getBindingVariable() = BR.viewModel


//    private fun setTrandingList() {
//        for (number in 0..10) {
//            val trandingBean = User()
//            trandingBean.name = "User"
//            trandingList.add(trandingBean)
//        }
//
//        trandingsUserAdapter = TrandingsUserAdapter(trandingList, this@FilterActivity, this)
//        rvFilterTranding.adapter = trandingsUserAdapter
//    }

    /**
     * set category list
     */
    private fun setCategoryList() {
        if (intent.hasExtra("categoryList")) {
            categoryBeanList =
                intent.getParcelableArrayListExtra<CategoryBean>("categoryList") as ArrayList<CategoryBean>
        }

        categoryAdapter =
            FilterCategoryAdapter(categoryBeanList, this@FilterActivity, this)
        val horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
            this@FilterActivity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvFilterCategory.layoutManager = horizontalLayout
        rvFilterCategory.adapter = categoryAdapter

        // Default display first category items
//        setSubcategoryList(categoryBeanList[0].sub_category)
    }

    private fun setSubcategoryList(
        subCategory: ArrayList<CategoryBean>,
        startColor: String,
        endColor: String
    ) {
        filterSubcategoryAdapter =
            FilterSubcategoryAdapter(
                subCategory,
                this@FilterActivity,
                this,
                false,
                startColor,
                endColor
            )
        rvSubcategory.adapter = filterSubcategoryAdapter
    }

    override fun onBackPressed() {
        val signUpFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_FRAGMENT)
        val signupWithPhoneFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_WITH_PHONE_FRAGMENT)
        val otpFragment =
            supportFragmentManager.findFragmentByTag(Constants.OTP_FRAGMENT)

        if (signupWithPhoneFragment != null) {
            val childFm = signupWithPhoneFragment.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack();
            } else {
                supportFragmentManager.popBackStack()
            }
        } else if (signUpFragment != null || otpFragment != null)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    fun onClickFilterBack(view: View) {
        onBackPressed()
    }

    /**
     * when user click on filter category item
     */
    override fun onCategoryItemClick(categoryBean: CategoryBean) {
        isFollowingSelected = false

        deselectFollowing()
        setSubcategoryList(
            categoryBean.sub_category,
            categoryBean.startColor,
            categoryBean.endColor
        )
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        val intent = Intent(this, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
        TODO("Not yet implemented")
    }

    override fun onItemPostImageClick(position: Int, videoList: ArrayList<VideoBean>) {
        val intent = Intent(this@FilterActivity, AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoList)
        intent.putExtra("position", position)
        openActivity(intent)
    }

    override fun onSubCategoryItemClick(subCategoryId: Int) {
        Log.d("Subcategory : ", subCategoryId.toString())
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
//            val intent = Intent()
//            intent.putExtra("fromSubCategory", true)
//            intent.putExtra("subCategoryId", subCategoryId)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
            followingClick = false
            postList.clear()
            postList = ArrayList()
            val jsonObject = JsonObject()
            jsonObject.addProperty("sub_cat_id", subCategoryId)
            filterViewModel.getTredingVideoList(jsonObject)
        }

    }

    override fun onItemClick(userId: Long, position: Int) {
        val intent = Intent(this@FilterActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onDestroy() {
        super.onDestroy()
        filterViewModel.onDestroy()
    }

    fun onClickInvite(view: View) {
        openActivity(this@FilterActivity, InviteActivity())
    }

    fun onFollowingClick(view: View) {
        if (::categoryAdapter.isInitialized) {
            categoryAdapter.lastSelectedPos = -1
            categoryAdapter.categoryList.forEach { category ->
                category.is_selected = 0
            }
            categoryAdapter.notifyDataSetChanged()
        }

//        if (isFollowingSelected) {
//            isFollowingSelected = false
//            deselectFollowing()
//        } else {
//            isFollowingSelected = true
//
        followingClick = true
        postList.clear()
        postList = ArrayList()
        val jsonObject = JsonObject()
        jsonObject.addProperty("sub_cat_id", "")
        jsonObject.addProperty("sub_cat_id", "")
        jsonObject.addProperty("following_id", sessionManager.getUserId())


        filterViewModel.getTredingVideoList(jsonObject)
        selectFollowing()
//        }
    }

    private fun selectFollowing() {
        Utils.filterrectangleCornerShapeGradient(
            tvFollowing, intArrayOf(
                ContextCompat.getColor(this@FilterActivity, R.color.color_text_red),
                ContextCompat.getColor(this@FilterActivity, R.color.color_text_red)
            )
        )
        tvFollowing.setTextColor(ContextCompat.getColor(this@FilterActivity, R.color.white))

    }

    private fun deselectFollowing() {
        Utils.filterrectangleShapeBorder(
            tvFollowing,
            ContextCompat.getColor(this@FilterActivity, R.color.color_text_red), true
        )

        tvFollowing.setTextColor(
            ContextCompat.getColor(
                this@FilterActivity,
                R.color.color_text_red
            )
        )

    }
}
