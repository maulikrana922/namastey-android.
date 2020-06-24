package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBasicInfoBinding
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.activity_profile_basic_info.*
import javax.inject.Inject


class ProfileBasicInfoActivity : BaseActivity<ActivityProfileBasicInfoBinding>(), ProfileBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var activityProfileBasicInfoBinding: ActivityProfileBasicInfoBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileBasicViewModel: ProfileBasicViewModel
    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.activity_profile_basic_info

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        activityProfileBasicInfoBinding = bindViewData()
        activityProfileBasicInfoBinding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {

        rangeProfileAge.setMaxStartValue(45f)
        rangeProfileAge.apply()
        rangeProfileAge.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            tvProfileAgeValue.text = minValue.toString() + " and " + maxValue.toString()
        })


    }

    fun onClickProfileOneBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickProfileNext(view: View) {
        openActivity(this@ProfileBasicInfoActivity,ProfileInterestActivity())
    }
}
