package com.namastey.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.application.NamasteyApplication
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
import kotlinx.android.synthetic.main.activity_gender.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.groupSelectInterest
import kotlinx.android.synthetic.main.activity_settings.tvSelectInterest
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.io.errors.IOException
import java.util.*
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
    private lateinit var locationCallback: LocationCallback
    private val MIN_DISTANCE_FOR_UPDATE: Float = 10 * 1.toFloat()
    private val MIN_TIME_FOR_UPDATE = 1000 * 60 * 2.toLong()
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

/*
        if (sessionManager.getUserGender() == Constants.Gender.male.name)
            llSettingBackground.background = getDrawable(R.drawable.blue_bar)
        else
            llSettingBackground.background = getDrawable(R.drawable.pink_bar)
*/

        if (sessionManager.getInterestIn() != 0) {
            interestIn = sessionManager.getInterestIn()

            when (interestIn) {

                1 -> {
                    tvSelectInterest.text = getString(R.string.male)
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_male_sign,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )
//                    setSelectedTextColor(tvInterestMen, ivMenSelect)
                }

                2 -> {
                    tvSelectInterest.text = getString(R.string.women)
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_female_sign,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )

//                    setSelectedTextColor(tvInterestWomen, ivWomenSelect)
                }

                3 -> {
                    tvSelectInterest.text = getString(R.string.everyone)
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_female_sign,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )

//                    setSelectedTextColor(tvInterestEveryone, ivEveryoneSelect)
                }
            }
        }

        getLocation()
        setCurrentLocation()

        switchHideProfile.isChecked = sessionManager.getIntegerValue(Constants.IS_HIDE) == 1
        switchPrivateAccount.isChecked = sessionManager.getIntegerValue(Constants.PROFILE_TYPE) == 1
        switchGlobal.isChecked = sessionManager.getIntegerValue(Constants.KEY_GLOBAL) == 1

        minAge = sessionManager.getStringValue(Constants.KEY_AGE_MIN)
        maxAge = sessionManager.getStringValue(Constants.KEY_AGE_MAX)
        distance = sessionManager.getStringValue(Constants.DISTANCE)

        rangeSettingAge.setMaxStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MAX).toFloat()
        ).apply()

        rangeSettingAge.setMinStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MIN).toFloat()
        ).apply()
//        rangeSettingAge.apply()

        if (distance.isNotEmpty()) {
            seekbarSettingDistance.minStartValue = distance.toFloat()
            seekbarSettingDistance.apply()
        }

        seekbarSettingDistance.setOnSeekbarChangeListener { value ->
            tvSettingDistanceMessage.text = String.format(getString(R.string.distance_msg_1), value)
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
        switchGlobal.setOnTouchListener { view, motionEvent ->
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

        switchGlobal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isTouched) {
                isTouched = false
                val jsonObject = JsonObject()
                when {
                    isChecked -> {
                        jsonObject.addProperty(Constants.IS_GLOBAL, 1)
                        settingsViewModel.editProfile(jsonObject)
                    }
                    else -> {
                        jsonObject.addProperty(Constants.IS_GLOBAL, 0)
                        settingsViewModel.editProfile(jsonObject)
                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
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
        /*      if (currentLocationFromDB != null) {
                  if (currentLocationFromDB!!.city != "" && currentLocationFromDB!!.state != "") {

                      Log.e("SettingActivity", "latitude: $latitude")
                      Log.e("SettingActivity", "longitude: $longitude")
                      if (realLatitude == latitude && realLongitude == longitude) {
                          tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
                      } else {
                          tvMyCurrentLocation.text =
                              currentLocationFromDB!!.city.plus(", ").plus(currentLocationFromDB!!.state)
                      }
                  }
              } else {
                  tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
              }*/




        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.e("Location", location.latitude.toString())
                }
            }
        }


        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_FOR_UPDATE,
            MIN_DISTANCE_FOR_UPDATE, this
        )

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location ->
                latitude = location.latitude
                longitude = location.longitude

                val geocoder = Geocoder(this, Locale.getDefault())

                try {
                    val addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )
                    val city = if (addresses.isNotEmpty())
                        addresses[0].locality
                    else  resources.getString(R.string.my_current_location)


                    if (currentLocationFromDB != null) {
                        if (currentLocationFromDB!!.city != "" && currentLocationFromDB!!.state != "") {
                            if (realLatitude == latitude && realLongitude == longitude) {
                                tvMyCurrentLocation.text =
                                    resources.getString(R.string.my_current_location)
                            } else {
                                tvMyCurrentLocation.text = currentLocationFromDB!!.city.plus(", ")
                                    .plus(currentLocationFromDB!!.state)
                            }
                        } else tvMyCurrentLocation.text = city
                    } else {
                        tvMyCurrentLocation.text = city
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
                }
            }


   /*     if (locationManager != null) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                Log.e("Location", location.latitude.toString())
                latitude = location.latitude;
                longitude = location.longitude;
            }
        }*/

        }

        private val mLocationListener = LocationListener {
            //your code here
        }

        private fun setSelectedTextColor(view: TextView, imageView: ImageView) {
            tvInterestMen.setTextColor(Color.GRAY)
            tvInterestWomen.setTextColor(Color.GRAY)
            tvInterestEveryone.setTextColor(Color.GRAY)

/*
        ivMenSelect.visibility = View.GONE
        ivWomenSelect.visibility = View.GONE
        ivEveryoneSelect.visibility = View.GONE
*/

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

                tvSelectInterest -> {
                    if (groupSelectInterest.visibility == View.VISIBLE)
                        groupSelectInterest.visibility = View.GONE
                    else
                        groupSelectInterest.visibility = View.VISIBLE
                }


                tvInterestMen -> {
                    interestIn = 1
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_male_sign,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )

                    groupSelectInterest.visibility = View.GONE
                    tvSelectInterest.text = getString(R.string.men)
//                setSelectedTextColor(tvInterestMen, ivMenSelect)
                }
                tvInterestWomen -> {
                    interestIn = 2
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_female_sign,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )

                    groupSelectInterest.visibility = View.GONE
                    tvSelectInterest.text = getString(R.string.women)
