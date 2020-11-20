package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityPassportContentBinding
import com.namastey.uiView.LocationView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.activity_passport_content.*
import javax.inject.Inject

class PassportContentActivity : BaseActivity<ActivityPassportContentBinding>(), LocationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityPassportContentBinding: ActivityPassportContentBinding
    private lateinit var locationViewModel: LocationViewModel

    override fun getViewModel() = locationViewModel

    override fun getLayoutId() = R.layout.activity_passport_content

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
        locationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setViewInterface(this)
        activityPassportContentBinding = bindViewData()
        activityPassportContentBinding.viewModel = locationViewModel

        initData()
    }

    private fun initData() {
        if (sessionManager.getUserGender() == Constants.Gender.male.name) {
            llPassportContentBackground.background = getDrawable(R.drawable.male_bg)
        } else {
            llPassportContentBackground.background = getDrawable(R.drawable.female_bg)
        }
    }

    fun onClickSearchDestination(view: View) {
        openActivity(this, SearchLocationActivity())
    }

    fun onClickPassportContentBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onDestroy() {
        locationViewModel.onDestroy()
        super.onDestroy()
    }
}