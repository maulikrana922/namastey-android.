package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FilterCategoryAdapter
import com.namastey.adapter.InterestAdapter
import com.namastey.adapter.TrandingsAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.InterestBean
import com.namastey.model.TrandingBean
import com.namastey.uiView.FilterView
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import javax.inject.Inject

class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView, OnImageItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private lateinit var trandingsAdapter: TrandingsAdapter
    private var trandingList: ArrayList<TrandingBean> = ArrayList()
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
            var trandingBean = TrandingBean()
            trandingBean.name = "User"
            trandingList.add(trandingBean)
        }

        trandingsAdapter = TrandingsAdapter(trandingList, this@FilterActivity)
        rvFilterTranding.adapter = trandingsAdapter
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

        var categoryAdapter = FilterCategoryAdapter(categoryBeanList, this)
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
        interestAdapter = InterestAdapter(interestList, this@FilterActivity, this)
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

    }
}