//                setSelectedTextColor(tvInterestWomen, ivWomenSelect)
                }
                tvInterestEveryone -> {
                    interestIn = 3
                    tvSelectInterest.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_non_binary,
                        0,
                        R.drawable.ic_drop_down,
                        0
                    )

                    groupSelectInterest.visibility = View.GONE
                    tvSelectInterest.text = getString(R.string.everyone)
//                setSelectedTextColor(tvInterestEveryone, ivEveryoneSelect)
                }
//            1 = Men
//            2 = Women
//            3 = Everyone
            }
            jsonObject.addProperty(Constants.INTERESTED_IN_GENDER, interestIn)
            Log.d("CreateProfile Request:", jsonObject.toString())
            settingsViewModel.editProfile(jsonObject)

        }

        override fun onSuccess(msg: String) {
            NamasteyApplication.instance.setIsUpdateProfile(true)
            sessionManager.setInterestIn(interestIn)
            sessionManager.setStringValue(maxAge, Constants.KEY_AGE_MAX)
            sessionManager.setStringValue(minAge, Constants.KEY_AGE_MIN)
            sessionManager.setStringValue(distance, Constants.DISTANCE)
            if (switchGlobal.isChecked)
                sessionManager.setIntegerValue(1, Constants.KEY_GLOBAL)
            else
                sessionManager.setIntegerValue(0, Constants.KEY_GLOBAL)
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
        }

        fun onClickMyCurrentLocation(view: View) {
            //startSearchLocationScreen()

//            object : NotAvailableFeatureDialog(
//                this,
//                getString(R.string.location_not_available),
//                getString(R.string.alert_msg_feature_not_available), R.drawable.ic_location
//            ) {
//                override fun onBtnClick(id: Int) {
//                    dismiss()
//                }
//            }.show()

            if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1)
            //startSearchLocationScreen()
                openActivity(this, PassportContentActivity())
            else {
                val intent = Intent(this@SettingsActivity, MemberActivity::class.java)
                openActivity(intent)
            }
        }

        private fun startSearchLocationScreen() {
            val intent = Intent(this@SettingsActivity, SearchLocationActivity::class.java)
            intent.putExtra("latitude", realLatitude)
            intent.putExtra("longitude", realLongitude)
            intent.putExtra("isFromSearch", true)
            openActivity(intent)
        }

        private fun getLocation() {
            appLocationService = AppLocationService(this)
            val gpsLocation: Location? =
                appLocationService.getLocation(LocationManager.GPS_PROVIDER)
            if (gpsLocation != null) {
                realLatitude = gpsLocation.latitude
                realLongitude = gpsLocation.longitude
                Log.e("DashboardActivity", "realLatitude: $realLatitude")
                Log.e("DashboardActivity", "realLongitude: $realLongitude")
            } else {
                tvMyCurrentLocation.text = resources.getString(R.string.my_current_location)
            }
        }

        override fun onLocationChanged(location: Location) {
            realLatitude = location.latitude
            realLongitude = location.longitude
            Log.e("DashboardActivity", "realLatitude: $realLatitude")
            Log.e("DashboardActivity", "realLongitude: $realLongitude")
        }
    }

    private fun LocationManager.requestLocationUpdates(
        networkProvider: String,
        minTimeBwUpdates: Long,
        minDistanceChangeForUpdates: Float,
        settingsActivity: SettingsActivity
    ) {

    }
