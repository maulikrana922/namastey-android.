package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FilterCategoryAdapter
import com.namastey.adapter.InterestAdapter
import com.namastey.adapter.TrandingsUserAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.listeners.OnImageItemClick
import com.namastey.listeners.OnUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.InterestBean
import com.namastey.roomDB.entity.User
import com.namastey.uiView.FilterView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import javax.inject.Inject

class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView, OnImageItemClick,
    OnUserItemClick, OnCategoryItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private lateinit var trandingsUserAdapter: TrandingsUserAdapter
    private var trandingList: ArrayList<User> = ArrayList()
    private lateinit var interestAdapter: InterestAdapter

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
    }

    override fun getViewModel() = filterViewModel

    override fun getLayoutId() = R.layout.activity_filter

    override fun getBindingVariable() = BR.viewModel


    private fun setTrandingList() {
        for (number in 0..10) {
            var trandingBean = User()
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
        var categoryBeanList: ArrayList<CategoryBean> = ArrayList()

        for (number in 0..10) {
            var categoryBean = CategoryBean()
            categoryBean.name = "Community"
            categoryBeanList.add(categoryBean)

            categoryBean = CategoryBean()
            categoryBean.name = "Profession"
            categoryBeanList.add(categoryBean)
        }

        var categoryAdapter = FilterCategoryAdapter(categoryBeanList, this@FilterActivity, this)
        var horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
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
            var interestBean = InterestBean()
            interestBean.interest_name = "Dance"
            interestList.add(interestBean)
        }

        rvChooseInterestFilter.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, this@FilterActivity, this, false)
        rvChooseInterestFilter.adapter = interestAdapter
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
}
