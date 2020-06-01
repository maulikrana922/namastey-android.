package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBinding
import com.namastey.model.DashboardBean
import com.namastey.uiView.ProfileView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : BaseActivity<ActivityProfileBinding>(), ProfileView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        activityProfileBinding = bindViewData()
        activityProfileBinding.viewModel = profileViewModel

        initData()
    }

    private fun initData() {

        profileViewModel.getUserDetails(sessionManager.getAccessToken())
        GlideApp.with(this).load(R.drawable.ic_male)
            .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male).fitCenter()
            .into(ivProfileUser)
    }

    override fun onSuccessResponse(dashboardBean: DashboardBean) {
        if (sessionManager.getUserGender().equals(Constants.Gender.female)) {
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileUser)
        } else {
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileUser)
        }
        tvProfileUsername.text = "User" + sessionManager.getUserUniqueId()
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile

    override fun getBindingVariable() = BR.viewModel

    fun onClickProfileBack(view: View) {
        finishActivity()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onDestroy() {
        profileViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * click on followers and following
     */
    fun onClickFollow(view: View) {
        openActivity(this,FollowingFollowersActivity())
    }
}
