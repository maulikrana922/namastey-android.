package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationListener
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySettingsBinding
import com.namastey.location.AppLocationService
import com.namastey.roomDB.AppDB
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.RecentLocations
import com.namastey.uiView.SettingsView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_alert.*
import javax.inject.Inject

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(), SettingsView, LocationListener {


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
    lateinit var dbHelper: DBHelper
    private lateinit var appDb: AppDB
    private var currentLocationFromDB: RecentLocations? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var realLatitude: Double = 0.0
    private var realLongitude: Double = 0.0
    private lateinit var appLocationService: AppLocationService

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

        appDb = AppDB.getAppDataBase(this)!!
        dbHelper = DBHelper(appDb)

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

        getLocation()
        setCurrentLocation()

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

    private fun setCurrentLocation() {
        //Log.e("SettingsActivity", "currentLocationFromDB: ${currentLocationFromDB.city}")
        Log.e(
            "SettingsActivity",
            "getRecentLocationFromList() : ${sessionManager.getRecentLocationFromList()}"
        )
        if (sessionManager.getRecentLocationFromList() != null) {
            currentLocationFromDB = sessionManager.getRecentLocationFromList()
            Log.e("SettingsActivity", "currentLocationFromDB: ${currentLocationFromDB!!.id}")
            latitude = currentLocationFromDB!!.latitude
            longitude = currentLocationFromDB!!.longitude
        } else if (dbHelper.getLastRecentLocations() != null) {
            currentLocationFromDB = dbHelper.getLastRecentLocations()
            Log.e("SettingsActivity", "currentLocationFromDB: ${currentLocationFromDB!!.id}")
            latitude = currentLocationFromDB!!.latitude
            longitude = currentLocationFromDB!!.longitude
        }
        if (currentLocationFromDB != null) {
            if (currentLocationFromDB!!.city != "" && currentLocationFromDB!!.state != "") {

                Log.e("DashboardActivity", "latitude: $latitude")
                Log.e("DashboardActivity", "longitude: $longitude")
                if (realLatitude == latitude && realLongitude == longitude) {
                    tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
                } else {
                    tvMyCurrentLocation.text =
                        currentLocationFromDB!!.city.plus(", ").plus(currentLocationFromDB!!.state)
                }
            }
        } else {
            tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
        }


        //tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
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

    override fun onLogoutSuccess(msg: String) {
        sessionManager.logout()
        val intent = Intent(this@SettingsActivity, SignUpActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivity(intent)
    }

    override fun onLogoutFailed(msg: String, error: Int) {
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
                        settingsViewModel.logOut()
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    fun onClickAccountSettings(view: View) {
        openActivity(this@SettingsActivity, AccountSettingsActivity())

        //For Testing
        /* val intent = Intent(this@SettingsActivity, MatchesScreenActivity::class.java)
         intent.putExtra("username", "Demo");
         intent.putExtra("profile_url", "");
         openActivity(intent)*/
    }

    fun onClickMyCurrentLocation(view: View) {
        val intent = Intent(this@SettingsActivity, SearchLocationActivity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        intent.putExtra("isFromSearch", true)
        openActivity(intent)
        // openActivity(this, PassportContentActivity())
    }

    private fun getLocation() {
        appLocationService = AppLocationService(this)

        val gpsLocation: Location? = appLocationService.getLocation(LocationManager.GPS_PROVIDER)
        if (gpsLocation != null) {
            realLatitude = gpsLocation.latitude
            realLongitude = gpsLocation.longitude
            Log.e("DashboardActivity", "realLatitude: $realLatitude")
            Log.e("DashboardActivity", "realLongitude: $realLongitude")
        }
    }

    override fun onLocationChanged(location: Location) {
        realLatitude = location.latitude
        realLongitude = location.longitude
        Log.e("DashboardActivity", "realLatitude: $realLatitude")
        Log.e("DashboardActivity", "realLongitude: $realLongitude")
    }
}