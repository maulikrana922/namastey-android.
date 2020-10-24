package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySettingsBinding
import com.namastey.uiView.SettingsView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_alert.*
import javax.inject.Inject

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(), SettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private var interestIn = 0
    private var isTouched = false
    private var minAge = ""
    private var maxAge = ""
    private var distance = ""

    override fun getViewModel() = settingsViewModel

    override fun getLayoutId() = R.layout.activity_settings

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        settingsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        activitySettingsBinding = bindViewData()
        activitySettingsBinding.viewModel = settingsViewModel

        initData()
    }

    private fun initData() {

        if (sessionManager.getUserGender() == Constants.Gender.male.name)
            llSettingBackground.background = getDrawable(R.drawable.blue_bar)
        else
            llSettingBackground.background = getDrawable(R.drawable.pink_bar)

        if (sessionManager.getInterestIn() != 0) {
            interestIn = sessionManager.getInterestIn()

            when (interestIn) {
                1 -> setSelectedTextColor(tvInterestMen, ivMenSelect)
                2 -> setSelectedTextColor(tvInterestWomen, ivWomenSelect)
                3 -> setSelectedTextColor(tvInterestEveryone, ivEveryoneSelect)
            }
        }

        switchHideProfile.isChecked = sessionManager.getIntegerValue(Constants.IS_HIDE) == 1
        switchPrivateAccount.isChecked = sessionManager.getIntegerValue(Constants.PROFILE_TYPE) == 1

        minAge = sessionManager.getStringValue(Constants.KEY_AGE_MIN)
        maxAge = sessionManager.getStringValue(Constants.KEY_AGE_MAX)
        distance = sessionManager.getStringValue(Constants.DISTANCE)

        rangeSettingAge.setMaxStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MAX).toFloat()
        )
        rangeSettingAge.setMinStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MIN).toFloat()
        )
        rangeSettingAge.apply()

        if (distance.isNotEmpty()) {
            seekbarSettingDistance.minStartValue = distance.toFloat()
            seekbarSettingDistance.apply()
        }

        seekbarSettingDistance.setOnSeekbarChangeListener { value ->
            tvSettingDistanceMessage.text = String.format(getString(R.string.distance_msg), value)
        }

        seekbarSettingDistance.setOnSeekbarFinalValueListener { value ->
            val jsonObject = JsonObject()
            distance = value.toString()
            jsonObject.addProperty(Constants.DISTANCE, distance)
            settingsViewModel.editProfile(jsonObject)
        }

        rangeSettingAge.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvSettingAgeMessage.text =
                String.format(getString(R.string.age_message_value), minValue, maxValue)
        }

        rangeSettingAge.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Log.d("min max:", "$minValue $maxValue")
            minAge = minValue.toString()
            maxAge = maxValue.toString()
            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.MIN_AGE, minAge)
            jsonObject.addProperty(Constants.MAX_AGE, maxAge)
            settingsViewModel.editProfile(jsonObject)
        }

        switchHideProfile.setOnTouchListener { view, motionEvent ->
            isTouched = true
            false
        }

        switchPrivateAccount.setOnTouchListener { view, motionEvent ->
            isTouched = true
            false
        }

        switchHideProfile.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isTouched) {
                isTouched = false
                when {
                    isChecked -> {
                        settingsViewModel.hideProfile(1)
                    }
                    else -> {
                        settingsViewModel.hideProfile(0)
                    }
                }

            }
        }

        switchPrivateAccount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isTouched) {
                isTouched = false
                when {
                    isChecked -> {
                        settingsViewModel.privateProfile(1)
                    }
                    else -> {
                        settingsViewModel.privateProfile(0)
                    }
                }

            }
        }

    }

    private fun setSelectedTextColor(view: TextView, imageView: ImageView) {
        tvInterestMen.setTextColor(Color.GRAY)
        tvInterestWomen.setTextColor(Color.GRAY)
        tvInterestEveryone.setTextColor(Color.GRAY)

        ivMenSelect.visibility = View.GONE
        ivWomenSelect.visibility = View.GONE
        ivEveryoneSelect.visibility = View.GONE

        imageView.visibility = View.VISIBLE

        view.setTextColor(Color.RED)

    }

    fun onClickSettingsBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickSettingInterestIn(view: View) {
        val jsonObject = JsonObject()
        when (view) {
            tvInterestMen -> {
                interestIn = 1
                setSelectedTextColor(tvInterestMen, ivMenSelect)
            }
            tvInterestWomen -> {
                interestIn = 2
                setSelectedTextColor(tvInterestWomen, ivWomenSelect)
            }
            tvInterestEveryone -> {
                interestIn = 3
                setSelectedTextColor(tvInterestEveryone, ivEveryoneSelect)
            }
//            1 = Men
//            2 = Women
//            3 = Everyone
        }
        jsonObject.addProperty(Constants.GENDER, interestIn)
        Log.d("CreateProfile Request:", jsonObject.toString())
        settingsViewModel.editProfile(jsonObject)

    }

    override fun onSuccess(msg: String) {
        sessionManager.setInterestIn(interestIn)
        sessionManager.setStringValue(maxAge, Constants.KEY_AGE_MAX)
        sessionManager.setStringValue(minAge, Constants.KEY_AGE_MIN)
        sessionManager.setStringValue(distance, Constants.DISTANCE)

    }

    override fun onSuccessHideProfile(message: String) {
        if (switchHideProfile.isChecked)
            sessionManager.setIntegerValue(1, Constants.IS_HIDE)
        else
            sessionManager.setIntegerValue(0, Constants.IS_HIDE)
    }

    override fun onSuccessProfileType(message: String) {
        if (switchPrivateAccount.isChecked)
            sessionManager.setIntegerValue(1, Constants.PROFILE_TYPE)
        else
            sessionManager.setIntegerValue(0, Constants.PROFILE_TYPE)
    }

    fun onClickLogout(view: View) {

        object : CustomAlertDialog(
            this@SettingsActivity,
            resources.getString(R.string.msg_logout),
            getString(R.string.logout),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        sessionManager.logout()
                        val intent = Intent(this@SettingsActivity, SignUpActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        openActivity(intent)
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }
}