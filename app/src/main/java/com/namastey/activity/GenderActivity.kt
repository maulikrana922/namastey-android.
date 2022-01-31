package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.databinding.ActivityGenderBinding
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_gender.*
import javax.inject.Inject

class GenderActivity : BaseActivity<ActivityGenderBinding>() {

    private var isGenderSelected = false
    private var isInterestInSelected = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        initData()
    }

    fun initData() {
        sessionManager = SessionManager(this)
    }

    override fun getViewModel(): BaseViewModel {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickGenderBack(view: View) {
        onBackPressed()
    }

    fun onClickSelectInterest(view: View) {
        when (view) {
            tvSelectInterest -> {
                if (groupSelectInterest.visibility == View.VISIBLE)
                    groupSelectInterest.visibility = View.GONE
                else
                    groupSelectInterest.visibility = View.VISIBLE
            }

            tvSelectMale -> {
                isInterestInSelected = true
                sessionManager.setInterestIn(1)
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.men)
                tvSelectInterest.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_male_sign,0,0,0)
            }
            tvSelectFemale -> {
                isInterestInSelected = true
                sessionManager.setInterestIn(2)
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.women)
                tvSelectInterest.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_female_sign,0,0,0)
            }
            tvSelectEveryone -> {
                isInterestInSelected = true
                sessionManager.setInterestIn(3)
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.everyone)
                tvSelectInterest.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_non_binary,0,0,0)
            }

            flMen -> {
                isGenderSelected = true
                sessionManager.setUserGender(Constants.Gender.male.name)
                Utils.roundedCornerShape(flMen, "#F8F8F8", "#30B7D6")
                flFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flNonBinary.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }
            flFemale -> {
                isGenderSelected = true
                sessionManager.setUserGender(Constants.Gender.female.name)
                Utils.roundedCornerShape(flFemale, "#F8F8F8", "#EB3EB0")
                flMen.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flNonBinary.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }
            flNonBinary -> {
                isGenderSelected = true
                sessionManager.setUserGender(Constants.Gender.nonbinary.name)
                Utils.roundedCornerShape(flNonBinary, "#F8F8F8", "#9748FC")
                flMen.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }

            btnContinue -> {
                if (isGenderSelected) {
                    if (isInterestInSelected) {
                        openActivity(this@GenderActivity, BirthdayActivity())
                    } else {
                        showMsg(getString(R.string.empty_interest_in))
                    }
                } else {
                    showMsg(getString(R.string.msg_select_gender))
                }
            }
        }
    }
}