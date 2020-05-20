package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBinding
import com.namastey.uiView.ProfileView
import com.namastey.utils.GlideLib
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : BaseActivity<ActivityProfileBinding>(),ProfileView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
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

        GlideApp.with(this).load(R.drawable.ic_male)
            .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male).fitCenter().into(ivProfileUser)
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
}
