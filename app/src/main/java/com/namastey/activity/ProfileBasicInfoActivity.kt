package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBasicInfoBinding
import com.namastey.fragment.EducationFragment
import com.namastey.fragment.InterestInFragment
import com.namastey.fragment.JobFragment
import com.namastey.fragment.SelectCategoryFragment
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
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
            tvProfileAgeValue.text = "$minValue and $maxValue"
        })


    }

    fun onClickProfileOneBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        }else{
            finishActivity()
        }
    }

    fun onClickProfileNext(view: View) {
        openActivity(this@ProfileBasicInfoActivity, ProfileInterestActivity())
    }

    /**
     * Click on select category open new fragment
     */
    fun onClickSelectCategory(view: View) {
        Utils.hideKeyboard(this@ProfileBasicInfoActivity)
        addFragment(
            SelectCategoryFragment.getInstance(
            ),
            Constants.SELECT_CATEGORY_FRAGMENT
        )

    }

    /**
     * click on interest in
     */
    fun onClickInterestIn(view: View) {
        Utils.hideKeyboard(this@ProfileBasicInfoActivity)
        addFragment(
            InterestInFragment.getInstance(
            ),
            Constants.INTEREST_IN_FRAGMENT
        )
    }

    fun onClickEducation(view: View) {
        Utils.hideKeyboard(this@ProfileBasicInfoActivity)
        addFragment(
            EducationFragment.getInstance(
            ),
            Constants.EDUCATION_FRAGMENT
        )
    }

    fun onClickJob(view: View) {
        Utils.hideKeyboard(this@ProfileBasicInfoActivity)
        addFragment(
            JobFragment.getInstance(
            ),
            Constants.JOB_FRAGMENT
        )
    }
}
