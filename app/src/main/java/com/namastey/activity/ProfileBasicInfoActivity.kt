package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBasicInfoBinding
import com.namastey.fragment.EducationFragment
import com.namastey.fragment.InterestInFragment
import com.namastey.fragment.JobFragment
import com.namastey.fragment.SelectCategoryFragment
import com.namastey.model.*
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.view_profile_basic_info.*
import java.util.*
import javax.inject.Inject


class ProfileBasicInfoActivity : BaseActivity<ActivityProfileBasicInfoBinding>(), ProfileBasicView {

    private val TAG: String = ProfileBasicInfoActivity::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var activityProfileBasicInfoBinding: ActivityProfileBasicInfoBinding

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var profileBasicViewModel: ProfileBasicViewModel

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
    }

    override fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>) {
    }

    override fun onFailedUniqueName(error: ErrorBean?) {
        Log.e(TAG, "onFailedUniqueName: Error: \t ${error!!.user_name}")
        tvUniqueNameError.visibility = View.VISIBLE
        tvUniqueNameError.text = error.user_name
    }

    override fun onSuccessUniqueName(msg: String) {
        //super.onSuccess(msg)
        tvUniqueNameError.visibility = View.GONE
        Log.e(TAG, "onSuccess: Error: \t $msg")
        openActivity(this@ProfileBasicInfoActivity, ProfileInterestActivity())

    }

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
        rangeProfileAge.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvProfileAgeValue.text = "$minValue - $maxValue"
            sessionManager.setStringValue(minValue.toString(), Constants.KEY_AGE_MIN)
            sessionManager.setStringValue(maxValue.toString(), Constants.KEY_AGE_MAX)
        }

        initValue()

    }

    fun onClickProfileOneBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            initValue()
        } else {
            finishActivity()
        }
    }

    private fun initValue() {

        if (sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME).isNotEmpty())
            edtProfileUserName.setText(sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME))

        if (sessionManager.getStringValue(Constants.KEY_TAGLINE).isNotEmpty())
            edtProfileTagline.setText(sessionManager.getStringValue(Constants.KEY_TAGLINE))

        if (sessionManager.getInterestIn() != 0) {
            when (sessionManager.getInterestIn()) {
                1 -> tvProfileInterestIn.text = getString(R.string.men)
                2 -> tvProfileInterestIn.text = getString(R.string.women)
                3 -> tvProfileInterestIn.text = getString(R.string.everyone)
            }

            llInterestIn.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
        }

        if (sessionManager.getCategoryList().size >= 3) {
            tvProfileSelectCategory.text = sessionManager.getCategoryList().get(0).name
            llCategory.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
        }

        if (sessionManager.getEducationBean().course.isNotEmpty()) {
            tvProfileEducation.text = sessionManager.getEducationBean().course
            llEducation.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
        }
        if (sessionManager.getJobBean().title.isNotEmpty()) {
            tvProfileJobs.text = sessionManager.getJobBean().title
            llJob.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
        }
    }

    fun onClickProfileNext(view: View) {

//        if (edtProfileUserName.text!!.trim().isEmpty()){
//            edtProfileUserName.error = "casual name required"
//        }

        profileBasicViewModel.checkUniqueUsername(edtProfileUserName.text.toString().trim())

        if (validation()) {
            sessionManager.setStringValue(
                edtProfileUserName.text.toString().trim(),
                Constants.KEY_MAIN_USER_NAME
            )
            sessionManager.setStringValue(
                edtProfileTagline.text.toString().trim(),
                Constants.KEY_TAGLINE
            )

            /*if (tvUniqueNameError.visibility == View.GONE) {
                openActivity(this@ProfileBasicInfoActivity, ProfileInterestActivity())
            }*/
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (edtProfileUserName.text!!.trim().isEmpty()) {
            edtProfileUserName.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
            isValid = false
        } else
            edtProfileUserName.setBackgroundResource(R.drawable.rounded_white_solid_black_border)

        if (edtProfileCasualName.text!!.trim().isEmpty()) {
            edtProfileCasualName.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
            tvCasualNameError.visibility = View.VISIBLE
            tvCasualNameError.text = resources.getString(R.string.please_enter_casual_name)
            isValid = false
        } else {
            tvCasualNameError.visibility = View.GONE
            edtProfileCasualName.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
        }

        if (sessionManager.getCategoryList().size < 3) {
            llCategory.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
            isValid = false
        } else
            llCategory.setBackgroundResource(R.drawable.rounded_white_solid_black_border)

        if (sessionManager.getInterestIn() == 0) {
            llInterestIn.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
            isValid = false
        } else
            llInterestIn.setBackgroundResource(R.drawable.rounded_white_solid_black_border)

//        if (sessionManager.getEducationBean().college.isEmpty()) {
//            llEducation.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
//            isValid = false
//        } else
//            llEducation.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
//
//        if (sessionManager.getJobBean().title.isEmpty()) {
//            llJob.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
//            isValid = false
//        } else
//            llJob.setBackgroundResource(R.drawable.rounded_white_solid_black_border)
//
//        if (edtProfileTagline.text!!.trim().isEmpty()) {
//            edtProfileTagline.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
//            isValid = false
//        } else
//            edtProfileTagline.setBackgroundResource(R.drawable.rounded_white_solid_black_border)


        return isValid
    }

    fun onClickBasicInfo(view: View) {
        Utils.hideKeyboard(this@ProfileBasicInfoActivity)
        when (view) {
            llCategory -> {
                addFragment(
                    SelectCategoryFragment.getInstance(
                    ),
                    Constants.SELECT_CATEGORY_FRAGMENT
                )
            }
            llInterestIn -> {
                addFragment(
                    InterestInFragment.getInstance(
                    ),
                    Constants.INTEREST_IN_FRAGMENT
                )
            }
            llEducation -> {
                addFragment(
                    EducationFragment.getInstance(
                        false, EducationBean()
                    ),
                    Constants.EDUCATION_FRAGMENT
                )
            }
            llJob -> {
                addFragment(
                    JobFragment.getInstance(
                        false, JobBean()
                    ),
                    Constants.JOB_FRAGMENT
                )
            }
        }


    }
}
