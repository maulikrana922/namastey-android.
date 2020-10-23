package com.namastey.activity

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
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(), SettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private var interestIn = 0

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
        if (sessionManager.getInterestIn() != 0) {
            interestIn = sessionManager.getInterestIn()

            when (interestIn) {
                1 -> setSelectedTextColor(tvInterestMen, ivMenSelect)
                2 -> setSelectedTextColor(tvInterestWomen, ivWomenSelect)
                3 -> setSelectedTextColor(tvInterestEveryone, ivEveryoneSelect)
            }
        }

        rangeSettingAge.setMaxStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MAX).toFloat()
        )
        rangeSettingAge.setMinStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MIN).toFloat()
        )
        rangeSettingAge.apply()

        seekbarSettingDistance.setOnSeekbarChangeListener { value ->
            tvSettingDistanceMessage.text = String.format(getString(R.string.distance_msg), value)
        }

        rangeSettingAge.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvSettingAgeMessage.text =
                String.format(getString(R.string.age_message_value), minValue, maxValue)
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
        when(view){
            tvInterestMen -> {
                interestIn = 1
                setSelectedTextColor(tvInterestMen,ivMenSelect)
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
    }
}