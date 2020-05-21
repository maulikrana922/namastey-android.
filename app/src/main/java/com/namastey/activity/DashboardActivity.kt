package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CategoryAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.model.CategoryBean
import com.namastey.uiView.DashboardView
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import javax.inject.Inject


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private val categoryList: ArrayList<CategoryBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        dashboardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        activityDashboardBinding = bindViewData()
        activityDashboardBinding.viewModel = dashboardViewModel

        initData()
    }

    private fun initData() {
        setCategoryList()
    }

    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_dashboard

    override fun getBindingVariable() = BR.viewModel


    //    Temp open this activity
    fun onClickUser(view: View) {
        openActivity(this, ProfileActivity())
    }

    //    For ui purpose set temp data
    fun setCategoryList() {
        for (int in 1..5) {
            var categoryBean = CategoryBean()
            categoryBean.name = "Community"
            categoryList.add(categoryBean)

            categoryBean = CategoryBean()
            categoryBean.name = "Profession"
            categoryList.add(categoryBean)
        }

        var categoryAdapter = CategoryAdapter(categoryList, this)
        var horizontalLayout = LinearLayoutManager(
            this@DashboardActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategory.layoutManager = horizontalLayout
        rvCategory.adapter = categoryAdapter
    }
}
