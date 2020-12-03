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
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import de.hdodenhof.circleimageview.CircleImageView

class SearchLocationActivity : FragmentActivity(),
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

    /*  @Inject
      lateinit var viewModelFactory: ViewModelFactory
      private lateinit var activitySearchLocationBinding: ActivitySearchLocationBinding
      private lateinit var locationViewModel: LocationViewModel

      override fun getViewModel() = locationViewModel

      override fun getLayoutId() = R.layout.activity_search_location

      override fun getBindingVariable() = BR.viewModel*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_location)

        sessionManager = SessionManager(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        /* val autocompleteFragment: PlaceAutocompleteFragment =
             fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

         autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
             override fun onPlaceSelected(place: Place) {
                 Log.e("SearchLocationActivity", "place: \t ${place.address}")
                 mMap!!.clear()
                 mMap!!.addMarker(
                     MarkerOptions().position(place.latLng).icon(
                         BitmapDescriptorFactory.fromBitmap(
                             createCustomMarker(
                                 this@SearchLocationActivity,
                                 sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
                                 sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                             )
                         )
                     ).title(place.name.toString())
                 )
                 mMap!!.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
                 mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12.0f))
             }

            override fun onError(status: Status?) {
                Log.e("SearchLocationActivity", "status: \t ${status!!.statusCode}")
                Log.e("SearchLocationActivity", "status: \t ${status!!.isSuccess}")
                Log.e("SearchLocationActivity", "status: \t ${status!!.status}")
            }
        })*/

        /*getActivityComponent().inject(this)
        locationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setViewInterface(this)
        activitySearchLocationBinding = bindViewData()
        activitySearchLocationBinding.viewModel = locationViewModel*/

        initData()
    }

    private fun initData() {
        Log.e(
            "SearchLocationActivity",
            " UserImage:\t ${sessionManager.getStringValue(Constants.KEY_PROFILE_URL)}"
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
                            this@SearchLocationActivity,
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
        latitude = location.latitude
        longitude = location.longitude
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarker(
                    this@SearchLocationActivity,
                    sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
                    sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                )
            )
        )
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
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
            .with(this)
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

    private fun searchLocationMarker(context: Context): Bitmap? {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_search_location_marker,
                null
            )

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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

    fun searchLocation(view: View?) {

        startActivity(Intent(this, LocationActivity::class.java))

        /* val locationSearch: EditText = findViewById<EditText>(R.id.searchDestination)
         val location: String = locationSearch.text.toString()
         var addressList: List<Address>? = null
         if (location != null || location != "") {
             val geocoder = Geocoder(this)
             try {
                 addressList = geocoder.getFromLocationName(location, 1)
             } catch (e: IOException) {
                 e.printStackTrace()
             }
             val address: Address = addressList!![0]
             val latLng = LatLng(address.latitude, address.longitude)
             mMap!!.addMarker(
                 MarkerOptions().position(latLng).icon(
                     BitmapDescriptorFactory.fromBitmap(
                         createCustomMarker(
                             this@SearchLocationActivity,
                             sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
                             sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                         )
                     )
                 ).title(location)
             )
             mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
             Toast.makeText(
                 applicationContext,
                 address.latitude.toString() + " " + address.longitude,
                 Toast.LENGTH_LONG
             ).show()

             Log.e(
                 "SearchLocationActivity",
                 "LatLong: \t ${address.latitude.toString() + " " + address.longitude}"
             )
         }*/
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("SearchLocationActivity", "onConnectionFailed: \t ${p0.errorCode}")
        Log.e("SearchLocationActivity", "onConnectionFailed: \t ${p0.errorMessage}")
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    fun onClickSearchLocationBack(view: View) {
        onBackPressed()
    }

    /*override fun onDestroy() {
        locationViewModel.onDestroy()
        super.onDestroy()
    }*/
}