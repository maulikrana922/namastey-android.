package com.namastey.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CurrentLocationAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityLocationBinding
import com.namastey.location.AppLocationService
import com.namastey.uiView.LocationView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.activity_location.*
import java.util.*
import javax.inject.Inject


class LocationActivity : BaseActivity<ActivityLocationBinding>(), LocationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileViewBinding: ActivityLocationBinding
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var currentLocationAdapter: CurrentLocationAdapter
    private lateinit var appLocationService: AppLocationService

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


        currentLocationAdapter = CurrentLocationAdapter(this)
        rvLocation.adapter = currentLocationAdapter

        btnAddNewLocation.setOnClickListener {
            openActivity(this, PassportContentActivity())
        }

        getLocation()
        //getAddress()
        /*addFragment(
            CurrentLocationFragment(),
            Constants.CURRENT_LOCATION_FRAGMENT
        )*/

    }

    private fun getLocation() {
        appLocationService = AppLocationService(this)

        val gpsLocation: Location? = appLocationService.getLocation(LocationManager.GPS_PROVIDER)
        if (gpsLocation != null) {
            val latitude: Double = gpsLocation.latitude
            val longitude: Double = gpsLocation.longitude
            val result = "Latitude: " + gpsLocation.latitude.toString() +
                    " Longitude: " + gpsLocation.longitude

            getAddress(gpsLocation.latitude, gpsLocation.longitude)

            Log.e("LocationActivity", "result: $result")
        } else {
            turnGPSOn()

            //showSettingsAlert()
        }
    }

    private fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("SETTINGS")
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?")
        alertDialog.setPositiveButton("Settings",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val intent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    this@LocationActivity.startActivity(intent)
                }
            })
        alertDialog.setNegativeButton("Cancel",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                }
            })
        alertDialog.show()
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val location = appLocationService.getLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city: String = addresses[0].locality
            val state: String = addresses[0].adminArea
            val country: String = addresses[0].countryName
            val postalCode: String = addresses[0].postalCode
            val knownName: String = addresses[0].featureName // Only if available else return NULL

            if (knownName != null && knownName != "" || city != null && city != "" || state != null && state != "") {
                tvMyCurrentLocation.text = "$knownName $city $state"
            }
            if (country != null && country != "" || postalCode != null && postalCode != "") {
                tvMyCurrentAddress.text = "$country $postalCode"
            }

            Log.e("LocationActivity", "getAddress: \taddress: $address")
            Log.e("LocationActivity", "getAddress: \tcity: $city")
            Log.e("LocationActivity", "getAddress: \tstate: $state")
            Log.e("LocationActivity", "getAddress: \tcountry: $country")
            Log.e("LocationActivity", "getAddress: \tpostalCode: $postalCode")
            Log.e("LocationActivity", "getAddress: \tknownName: $knownName")
        } else {
            turnGPSOn()
            //showSettingsAlert()
        }
    }

    private fun turnGPSOn() {
        val provider =
            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        Log.e("LocationActivity", "provider: $provider")
        if (!provider.contains("gps")) { //if gps is disabled
            val poke = Intent()
            poke.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            this.sendBroadcast(poke)
        }

        /* val intent =
             Intent("android.location.GPS_ENABLED_CHANGE")
         intent.putExtra("enabled", true)
         sendBroadcast(intent)*/
    }

    /*private fun getAddress() {
        val location = appLocationService.getLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            val locationAddress = LocationAddress()
            locationAddress.getAddressFromLocation(
                latitude,
                longitude,
                applicationContext,
                GeoCoderHandler()
            )
            Log.e("LocationActivity", "getAddress: \tlocationAddress: $locationAddress")
        } else {
            showSettingsAlert()
        }
    }


    private class GeoCoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle: Bundle = message.data
                    bundle.getString("address")
                }
                else -> null
            }
            Log.e("LocationActivity", "locationAddress: $locationAddress")
        }
    }*/

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