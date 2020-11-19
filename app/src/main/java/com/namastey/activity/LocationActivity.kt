package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityLocationBinding
import com.namastey.fragment.CurrentLocationFragment
import com.namastey.uiView.LocationView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.activity_location.*
import javax.inject.Inject

class LocationActivity : BaseActivity<ActivityLocationBinding>(), LocationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileViewBinding: ActivityLocationBinding
    private lateinit var locationViewModel: LocationViewModel

    override fun getViewModel() = locationViewModel

    override fun getLayoutId() = R.layout.activity_location

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        locationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setViewInterface(this)
        activityProfileViewBinding = bindViewData()
        activityProfileViewBinding.viewModel = locationViewModel

        initData()
    }

    private fun initData() {
        if (sessionManager.getUserGender() == Constants.Gender.male.name)
            llLocationBackground.background = getDrawable(R.drawable.blue_bar)
        else
            llLocationBackground.background = getDrawable(R.drawable.pink_bar)

        addFragment(
            CurrentLocationFragment(),
            Constants.CURRENT_LOCATION_FRAGMENT
        )
    }

    fun onClickLocationBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onDestroy() {
        locationViewModel.onDestroy()
        super.onDestroy()
    }
}