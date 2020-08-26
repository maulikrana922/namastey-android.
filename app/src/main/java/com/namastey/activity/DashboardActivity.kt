package com.namastey.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CategoryAdapter
import com.namastey.adapter.FeedAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import javax.inject.Inject


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView, OnFeedItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter
    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var bottomSheetDialogShare: BottomSheetDialog

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

        setupPermissions()

    }

    private fun setupPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.location_permission_message))
                    .setTitle(getString(R.string.permission_required))

                builder.setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, id ->
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else
                makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
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
        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this)
        viewpagerFeed.adapter = feedAdapter

    }

    /**
     * Display share option if user login
     */
    private fun openShareOptionDialog() {
        bottomSheetDialogShare = BottomSheetDialog(this@DashboardActivity, R.style.choose_photo)
        bottomSheetDialogShare.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_feed,
                null
            )
        )
        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)



        bottomSheetDialogShare.tvShareCancel.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }
        bottomSheetDialogShare.show()
    }


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
        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.FILTER_OK) {
            supportFragmentManager.popBackStack()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.location_permission_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog, id ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_CODE
                            )
                        }

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.permission_denied_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.go_to_settings)
                        ) { dialog, id ->
                            var intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                        val dialog = builder.create()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                    }

                }
            }

        }

    }

    override fun onItemClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser()) {

        } else {
            openShareOptionDialog()
        }
    }

}
