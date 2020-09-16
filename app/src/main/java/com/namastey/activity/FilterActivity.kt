package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.*
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.listeners.*
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.model.InterestBean
import com.namastey.model.VideoBean
import com.namastey.roomDB.entity.User
import com.namastey.uiView.FilterView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import javax.inject.Inject


class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView, OnImageItemClick,
    OnUserItemClick, OnCategoryItemClick, OnItemClick, OnSelectUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private lateinit var trandingsUserAdapter: TrandingsUserAdapter
    private var trandingList: ArrayList<User> = ArrayList()
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

        // Temp for UI
        setTrandingList()
        setCategoryList()
        setInterestList()
        setUserList()

    }

    override fun getViewModel() = filterViewModel

    override fun getLayoutId() = R.layout.activity_filter

    override fun getBindingVariable() = BR.viewModel


    private fun setTrandingList() {
        for (number in 0..10) {
            val trandingBean = User()
            trandingBean.name = "User"
            trandingList.add(trandingBean)
        }

        trandingsUserAdapter = TrandingsUserAdapter(trandingList, this@FilterActivity, this)
        rvFilterTranding.adapter = trandingsUserAdapter
    }

    /**
     * Temp set categoryBean list
     */
    private fun setCategoryList() {
        val categoryBeanList: ArrayList<CategoryBean> = ArrayList()

        for (number in 0..10) {
            var categoryBean = CategoryBean()
            categoryBean.name = "Community"
            categoryBeanList.add(categoryBean)

            categoryBean = CategoryBean()
            categoryBean.name = "Profession"
            categoryBeanList.add(categoryBean)
        }

        val categoryAdapter = FilterCategoryAdapter(categoryBeanList, this@FilterActivity, this)
        val horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
            this@FilterActivity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvFilterCategory.layoutManager = horizontalLayout
        rvFilterCategory.adapter = categoryAdapter
    }

    private fun setInterestList() {
        val interestList: ArrayList<InterestBean> = ArrayList()
        for (number in 0..10) {
            val interestBean = InterestBean()
            interestBean.interest_name = "Dance"
            interestList.add(interestBean)
        }

        rvChooseInterestFilter.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        filterSubcategoryAdapter =
            FilterSubcategoryAdapter(interestList, this@FilterActivity, this, false)
        rvChooseInterestFilter.adapter = filterSubcategoryAdapter
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickFilterBack(view: View) {
        onBackPressed()
    }

    /**
     * Click on choose interest row
     */
    override fun onImageItemClick(interestBean: InterestBean) {
        setResult(Constants.FILTER_OK)
        finishActivity()
    }

    /**
     *  when user click on tranding user list
     */
    override fun onUserItemClick(user: User) {
        setResult(Constants.FILTER_OK)
        finishActivity()
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
                false, true
            )

        rvFilterTranding.adapter = albumDetailAdapter


        searchFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty())
                    rvFilterSearch.visibility = View.VISIBLE
                else
                    rvFilterSearch.visibility = View.GONE
                return true
            }
        })
    }

    override fun onItemClick(value: Long, position: Int) {
        TODO("Not yet implemented")
    }
}
