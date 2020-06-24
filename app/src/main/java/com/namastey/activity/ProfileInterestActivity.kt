package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileInterestBinding
import com.namastey.uiView.ProfileInterestView
import com.namastey.viewModel.ProfileBasicViewModel
import com.namastey.viewModel.ProfileInterestViewModel
import javax.inject.Inject

class ProfileInterestActivity : BaseActivity<ActivityProfileInterestBinding>(), ProfileInterestView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var activityProfileInterestBinding: ActivityProfileInterestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        activityProfileInterestBinding = bindViewData()
        activityProfileInterestBinding.viewModel = profileInterestViewModel

        initData()
    }

    private fun initData() {

    }

    override fun getViewModel() = profileInterestViewModel

    override fun getLayoutId() = R.layout.activity_profile_interest

    override fun getBindingVariable() = BR.viewModel

    fun onClickInterestBack(view: View) {
        onBackPressed()
    }
    override fun onBackPressed() {
        finishActivity()
    }

}
