package com.namastey.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.namastey.R
import com.namastey.dagger.module.GlideApp
import com.namastey.roomDB.AppDB
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.RecentLocations
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_passport_content.*

//class PassportContentActivity : BaseActivity<ActivityPassportContentBinding>(), LocationView {


open class PassportContentActivity : FragmentActivity(),
    OnMapReadyCallback,
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var sessionManager: SessionManager
    private var mMap: GoogleMap? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var dbHelper: DBHelper
    private lateinit var appDb: AppDB
    private var currentLocationFromDB: RecentLocations? = null

    /* @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityPassportContentBinding: ActivityPassportContentBinding
    private lateinit var locationViewModel: LocationViewModel*/

    /*override fun getViewModel() = locationViewModel

    override fun getLayoutId() = R.layout.activity_passport_content

    override fun getBindingVariable() = BR.viewModel*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport_content)

        sessionManager = SessionManager(this)
        appDb = AppDB.getAppDataBase(this)!!
        dbHelper = DBHelper(appDb)
        currentLocationFromDB = dbHelper.getLastRecentLocations()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //getActivityComponent().inject(this)
        /* locationViewModel =
             ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
         locationViewModel.setViewInterface(this)
         activityPassportContentBinding = bindViewData()
         activityPassportContentBinding.viewModel = locationViewModel*/

        initData()
    }

    private fun initData() {
        if (sessionManager.getUserGender() == Constants.Gender.male.name) {
            llPassportContentBackground.background = getDrawable(R.drawable.male_bg)
        } else {
            llPassportContentBackground.background = getDrawable(R.drawable.female_bg)
        }
        Log.e(
            "PassportContent",
            " UserImage:\t ${sessionManager.getStringValue(Constants.KEY_PROFILE_URL)}"
        )
    }

    fun onClickSearchDestination(view: View) {
        Utils.hideKeyboard(this@PassportContentActivity)
        val intent = Intent(this@PassportContentActivity, SearchLocationActivity::class.java)
        intent.putExtra("isFromPassPort", true)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit);
        // startActivity(Intent(this, SearchLocationActivity::class.java))
    }

    fun onClickPassportContentBack(view: View) {
        onBackPressed()
    }

    fun onClickSearchIcon(view: View) {
        Utils.hideKeyboard(this@PassportContentActivity)
        //startActivity(Intent(this, LocationActivity::class.java))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMarkerAtLocation()
    }

    private fun setMarkerAtLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }

        mMap!!.setOnMapLoadedCallback {
            val customMarkerLocationOne = LatLng(latitude, longitude)
            mMap!!.addMarker(
                MarkerOptions().position(customMarkerLocationOne).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(
                            this@PassportContentActivity,
                            sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
                            sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                        )
                    )
                )
            ).title = ""

            //LatLngBound will cover all your marker on Google Maps
            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            builder.include(customMarkerLocationOne) //Taking Point A (First LatLng)
            val bounds: LatLngBounds = builder.build()
            val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200)
            mMap!!.moveCamera(cu)
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
        }

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        if (sessionManager.getRecentLocationFromList() != null) {
            latitude = sessionManager.getRecentLocationFromList()!!.latitude
            longitude = sessionManager.getRecentLocationFromList()!!.longitude
            Log.e("PassportContentActivity", "sessionManager latitude:\t $latitude")
            Log.e("PassportContentActivity", "sessionManager longitude:\t $longitude")
        } else
            if (intent.extras != null && intent.extras!!.getDouble("latitude") != 0.0 && intent.extras!!.getDouble(
                    "longitude"
                ) != 0.0
            ) {
                latitude = intent.extras!!.getDouble("latitude", 0.0)
                longitude = intent.extras!!.getDouble("longitude", 0.0)
                Log.e("PassportContentActivity", "extras latitude:\t $latitude")
                Log.e("PassportContentActivity", "extras longitude:\t $longitude")
            } else if (currentLocationFromDB != null) {
                latitude = currentLocationFromDB!!.latitude
                longitude = currentLocationFromDB!!.longitude
            } else {
                latitude = location.latitude
                longitude = location.longitude
            }

        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarker(
                    this@PassportContentActivity,
                    sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
                    sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                )
            )
        )
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))

        mMap!!.setOnMarkerClickListener {
            val intent = Intent(this@PassportContentActivity, LocationActivity::class.java)
            intent.putExtra("isFromPassPort", true)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit);

            // startActivity(Intent(this, LocationActivity::class.java))
            true
        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    override fun onResume() {
        super.onResume()
        currentLocationFromDB = dbHelper.getLastRecentLocations()
        // Log.e("PassportContent", "onResume: \t ${currentLocationFromDB!!.id}")
    }

    override fun onRestart() {
        super.onRestart()
        currentLocationFromDB = dbHelper.getLastRecentLocations()
        //Log.e("PassportContent", "onRestart: \t ${currentLocationFromDB!!.id}")
    }

    private fun createCustomMarker(
        context: Context,
        imageUrl: String,
        _name: String?
    ): Bitmap? {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_image_marker,
                null
            )
        val markerImage: CircleImageView =
            marker.findViewById<View>(R.id.ivUserProfile) as CircleImageView
        //markerImage.setImageResource(imageUrl)

        GlideApp
            .with(this@PassportContentActivity)
            .load(imageUrl)
            .into(markerImage)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("PassportContent", "onConnectionFailed: \t ${p0.errorCode}")
        Log.e("PassportContent", "onConnectionFailed: \t ${p0.errorMessage}")
    }

}