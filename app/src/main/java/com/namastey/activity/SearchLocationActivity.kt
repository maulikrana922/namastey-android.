package com.namastey.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.namastey.R
import com.namastey.dagger.module.GlideApp
import com.namastey.roomDB.AppDB
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.RecentLocations
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_search_location.*
import org.jetbrains.anko.doAsync
import java.util.*

class SearchLocationActivity : FragmentActivity(),
    OnMapReadyCallback,
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private val TAG: String = SearchLocationActivity::class.java.simpleName

    private lateinit var sessionManager: SessionManager
    private var mMap: GoogleMap? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var apiKey = ""
    private var mResult: StringBuilder? = null


    lateinit var dbHelper: DBHelper
    private lateinit var appDb: AppDB

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

        appDb = AppDB.getAppDataBase(this)!!
        dbHelper = DBHelper(appDb)

        sessionManager = SessionManager(this)
        apiKey = getString(R.string.google_api_key)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //searchOnCustomView()
        searchPlace()
        initData()
        //getIntentData()
    }

    private fun initData() {
        Log.e(
            "SearchLocationActivity",
            " UserImage:\t ${sessionManager.getStringValue(Constants.KEY_PROFILE_URL)}"
        )
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            latitude = intent.extras!!.getDouble("latitude", 0.0)
            longitude = intent.extras!!.getDouble("longitude", 0.0)

            Log.e("SearchLocationActivity", " latitude:\t $latitude")
            Log.e("SearchLocationActivity", " longitude:\t $longitude")
        }

    }

    /**
     * Method to search using custom  EditText
     */
    private fun searchOnCustomView() {
        val placesClient: PlacesClient = Places.createClient(this)

        ivLocation.setOnClickListener { v ->
            Toast.makeText(
                this@SearchLocationActivity,
                searchDestination.text.toString(),
                Toast.LENGTH_SHORT
            ).show()
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
            // Create a RectangularBounds object.
            /* val bounds: RectangularBounds = RectangularBounds.newInstance(
                 LatLng(-33.880490, 151.184363),  //dummy lat/lng
                 LatLng(-33.858754, 151.229596)
             )*/
            // Use the builder to create a FindAutocompletePredictionsRequest.
            val request: FindAutocompletePredictionsRequest =
                FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                    //  .setLocationBias(bounds) //.setLocationRestriction(bounds)
                    //  .setCountry("ng") //Nigeria
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(searchDestination.text.toString())
                    .build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                    mResult = StringBuilder()
                    for (prediction in response.autocompletePredictions) {
                        mResult!!.append(" ")
                            .append("""${prediction.getFullText(null)}""".trimIndent())
                        Log.e(TAG, prediction.placeId)
                        Log.e(TAG, prediction.getPrimaryText(null).toString())
                    }
                    Log.e("SearchLocationActivity", " mResult:\t $mResult")
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        val apiException: ApiException = exception
                        Log.e(TAG, "Place not found: " + apiException.statusCode)
                        Log.e(TAG, "Place not found: " + apiException.status)
                        Log.e(TAG, "Place not found: " + apiException.message)
                    }
                }
        }
    }

    /**
     * Method to search in default View
     */
    private fun searchPlace() {
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

        val autocompleteFragment: AutocompleteSupportFragment? =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?

        autocompleteFragment!!.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.e("SearchLocationActivity", "place: \t ${place.latLng}")
                getAddress(place.latLng!!.latitude, place.latLng!!.longitude)
                mMap!!.clear()
                mMap!!.addMarker(
                    MarkerOptions().position(place.latLng!!).icon(
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

            override fun onError(status: Status) {
                Log.e("SearchLocationActivity", "status: \t ${status.statusCode}")
                Log.e("SearchLocationActivity", "status: \t ${status.isSuccess}")
                Log.e("SearchLocationActivity", "status: \t ${status.status}")
            }
        })
    }

    private fun getAddress(latitude: Double, longitude: Double) {

        val addresses: List<Address>
        val geoCoder: Geocoder = Geocoder(this, Locale.getDefault())

        addresses = geoCoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address: String =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].locality
        val state: String = addresses[0].adminArea
        val country: String = addresses[0].countryName
        val postalCode = if (addresses[0].postalCode != null && addresses[0].postalCode != "") {
            addresses[0].postalCode
        } else {
            ""
        }
        val knownName = if (addresses[0].featureName != null && addresses[0].featureName != "") {
            addresses[0].featureName
        } else {
            ""
        }
        // val knownName: String = addresses[0].featureName // Only if available else return NULL

        Log.e("LocationActivity", "getAddress: \taddress: $address")
        Log.e("LocationActivity", "getAddress: \tcity: $city")
        Log.e("LocationActivity", "getAddress: \tstate: $state")
        Log.e("LocationActivity", "getAddress: \tcountry: $country")
        Log.e("LocationActivity", "getAddress: \tpostalCode: $postalCode")
        Log.e("LocationActivity", "getAddress: \tknownName: $knownName")

        val currentTime = System.currentTimeMillis()
        val recentLocation = RecentLocations(
            0,
            city,
            state,
            country,
            postalCode,
            knownName,
            currentTime,
            latitude,
            longitude
        )
        doAsync {
            dbHelper.addRecentLocation(recentLocation)
        }
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
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
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
        if (intent.extras != null && intent.extras!!.getDouble("latitude") != 0.0  && intent.extras!!.getDouble("longitude") != 0.0  ) {

            latitude = intent.extras!!.getDouble("latitude", 0.0)
            longitude = intent.extras!!.getDouble("longitude", 0.0)

            Log.e("SearchLocationActivity", "extras latitude:\t $latitude")
            Log.e("SearchLocationActivity", "extras longitude:\t $longitude")


        } else {
            latitude = location.latitude
            longitude = location.longitude

            Log.e("SearchLocationActivity", " latitude:\t $latitude")
            Log.e("SearchLocationActivity", " longitude:\t $longitude")
        }

        val latLng = LatLng(latitude, longitude)
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
        marker.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

    /*fun searchLocation(view: View?) {
        startActivity(Intent(this, LocationActivity::class.java))
    }*/

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

}