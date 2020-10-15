package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumDetailAdapter
import com.namastey.adapter.FilterCategoryAdapter
import com.namastey.adapter.FilterSubcategoryAdapter
import com.namastey.adapter.UserSearchAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import com.namastey.uiView.FilterView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import javax.inject.Inject


class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView,
    OnCategoryItemClick, OnItemClick, OnSelectUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var filterSubcategoryAdapter: FilterSubcategoryAdapter
    private lateinit var userSearchAdapter: UserSearchAdapter
    private var userList = ArrayList<DashboardBean>()
    private lateinit var albumDetailAdapter: AlbumDetailAdapter
    private var postList = ArrayList<VideoBean>()

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

        searchFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty() && newText.trim().length >= 2) {
                    rvFilterSearch.visibility = View.VISIBLE
                    filterViewModel.getSearchUser(newText.trim())
                } else
                    rvFilterSearch.visibility = View.GONE
                return true
            }
        })

        filterViewModel.getTredingVideoList()
        setCategoryList()

    }

    override fun onSuccessSearchList(arrayList: java.util.ArrayList<DashboardBean>) {
        this.userList = arrayList
        userSearchAdapter =
            UserSearchAdapter(this.userList, this@FilterActivity, false, this, this)
        rvFilterSearch.adapter = userSearchAdapter
    }

    override fun onSuccessTreding(data: java.util.ArrayList<VideoBean>) {
        rvFilterTranding.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
        postList = data
        albumDetailAdapter =
            AlbumDetailAdapter(
                postList,
                this@FilterActivity,
                this,
                false, true, false
            )

        rvFilterTranding.adapter = albumDetailAdapter
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

        val categoryAdapter =
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

    private fun setSubcategoryList(subCategory: ArrayList<CategoryBean>) {
        filterSubcategoryAdapter =
            FilterSubcategoryAdapter(subCategory, this@FilterActivity, this, false)
        rvSubcategory.adapter = filterSubcategoryAdapter
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickFilterBack(view: View) {
        onBackPressed()
    }

    /**
     * when user click on filter category item
     */
    override fun onCategoryItemClick(categoryBean: CategoryBean) {

    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        TODO("Not yet implemented")
    }

    // Temp for ui
    private fun setUserList() {
        rvFilterTranding.addItemDecoration(GridSpacingItemDecoration(2, 20, false))

        postList.clear()
        for (number in 0..5) {
            val videoBean = VideoBean()
            videoBean.cover_image_url = ""
            postList.add(videoBean)
        }

        albumDetailAdapter =
            AlbumDetailAdapter(
                postList,
                this@FilterActivity,
                this,
                false, true, false
            )

        rvFilterTranding.adapter = albumDetailAdapter


    }

    override fun onSubCategoryItemClick(position: Int) {
        setSubcategoryList(categoryBeanList[position].sub_category)
    }

    override fun onItemClick(value: Long, position: Int) {
        val intent = Intent(this@FilterActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, value)
        openActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        filterViewModel.onDestroy()
    }
}
