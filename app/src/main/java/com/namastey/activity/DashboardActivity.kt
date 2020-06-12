package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CategoryAdapter
import com.namastey.adapter.FeedAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import javax.inject.Inject


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter

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
        sessionManager.setLoginUser(true)
        dashboardViewModel.getCategoryList()
//        setCategoryList()
//        setDashboardList()

    }

    /**
     * Temp set feed list
     */
    private fun setDashboardList() {
        for (number in 0..10) {
            var dashboardBean = DashboardBean()
            dashboardBean.name = "NamasteyApp"
            feedList.add(dashboardBean)
        }
        feedAdapter = FeedAdapter(feedList, this)
        viewpagerFeed.adapter = feedAdapter

    }

    /**
     * Temp set categoryBean list
     */
//    private fun setCategoryList() {
//        var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
//
//        for (number in 0..10) {
//            var categoryBean = CategoryBean()
//            categoryBean.name = "Community"
//            categoryBeanList.add(categoryBean)
//
//            categoryBean = CategoryBean()
//            categoryBean.name = "Profession"
//            categoryBeanList.add(categoryBean)
//        }
//
//        var categoryAdapter = CategoryAdapter(categoryBeanList, this)
//        var horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
//            this@DashboardActivity,
//            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
//            false
//        )
//        rvCategory.layoutManager = horizontalLayout
//        rvCategory.adapter = categoryAdapter
//    }

    /**
     * Success of get category list
     */
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        var categoryAdapter = CategoryAdapter(categoryBeanList, this)
        var horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
            this@DashboardActivity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategory.layoutManager = horizontalLayout
        rvCategory.adapter = categoryAdapter

        setDashboardList()

    }

    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_dashboard

    override fun getBindingVariable() = BR.viewModel


    //    Temp open this activity
    fun onClickUser(view: View) {
        openActivity(this, ProfileActivity())
    }

    override fun onDestroy() {
        dashboardViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.FILTER_OK) {
            supportFragmentManager.popBackStack()
        }

    }
}
