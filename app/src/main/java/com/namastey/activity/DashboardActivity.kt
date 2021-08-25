package com.namastey.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import com.namastey.BR
import com.namastey.BuildConfig
import com.namastey.R
import com.namastey.adapter.*
import com.namastey.application.NamasteyApplication
import com.namastey.customViews.ExoPlayerRecyclerView
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.fcm.MyFirebaseMessagingService
import com.namastey.fragment.NotificationFragment
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.*
import com.namastey.location.AppLocationService
import com.namastey.model.*
import com.namastey.receivers.MaxLikeService
import com.namastey.roomDB.AppDB
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.RecentLocations
import com.namastey.uiView.DashboardView
import com.namastey.utils.*
import com.namastey.viewModel.DashboardViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_boost_not_available.view.*
import kotlinx.android.synthetic.main.dialog_boost_success.view.*
import kotlinx.android.synthetic.main.dialog_boost_success.view.btnAlertOk
import kotlinx.android.synthetic.main.dialog_boost_time_pending.view.*
import kotlinx.android.synthetic.main.dialog_boost_time_pending.view.tvTimeRemaining
import kotlinx.android.synthetic.main.dialog_bottom_category.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.*
import kotlinx.android.synthetic.main.dialog_bottom_share_profile.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.dialog_membership.view.*
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_share_app.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), PurchasesUpdatedListener,
    DashboardView, OnFeedItemClick,
    OnSelectUserItemClick, OnMentionUserItemClick, LocationListener, OnSocialTextViewClick,
    CategoryAdapter.OnItemClickCategory, SubCategoryAdapter.OnItemClick, OnItemClick {
    private val TAG = "DashboardActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var appLocationService: AppLocationService
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var mentionArrayAdapter: ArrayAdapter<Mention>
    private var followingList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var userShareAdapter: UserShareAdapter
    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var bottomSheetDialogShareApp: BottomSheetDialog
    private lateinit var bottomSheetDialogShare: BottomSheetDialog
    private lateinit var bottomSheetDialogComment: BottomSheetDialog
    private lateinit var bottomSheetCategoryDialog: BottomSheetDialog
    private var colorDrawableBackground = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private var position = -1
    private var commentCount = -1
    private var isUpdateComment = false
    private var fileUrl = ""
    private var postId = 0L
    private var notification: Notification = Notification()
    private var fragmentRefreshListener: FragmentRefreshListener? = null
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var membershipViewList = ArrayList<MembershipPriceBean>()
    private var currentPage = 1
    private var mbNext = true
    private var mbLoading = true

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isFromSetting = false       // GPS enable and reload data
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var dbHelper: DBHelper
    private lateinit var appDb: AppDB
    private var currentLocationFromDB: RecentLocations? = null
    private var mIntent: IntentFilter? = null

    private var timer = 0L
    private var isFromProfile = false
    private var mRecyclerView: ExoPlayerRecyclerView? = null

    //    private lateinit var videoAutoPlayHelper: VideoAutoPlayHelper
    private var mLayoutManager: LinearLayoutManager? = null
    private var firstTime = true

    private var videoIdList: ArrayList<Long> = ArrayList()
    private var userIdList: ArrayList<Long> = ArrayList()
    private var noOfCall = 0
    private var totalCount = 1

    //In App Product Price
    private lateinit var billingClient: BillingClient
    private val subscriptionSkuList = listOf("000010", "000020", "000030")

    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_dashboard

    override fun getBindingVariable() = BR.viewModel
    private fun startMaxLikeService() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 0
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MaxLikeService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        dashboardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        activityDashboardBinding = bindViewData()
        activityDashboardBinding.viewModel = dashboardViewModel

        startMaxLikeService()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@DashboardActivity)

        //addLocationPermission()

//        getLocation()
        initData()
        getDataFromIntent(intent!!)
    }

    override fun onStart() {
        super.onStart()
        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        //TODO: UI updates.
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this@DashboardActivity)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null);

    }

    private fun initData() {
        sessionManager.setLoginUser(true)
//        Log.e("DashboardActivity", "FireBaseToken: ${sessionManager.getFirebaseToken()}")

        appDb = AppDB.getAppDataBase(this)!!
        dbHelper = DBHelper(appDb)
        currentLocationFromDB = dbHelper.getLastRecentLocations()
        mRecyclerView = findViewById(R.id.viewpagerFeed)

        if (sessionManager.getBooleanValue(Constants.KEY_IS_BOOST_ACTIVE)) {

            val currentStamp = Timestamp(System.currentTimeMillis())
            val storeTime = Timestamp(sessionManager.getLongValue(Constants.KEY_BOOST_STAR_TIME))
            val diff = currentStamp.time - storeTime.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            Log.d("Days second : ", seconds.toString())
            Log.d("Days minutes : ", minutes.toString())
            Log.d("Days hours : ", hours.toString())
            Log.d("Days days : ", days.toString())
            Log.d(
                "current time : ",
                Utils.convertTimestampToChatFormat(System.currentTimeMillis()).toString()
            )
            if (minutes > 30) {
                sessionManager.setBooleanValue(false, Constants.KEY_IS_BOOST_ACTIVE)
            }
        }
        if (sessionManager.getUserGender() == Constants.Gender.female.name) {
            ivUser.setImageResource(R.drawable.ic_female_user)
        } else {
            ivUser.setImageResource(R.drawable.ic_top_profile)
        }
        if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
            GlideApp.with(this@DashboardActivity)
                .load(sessionManager.getStringValue(Constants.KEY_PROFILE_URL))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.default_placeholder)
                .fitCenter().into(ivUser)
        }

        if (intent.hasExtra("isFromProfile")) {
            isFromProfile = intent.getBooleanExtra("isFromProfile", false)
            Log.e("DashboardActivity", "isFromProfile: \t $isFromProfile")
        }

        dashboardViewModel.getPurchaseStatus()
        //dashboardViewModel.getMembershipPriceList()
        dashboardViewModel.getCategoryList()
        // dashboardViewModel.getNewFeedList(currentPage, 0, latitude, longitude)
        // dashboardViewModel.getNewFeedListV2(currentPage, 0, latitude, longitude, videoIdList)
//        getFeedListApi(0)

//        val snapHelper: SnapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(rvFeed)

//        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this, sessionManager)
//        rvFeed.adapter = feedAdapter
//
//        /*Helper class to provide AutoPlay feature inside cell*/
//        videoAutoPlayHelper =
//            VideoAutoPlayHelper(rvFeed)
//        videoAutoPlayHelper.startObserving();
        getVideoUrl()


//        setDashboardList()

        Handler(Looper.getMainLooper()).postDelayed({
            setupPermissions()
        }, 2000)
        setSliderData()
        //startPagination()

        // dashboardViewModel.getFeedList(0)

        mentionArrayAdapter = MentionArrayAdapter(this)
    }

    private fun getVideoUrl() {
        /* mRecyclerView!!.layoutManager =
             LinearLayoutManager(this, LinearLayout.VERTICAL, false)*/
        mLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager

        /* val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
         linearSnapHelper.attachToRecyclerView(mRecyclerView)*/

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)

        //set data object
        mRecyclerView!!.setDashboardBeans(feedList)
        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this, sessionManager)
        mRecyclerView!!.adapter = feedAdapter

//        if (firstTime) {
//            Handler(Looper.getMainLooper()).post { mRecyclerView!!.playVideo(false) }
//            firstTime = false
//        }
    }

    private fun addLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openActivity(this, PassportContentActivity())
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PERMISSION_REQUEST_CODE
                )
            }
        } else {
            openActivity(this, PassportContentActivity())
        }
    }

    fun getFeedListApi(subCat: Int, isFollowingSearch: Boolean) {
        val jsonObject = JSONObject()

        val videoListJsonArray = JSONArray(videoIdList)
        var userListJsonArray = JSONArray(userIdList)
        // var userListJsonArray

        Log.e("DashboardActivity", "totalCount: \t $totalCount")
        Log.e("DashboardActivity", "userIdList: \t ${userIdList.size}")

        if (totalCount >= 10) {
            userListJsonArray = JSONArray(userIdList)
        } else {
            userListJsonArray = JSONArray(ArrayList<Long?>())
            userIdList.clear()
        }

        Log.e("DashboardActivity", "userListJsonArray: \t $userListJsonArray")

        jsonObject.put("ids", videoListJsonArray)
        jsonObject.put("user_ids", userListJsonArray)
        jsonObject.put("page", 1)
        jsonObject.put("sub_cat_id", subCat)
        jsonObject.put("lat", latitude)
        jsonObject.put("lng", longitude)

        if (isFollowingSearch)
            jsonObject.put("following_id", sessionManager.getUserId())

//        val feedVideoRequest = FeedVideoRequest(jsonArray,1,subCat, latitude, longitude)
        Log.e("DashboardActivity", "jsonObject: \t $jsonObject")

        val jsonParser = JsonParser()
        val gsonObject = jsonParser.parse(jsonObject.toString()) as JsonObject

        if (totalCount != 0)
            dashboardViewModel.getNewFeedListV2(gsonObject)

        // userIdList.clear()
    }

    private fun setupPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.location_permission_message))
                    .setTitle(getString(R.string.permission_required))

                builder.setPositiveButton(getString(R.string.ok)) { dialog, id ->
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else
                makeRequest()
        } else {
            getLastKnownLocation()
//            getLocation()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Display share option if user login
     */
    private fun openShareOptionDialog(dashboardBean: DashboardBean) {
        bottomSheetDialogShare = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetDialogShare.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_feed_new,
                null
            )
        )

        if (dashboardBean.is_share == 0) {
            bottomSheetDialogShare.svShareOption.alpha = 0.3f
        }
        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)

//        bottomSheetDialogShare.tvShareCancel.setOnClickListener {
//            bottomSheetDialogShare.dismiss()
//        }
        bottomSheetDialogShare.tv_user_name.text = dashboardBean.username
        bottomSheetDialogShare.tv_Job.text = dashboardBean.job
        if (dashboardBean.profile_url.isNotBlank()) {
            GlideLib.loadImage(
                this@DashboardActivity,
                bottomSheetDialogShare.iv_user_profile,
                dashboardBean.profile_url
            )
        }
        if (dashboardBean.is_share == 1) {
            // Share on Twitter app if install otherwise web link
            bottomSheetDialogShare.ivShareWhatssapp.setOnClickListener {
                postShare(dashboardBean.id.toInt())
                shareWhatsApp(dashboardBean)
            }
            bottomSheetDialogShare.ivShareApp.setOnClickListener {
                bottomSheetDialogShare.dismiss()
                postShare(dashboardBean.id.toInt())
                shareWithInApp(dashboardBean)
            }
            bottomSheetDialogShare.ivShareFacebook.setOnClickListener {
                postShare(dashboardBean.id.toInt())
                shareFaceBook(dashboardBean)
            }
            bottomSheetDialogShare.ivShareInstagram.setOnClickListener {
                postShare(dashboardBean.id.toInt())
                shareInstagram(dashboardBean)
            }
            bottomSheetDialogShare.ivShareTwitter.setOnClickListener {
                postShare(dashboardBean.id.toInt())
                shareTwitter(dashboardBean)
            }
            bottomSheetDialogShare.ivShareOther.setOnClickListener {
                postShare(dashboardBean.id.toInt())
                shareOther(dashboardBean)
            }
        }
        if (dashboardBean.is_download == 0) {
            bottomSheetDialogShare.ivShareSave.visibility = View.GONE
            bottomSheetDialogShare.tvShareSave.visibility = View.GONE
        } else {
            bottomSheetDialogShare.ivShareSave.visibility = View.VISIBLE
            bottomSheetDialogShare.tvShareSave.visibility = View.VISIBLE
            if (dashboardBean.is_saved == 1){
                bottomSheetDialogShare.ivShareSave.setImageDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.ic_save_fill))
            }
        }
        if(dashboardBean.is_reported == 1){
            bottomSheetDialogShare.ivShareReport.setImageDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.ic_report_fill))
        }
        //Bottom Icons
        bottomSheetDialogShare.ivShareSave.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            if (dashboardBean.is_saved == 1) {
                object : CustomAlertDialog(
                    this@DashboardActivity,
                    resources.getString(R.string.alert_save_message),
                    getString(R.string.okay),
                    ""
                ) {
                    override fun onBtnClick(id: Int) {
                        dismiss()
                    }
                }.show()
            } else {
                dashboardViewModel.savePost(dashboardBean.id)
            }
        }
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }

        bottomSheetDialogShare.show()
    }

    private fun shareWhatsApp(dashboardBean: DashboardBean) {
        try {
            val pm: PackageManager = packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                sendIntent.type = "*/*"
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.whatsapp")

//                sendIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    Uri.parse(dashboardBean.video_url)

            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                Uri.parse(dashboardBean.video_url)
            )
            startActivity(sendIntent)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(
                this@DashboardActivity,
                getString(R.string.whatsapp_not_install_message),
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun shareWithInApp(dashboardBean: DashboardBean) {
        Log.e("DashboardActivity", "cover_image_url: \t ${dashboardBean?.cover_image_url}")
        var coverImage = ""
        if (dashboardBean.cover_image_url != null && dashboardBean.cover_image_url != "") {
            coverImage = dashboardBean.cover_image_url
        } else {
            coverImage = ""
        }
        val message = String.format(
            getString(R.string.profile_link_msg),
            dashboardBean.username
        ).plus(" \n").plus(String.format(getString(R.string.profile_link), dashboardBean.username))

        bottomSheetDialogShareApp = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetDialogShareApp.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_profile,
                null
            )
        )
        bottomSheetDialogShareApp.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShareApp.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShareApp.setCancelable(true)
        dashboardViewModel.getFollowingShareList()
        bottomSheetDialogShareApp.show()
        searchFollowing()

        bottomSheetDialogShareApp.ivShareClose.setOnClickListener {
            bottomSheetDialogShareApp.dismiss()
        }

        bottomSheetDialogShareApp.tvDone.setOnClickListener {
            bottomSheetDialogShareApp.dismiss()
        }

//        addFragment(
//            ShareAppFragment.getInstance(
//                sessionManager.getUserId(),
//                // dashboardBean?.cover_image_url,
//                coverImage,
//                dashboardBean.video_url,
//                message,
//                false
//            ),
//            Constants.SHARE_APP_FRAGMENT
//        )
    }

    private fun searchFollowing() {
        bottomSheetDialogShareApp.searchProfileShare.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    bottomSheetDialogShareApp.rvSearchUsers.visibility = View.VISIBLE
                    filter(newText.toString().trim())

                } else {
                    filter("")
                    //shareAppViewModel.getFollowingList(userId)
                    dashboardViewModel.getFollowingShareList()
                }

                return true
            }
        })

        bottomSheetDialogShareApp.searchProfileShare.setOnCloseListener {
            filter("")
            dashboardViewModel.getFollowingShareList()
            //shareAppViewModel.getFollowingList(userId)

            false
        }
    }


    private fun filter(text: String) {
//        Log.e("ShareAppFragment", "filter: text: $text")
        val filteredName: ArrayList<DashboardBean> = ArrayList()

        for (following in followingList) {
            if (following.username.toLowerCase().contains(text.toLowerCase())) {
                filteredName.add(following)
            }
        }

        userShareAdapter.filterList(filteredName)
    }


    private fun shareFaceBook(dashboardBean: DashboardBean) {
        var facebookAppFound = false
        var shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, dashboardBean.video_url)
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(dashboardBean.video_url))

        val pm: PackageManager = packageManager
        val activityList: List<ResolveInfo> = pm.queryIntentActivities(shareIntent, 0)
        for (app in activityList) {
            if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                val activityInfo: ActivityInfo = app.activityInfo
                val name =
                    ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                shareIntent.component = name
                facebookAppFound = true
                break
            }
        }
        if (!facebookAppFound) {
            val sharerUrl =
                "https://www.facebook.com/sharer/sharer.php?u=${dashboardBean.video_url}"
            shareIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(sharerUrl)
            )
        }
        startActivity(shareIntent)
    }

    private fun shareInstagram(dashboardBean: DashboardBean) {
        var intent =
            packageManager.getLaunchIntentForPackage("com.instagram.android")
        if (intent != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.instagram.android")
            try {

                val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val fileName = "share_video.mp4"
                val file = File(folder, fileName)
                val uri = let {
                    FileProvider.getUriForFile(
                        it,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        file
                    )
                }

                fileUrl = dashboardBean.video_url

                downloadFile(this@DashboardActivity, fileUrl, uri)

            } catch (e: Exception) {
                Log.e("ERROR", e.printStackTrace().toString())
            }

        } else {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=" + "com.instagram.android")
            startActivity(intent)
        }
    }

    private fun shareTwitter(dashboardBean: DashboardBean) {
        val tweetUrl =
            StringBuilder("https://twitter.com/intent/tweet?text=")
        tweetUrl.append(dashboardBean.video_url)
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(tweetUrl.toString())
        )
        val matches: List<ResolveInfo> =
            packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.toLowerCase(Locale.ROOT)
                    .startsWith("com.twitter")
            ) {
                intent.setPackage(info.activityInfo.packageName)
            }
        }
        startActivity(intent)
    }

    private fun shareOther(dashboardBean: DashboardBean) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sendIntent.putExtra(
            Intent.EXTRA_TEXT, dashboardBean.video_url
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun postShare(postId: Int) {
        dashboardViewModel.postShare(postId, 1)
    }

    /**
     * Display dialog of report user
     */
    private fun displayReportUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonNewAlertDialog(
            this,
            dashboardBean.casual_name,
            getString(R.string.msg_report_user),
            dashboardBean.profile_url,
            getString(R.string.confirm),
            resources.getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                        bottomSheetDialogShare.dismiss()
                        showReportTypeDialog(dashboardBean.user_id)
                    }
                }
            }
        }.show()
    }

    /**
     * Display dialog with option (Reason)
     */
    private fun showReportTypeDialog(reportUserId: Long) {
        val bottomSheetReport: BottomSheetDialog =
            BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetReport.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_pick,
                null
            )
        )
        bottomSheetReport.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetReport.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetReport.setCancelable(false)

        bottomSheetReport.tvPhotoTake.text = getString(R.string.its_spam)
        bottomSheetReport.tvPhotoTake.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoChoose.text = getString(R.string.its_inappropriate)
        bottomSheetReport.tvPhotoChoose.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoCancel.setTextColor(Color.BLUE)

        bottomSheetReport.tvPhotoTake.setOnClickListener {
            bottomSheetReport.dismiss()
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_spam))
        }
        bottomSheetReport.tvPhotoChoose.setOnClickListener {
            bottomSheetReport.dismiss()
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_inappropriate))
        }
        bottomSheetReport.tvPhotoCancel.setOnClickListener {
            bottomSheetReport.dismiss()
        }
        bottomSheetReport.show()
    }

    private fun displayBlockUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonNewAlertDialog(
            this,
            dashboardBean.casual_name,
            getString(R.string.msg_block_user),
            dashboardBean.profile_url,
            getString(R.string.confirm),
            resources.getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                        bottomSheetDialogShare.dismiss()
                        dashboardViewModel.blockUser(dashboardBean.user_id, 1)
                    }
                }
            }
        }.show()
    }

    private fun showMembershipDialog(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DashboardActivity)
        //  val viewGroup: ViewGroup = layoutView.findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this@DashboardActivity)
                .inflate(R.layout.dialog_membership, null, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        if (!(this@DashboardActivity as Activity).isFinishing()) {
            alertDialog.show()
        }

        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)
        //manageVisibility(dialogView)
        setupBillingClient(dialogView)
        viewpager.adapter =
            MembershipDialogSliderAdapter(this@DashboardActivity, membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position
        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setupBillingClient(view: View) {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "setupBillingClient: Setup Billing Done")
                    loadAllSubsSKUs(view)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e(TAG, "setupBillingClient: Failed")

            }
        })
    }

    private fun loadAllSubsSKUs(view: View) = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(subscriptionSkuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e(TAG, "loadAllSubsSKUs: billingResult ${billingResult.responseCode}")
            Log.e(TAG, "loadAllSubsSKUs: skuDetailsList $skuDetailsList")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (i in skuDetailsList.indices) {
                    val skuDetails = skuDetailsList[i]
                    if (skuDetails.sku == "000010") {
                        val price = Utils.splitString(skuDetails.price, 1)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        view.tvTextLowEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.per_month))
                    }

                    if (skuDetails.sku == "000020") {
                        val price = Utils.splitString(skuDetails.price, 6)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        view.tvTextMediumEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.per_month))
                    }

                    if (skuDetails.sku == "000030") {
                        val price = Utils.splitString(skuDetails.price, 12)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        view.tvTextHighEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.per_month))
                    }
                    manageVisibility(view)
                }
            } else if (billingResult.responseCode == 1) {
                //user cancel
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 2) {
                Toast.makeText(this, "Internet required for purchase", Toast.LENGTH_LONG)
                    .show()
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 3) {
                Toast.makeText(
                    this,
                    "Incompatible Google Play Billing Version",
                    Toast.LENGTH_LONG
                ).show()
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 7) {
                Toast.makeText(this, "you already own Premium", Toast.LENGTH_LONG)
                    .show()
                return@querySkuDetailsAsync
            } else
                Toast.makeText(this, "no skuDetails sorry", Toast.LENGTH_LONG)
                    .show()
        }
    } else {
        println("Billing Client not ready")
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.e(TAG, "onPurchasesUpdated: debugMessage $billingResult")
        Log.e(TAG, "onPurchasesUpdated: responseCode ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.e(TAG, "purchase: \t $purchase")
                Log.e(TAG, "purchaseToken: \t ${purchase.purchaseToken}")
                Log.e(TAG, "purchaseToken: \t $purchase")

                finish()
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.e(TAG, "onPurchasesUpdated User Cancelled")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Service Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Billing Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Item Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            Log.e(TAG, "onPurchasesUpdated Developer Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR) {
            Log.e(TAG, "onPurchasesUpdated  Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item already owned")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item not owned")
            finish()
        } else {
            Log.e(TAG, "onPurchasesUpdated: debugMessage ${billingResult.debugMessage}")
            finish()
        }
    }


    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.boost_your_love_life),
                //resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes1),
                resources.getString(R.string.do_not_want_to_wait_slider),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                //resources.getString(R.string.swipe_around_the_world),
                resources.getString(R.string.explore_the_globe),
                resources.getString(R.string.around_the_world_in_80_seconds),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.super_message),
                resources.getString(R.string.express_your_feelings),
                //getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                // resources.getString(R.string.see_who_like_you),
                getString(R.string.see_who_like_you1),
                resources.getString(R.string.your_crush_is_waiting),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
    }

    private fun setSliderDataTemp() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes),
                getString(R.string.do_not_want_to_wait_slider),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.swipe_around_the_world),
                getString(R.string.passport_to_anywhere),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._5_free_super_message),
                getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.see_who_like_you),
                getString(R.string.month_with_them_instantly),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
    }

    private fun manageVisibility(view: View) {
        val constHigh = view.findViewById<ConstraintLayout>(R.id.constHigh)
        val constMedium = view.findViewById<ConstraintLayout>(R.id.constMedium)
        val constLow = view.findViewById<ConstraintLayout>(R.id.constLow)

        /*for (data in membershipViewList) {
            val membershipType = data.membership_type
            val price = data.price
            val discount = data.discount_pr

            Log.e("DashboardActivity", "numberOfBoost: \t $membershipType")
            Log.e("DashboardActivity", "price: \t $price")
            Log.e("DashboardActivity", "discount: \t $discount")

            if (membershipType == 0) {
                view.tvTextLowEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
            }

            if (membershipType == 1) {
                view.tvTextMediumEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))

            }

            if (membershipType == 2) {
                view.tvTextHighEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))
            }
        }*/

        constLow.setOnClickListener {
            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.white
                )
            )
            //  view.tvOfferLow.visibility = View.VISIBLE
            view.viewSelectedLow.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
        }

        constMedium.setOnClickListener {
            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.white
                )
            )
            view.tvOfferMedium.visibility = View.VISIBLE
            view.viewSelectedMedium.visibility = View.VISIBLE

            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
        }

        constHigh.setOnClickListener {
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorBlueLight
                )
            )
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.white
                )
            )
            view.tvOfferHigh.visibility = View.VISIBLE
            view.viewSelectedHigh.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    R.color.colorDarkGray
                )
            )
        }
    }

    /**
     * Success of get category list
     */
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        this.categoryBeanList = categoryBeanList
        // tvDiscover.visibility = View.VISIBLE


//        setDashboardList()

    }

    override fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessFeedFinal(dashboardList: ArrayList<DashboardBean>, total: Int) {
        NamasteyApplication.instance.setIsUpdateProfile(false)
        Log.e("DashboardActivity", "onSuccessNewFeed: ${dashboardList.size}")
        Log.e("DashboardActivity", "onSuccessNewFeed: total\t $total")
        totalCount = total
        feedList.addAll(dashboardList)


        if (feedList.size == 0) {
            mRecyclerView!!.visibility = View.GONE
            groupNoOne.visibility = View.VISIBLE
//            if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
//                GlideLib.loadImage(
//                    this, ivProfile, sessionManager.getStringValue(
//                        Constants.KEY_PROFILE_URL
//                    )
//                )
//            }

        } else {
            mRecyclerView!!.visibility = View.VISIBLE
            groupNoOne.visibility = View.GONE
        }
        //getVideoUrl()
        feedAdapter.notifyDataSetChanged()
        if (firstTime) {
            Handler(Looper.getMainLooper()).post { mRecyclerView!!.playVideo(false, false, 0) }
            firstTime = false
        }
        mbNext = dashboardList.size != 0

        if (mbNext) {
            mbLoading = false
        }

        // noOfCall += 1
        for (ids in dashboardList) {
            if (!videoIdList.contains(ids.id)) {
                videoIdList.add(ids.id)
            }
            userIdList.add(ids.user_id)
        }
        Log.e("DashboardActivity", "onSuccessNewFeed: userIdList\t $userIdList")
    }

    // Temp open this activity
    fun onClickUser(view: View) {
        //openActivity(this, ProfileViewActivity())
        val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, sessionManager.getUserId())
        intent.putExtra("ownProfile", true)
        openActivity(intent)
    }

    fun onClickInbox(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                val intent = Intent(this@DashboardActivity, MatchesActivity::class.java)
                intent.putExtra("isFromDashboard", true)
                openActivity(intent)
            } else {
                completeSignUpDialog()
            }
        }
    }

    override fun onDestroy() {
        dashboardViewModel.onDestroy()
        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()

        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

//        if (CacheDataSourceFactory.getInstance(this@DashboardActivity) != null){
//            CacheDataSourceFactory.getInstance(this@DashboardActivity).release()
//        }
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
//        if (::videoAutoPlayHelper.isInitialized)
//            videoAutoPlayHelper.removePlayer()

        super.onDestroy()
        unregisterReceiver(notificationBroadcast)
        //feedAdapter.releasePlayer()
        //feedAdapter.stopPlayer()
        //if (Util.SDK_INT <= 23) releasePlayer()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Constants.REQUEST_CODE && data != null) {
            when {
                data.hasExtra("fromSubCategory") -> {
                    with(dashboardViewModel) {
                        feedList.clear()
                        currentPage = 1
                        totalCount = 1
                        videoIdList.clear()
                        firstTime = true
                        /* dashboardViewModel.getNewFeedListV2(
                             currentPage,
                             data.getIntExtra("subCategoryId", 0),
                             latitude,
                             longitude,
                             videoIdList
                         )*/

                        getFeedListApi(data.getIntExtra("subCategoryId", 0), false)


                        /*currentPage = 1
                        getNewFeedList(
                            currentPage,
                            data.getIntExtra("subCategoryId", 0),
                            latitude,
                            longitude
                        )*/
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.FILTER_OK) {
            if (data != null && data.hasExtra("fromSubCategory")) {
                supportFragmentManager.popBackStack()
                with(dashboardViewModel) {
                    feedList.clear()
                    currentPage = 1
                    totalCount = 1
                    videoIdList.clear()
                    firstTime = true
                    /* dashboardViewModel.getNewFeedListV2(
                         currentPage,
                         0,
                         latitude,
                         longitude,
                         videoIdList
                     )*/

                    getFeedListApi(data.getIntExtra("subCategoryId", 0), false)


                    /*getNewFeedList(
                        currentPage,
                        data.getIntExtra("subCategoryId", 0),
                        latitude,
                        longitude
                    )*/
                }
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    private fun downloadFile(context: Context, url: String, file: Uri) {
        val ktor = HttpClient(Android)
        dashboardViewModel.setDownloading(true)
        bottomSheetDialogShare.progress_bar.visibility = View.VISIBLE
        bottomSheetDialogShare.ivShareInstagram.visibility = View.INVISIBLE
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                dashboardViewModel.setDownloading(false)
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                if (::bottomSheetDialogShare.isInitialized)
                                    bottomSheetDialogShare.dismiss()
                                bottomSheetDialogShare.progress_bar.progress = 0
                                openInstagram(file)
                            }
                            is DownloadResult.Error -> {
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                dashboardViewModel.setDownloading(false)
                                Toast.makeText(
                                    context,
                                    "Error while downloading file",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is DownloadResult.Progress -> {
                                bottomSheetDialogShare.progress_bar.progress = it.progress
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openInstagram(uri: Uri) {
        let { context ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.instagram.android")
            shareIntent.type = "video/*"

            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
            startActivity(shareIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.location_permission_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog, id ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_CODE
                            )
                        }

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.permission_denied_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.go_to_settings)
                        ) { dialog, id ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                        val dialog = builder.create()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                    }

                } else {
                    getLastKnownLocation()
//                    getLocation()
                }
            }
        }
    }

    /**
     * user_profile_type = 1 means private
     * and user_profile_type = 0 means public
     */
    override fun onClickFollow(position: Int, dashboardBean: DashboardBean, isFollow: Int) {
        if (sessionManager.isGuestUser() && dashboardBean.user_profile_type == 1) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                this.position = position
                dashboardViewModel.followUser(dashboardBean.user_id, isFollow)
            } else {
                completeSignUpDialog()
            }
        }
    }

    override fun onItemClick(position: Int, dashboardBean: DashboardBean) {
        this.position = position

        // if (sessionManager.isGuestUser() && dashboardBean.user_profile_type == 1) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        } else {
            openShareOptionDialog(dashboardBean)
        }
    }

    override fun onProfileLikeClick(position: Int, dashboardBean: DashboardBean, isLike: Int) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                this.position = position
                if (isLike == 1) {
                    animationLike.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        animationLike.visibility = View.GONE
                    }, 2000)
                }
                dashboardViewModel.likeUserProfile(dashboardBean.user_id, isLike)
            } else {
                completeSignUpDialog()
            }
        }
    }

    /*fun animateHeart(view: ImageView) {
    val scaleAnimation = ScaleAnimation(
        0.0f, 1.0f, 0.0f, 1.0f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    prepareAnimation(scaleAnimation)
    val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
    prepareAnimation(alphaAnimation)
    val animation = AnimationSet(true)
    animation.addAnimation(alphaAnimation)
    animation.addAnimation(scaleAnimation)
    animation.setDuration(700)
    animation.setFillAfter(true)
    view.startAnimation(animation)
}p

private fun prepareAnimation(animation: Animation): Animation? {
    animation.setRepeatCount(1)
    animation.setRepeatMode(Animation.REVERSE)
    return animation
}*/
    fun onClickDiscover(view: View) {
//        if (sessionManager.isGuestUser()) {
//            addFragment(
//                SignUpFragment.getInstance(
//                    true
//                ),
//                Constants.SIGNUP_FRAGMENT
//            )
//        } else if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
//            completeSignUpDialog()
//        } else {
        feedList.clear()
        videoIdList.clear()
        currentPage = 1
        totalCount = 1
        getFeedListApi(0, false)
        val intent = Intent(this@DashboardActivity, FilterActivity::class.java)
        intent.putExtra("categoryList", categoryBeanList)
        openActivityForResult(intent, Constants.FILTER_OK)
//        }

        /* feedList.clear()
         dashboardViewModel.getNewFeedList(currentPage, 0, latitude, longitude)
         val intent = Intent(this@DashboardActivity, FilterActivity::class.java)
         intent.putExtra("categoryList", categoryBeanList)
         openActivityForResult(intent, Constants.FILTER_OK)*/
    }

    /**
     * Click on comment count display list of comment and add comment dialog
     */
    override fun onCommentClick(position: Int, postId: Long) {
        this.position = position
        this.postId = postId
        var intent = Intent(this, CommentActivity::class.java)
        intent.putExtra("postId", this.postId)
        intent.putExtra("position", this.position)
        openActivity(intent)
    }

    /*this.position = position
    this.postId = postId
    bottomSheetDialogComment = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
    bottomSheetDialogComment.setContentView(
        layoutInflater.inflate(
            R.layout.dialog_bottom_post_comment,
            null
        )
    )
    bottomSheetDialogComment.window?.setBackgroundDrawableResource(android.R.color.transparent)
    bottomSheetDialogComment.window?.attributes?.windowAnimations = R.style.DialogAnimation
    bottomSheetDialogComment.setCancelable(true)
    deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white)!!

    dashboardViewModel.getCommentList(postId)

    bottomSheetDialogComment.ivCommentAdd.setOnClickListener {
        if (sessionManager.isGuestUser()) {
            bottomSheetDialogComment.dismiss()
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                if (bottomSheetDialogComment.edtComment.text.toString().isNotBlank()) {
                    dashboardViewModel.addComment(
                        postId,
                        bottomSheetDialogComment.edtComment.text.toString()
                    )
                }
            } else {
                completeSignUpDialog()
            }
        }
    }

    bottomSheetDialogComment.edtComment.setOnClickListener {
        if (sessionManager.isGuestUser()) {
            bottomSheetDialogComment.dismiss()
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        }
    }

    addCommentsTextChangeListener()

    bottomSheetDialogComment.ivCloseComment.setOnClickListener {
        bottomSheetDialogComment.dismiss()
    }

    bottomSheetDialogComment.setOnDismissListener {
        if (isUpdateComment) {
            isUpdateComment = false
            val dashboardBean = feedList[position]
            dashboardBean.comments = commentCount
            feedAdapter.notifyItemChanged(position)
        }
    }

    bottomSheetDialogComment.show()
}
*/
    /* private fun addCommentsTextChangeListener() {
         mentionArrayAdapter.clear()
         dashboardViewModel.getMentionList("")
         var strMention = ""
         bottomSheetDialogComment.edtComment.showSoftInputOnFocus
         bottomSheetDialogComment.edtComment.mentionColor =
             ContextCompat.getColor(this, R.color.colorBlack)
         bottomSheetDialogComment.edtComment.mentionAdapter = mentionArrayAdapter
         bottomSheetDialogComment.edtComment.setMentionTextChangedListener { view, text ->
             Log.e("mention", text.toString())
             mentionArrayAdapter.notifyDataSetChanged()
             strMention = text.toString()
             if (text.length == 1) {
                 bottomSheetDialogComment.lvMentionList.visibility = View.GONE
             } else bottomSheetDialogComment.lvMentionList.visibility = View.VISIBLE

         }

         bottomSheetDialogComment.lvMentionList.adapter = mentionArrayAdapter

         bottomSheetDialogComment.lvMentionList.setOnItemClickListener { _, _, i, l ->
             val strName = bottomSheetDialogComment.edtComment.text.toString().replace(
                 strMention, "${
                     mentionArrayAdapter.getItem(
                         i
                     ).toString()
                 }"
             )
             bottomSheetDialogComment.edtComment.setText(strName)
             bottomSheetDialogComment.edtComment.setSelection(bottomSheetDialogComment.edtComment.text!!.length)
             bottomSheetDialogComment.lvMentionList.visibility = View.GONE


         }
     }*/

    fun onClickCategory(view: View) {

        bottomSheetCategoryDialog =
            BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetCategoryDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_category,
                null
            )
        )
        bottomSheetCategoryDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetCategoryDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetCategoryDialog.setCancelable(true)

        dashboardViewModel.getCommentList(postId)

        val horizontalLayout = LinearLayoutManager(
            this@DashboardActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        categoryAdapter = CategoryAdapter(this.categoryBeanList, this, this)

        bottomSheetCategoryDialog.rvCategory.layoutManager = horizontalLayout
        bottomSheetCategoryDialog.rvCategory.adapter = categoryAdapter


        bottomSheetCategoryDialog.ivBack.setOnClickListener {
            bottomSheetCategoryDialog.rvCategory.visibility = View.VISIBLE
            bottomSheetCategoryDialog.rvSubCategory.visibility = View.GONE
            bottomSheetCategoryDialog.clToolbar.visibility = View.GONE
            bottomSheetCategoryDialog.tvFollowing.visibility = View.VISIBLE
        }

        bottomSheetCategoryDialog.tvFollowing.setOnClickListener {
            feedList.clear()
            videoIdList.clear()
            currentPage = 1
            totalCount = 1
            getFeedListApi(0, true)
            firstTime = true
            bottomSheetCategoryDialog.dismiss()
        }

        bottomSheetCategoryDialog.rvSubCategory.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                20,
                false
            )
        )
        bottomSheetCategoryDialog.show()

    }

    override fun onUserProfileClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, dashboardBean.user_id)
            openActivity(intent)
        }
    }

    override fun onFeedBoost(userId: Long) {
//        if (sessionManager.isGuestUser()) {
//            addFragment(
//                SignUpFragment.getInstance(
//                    true
//                ),
//                Constants.SIGNUP_FRAGMENT
//            )
//        } else {
        if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            showBoostFeatureNotAdded()
//                =============== This feature currently not added ============
//                if (sessionManager.getBooleanValue(Constants.KEY_IS_BOOST_ACTIVE)) {
//                    val currentTime = System.currentTimeMillis()
//                    var storedTime = sessionManager.getLongValue(Constants.KEY_BOOST_STAR_TIME)
//                    Log.e("DashboardActivity", "currentTime: $currentTime")
//                    Log.e("DashboardActivity", "storedTime: $storedTime")
//                    storedTime += TimeUnit.MINUTES.toMillis(30)
//                    timer = storedTime - currentTime
//
//                    showBoostPendingDialog(timer)
//
//                } else {
//                    if (sessionManager.getIntegerValue(Constants.KEY_NO_OF_BOOST) > 0) {
//                        showBoostStartConfirmationDialog()
//                    } else {
//                        val intent = Intent(this@DashboardActivity, ProfileActivity::class.java)
//                        intent.putExtra("fromBuyBoost", true)
//                        openActivity(intent)
//                    }
//                }
//                =============== This feature currently not added ============

        } else {
            completeSignUpDialog()
        }
//        }
    }

    private fun showBoostFeatureNotAdded() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DashboardActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_boost_not_available, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        if (!(this@DashboardActivity as Activity).isFinishing) {
            alertDialog.show()
        }

        view.btnAlertOk.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun showBoostStartConfirmationDialog() {
        object : CustomCommonAlertDialog(
            this@DashboardActivity,
            sessionManager.getIntegerValue(Constants.KEY_NO_OF_BOOST).toString().plus(" ")
                .plus(getString(R.string.msg_boost_left)),
            getString(R.string.msg_boost_start),
            sessionManager.getStringValue(Constants.KEY_PROFILE_URL),
            getString(R.string.use_boost),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        sessionManager.setBooleanValue(true, Constants.KEY_IS_BOOST_ACTIVE)
                        sessionManager.setLongValue(
                            System.currentTimeMillis(),
                            Constants.KEY_BOOST_STAR_TIME
                        )
                        dashboardViewModel.boostUse()
                    }
                }
            }
        }.show()
    }

    override fun onDescriptionClick(userName: String) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USERNAME, userName)
            openActivity(intent)

        }
    }

    override fun onClickSocialText(userName: String) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USERNAME, userName)
            openActivity(intent)

        }
    }

    private fun showBoostSuccessDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DashboardActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_boost_success, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        if (!(this@DashboardActivity as Activity).isFinishing) {
            alertDialog.show()
        }

        view.btnAlertOk.setOnClickListener {
            alertDialog.dismiss()
            showBoostStartConfirmationDialog()
        }

        view.tvNoThanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showBoostPendingDialog(myTimer: Long) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DashboardActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_boost_time_pending, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        if (!(this@DashboardActivity as Activity).isFinishing()) {
            alertDialog.show()
        }

        val interval = 1000L
        Log.e("DashboardActivity", "myTimer: $myTimer")

        if (myTimer.toString().contains("-")) {
            alertDialog.dismiss()
        }
        val t: CountDownTimer
        t = object : CountDownTimer(myTimer, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val time = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millisUntilFinished
                        )
                    ), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished
                        )
                    )
                )
//                Log.e("DashboardActivity", "Time: $time")

                view.tvTimeRemaining.text =
                    time.plus(" ").plus(resources.getString(R.string.remaining))
            }

            override fun onFinish() {
                alertDialog.dismiss()
                sessionManager.setBooleanValue(false, Constants.KEY_IS_BOOST_ACTIVE)
                feedAdapter.notifyDataSetChanged()
                showBoostSuccessDialog()
                cancel()
            }
        }.start()


        view.btnAlertOk.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
    }

    override fun onSelectItemClick(
        userId: Long,
        position: Int,
        userProfileType: String
    ) {
        Log.e(
            "DashboardActivity",
            "onSelectItemClick: \t userProfileType: $userProfileType"
        )
        if (userProfileType == "1" && sessionManager.isGuestUser()) {
            bottomSheetDialogComment.dismiss()
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, userId)
            openActivity(intent)
        }
    }

    private fun getDataFromIntent(intent: Intent) {

        if (intent.hasExtra(Constants.ACTION_ACTION_TYPE) && intent.getStringExtra(Constants.ACTION_ACTION_TYPE) == "notification") {
            if (intent.hasExtra(Constants.NOTIFICATION_TYPE) && intent.getStringExtra(Constants.NOTIFICATION_TYPE) == Constants.NOTIFICATION_PENDING_INTENT) {
                sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            }
            if (intent.hasExtra(Constants.KEY_NOTIFICATION)) {
                notification = intent.getParcelableExtra(Constants.KEY_NOTIFICATION)!!
                when (notification.isNotificationType) {
                    "0" -> { // for Comment Notification
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onClickMatches", false)
                        openActivity(intentMatchesActivity)
                    }
                    "1" -> { // for mention in comment Notification
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onClickMatches", false)
                        openActivity(intentMatchesActivity)
                    }
                    "2" -> { // for follow Notification

                        val profileBean: ProfileBean = ProfileBean()
                        profileBean.user_id = sessionManager.getUserId()
                        profileBean.username =
                            sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                        profileBean.profileUrl =
                            sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
                        profileBean.gender = sessionManager.getStringValue(Constants.GENDER)

                        val intentProfileActivity =
                            Intent(this@DashboardActivity, ProfileActivity::class.java)
//                        intentProfileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intentProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intentProfileActivity.putExtra(Constants.PROFILE_BEAN, profileBean)
                        intentProfileActivity.putExtra("isMyProfile", true)
                        startActivity(intentProfileActivity)
                        // addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "3" -> { // for new video Notification
                        val intentDashboardActivity =
                            Intent(this@DashboardActivity, DashboardActivity::class.java)
                        intentDashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentDashboardActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intentDashboardActivity)
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "4" -> { // for Matches Notification
                        // addFragment(MatchesProfileFragment(), Constants.NOTIFICATION_FRAGMENT)
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onClickMatches", true)
                        openActivity(intentMatchesActivity)
                    }
                    "5" -> { // for mention in post Notification
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onClickMatches", false)
                        openActivity(intentMatchesActivity)
                    }
                    "6" -> { // for follow request Notification
                        //addFragment(FollowRequestFragment(), Constants.FOLLOW_REQUEST_FRAGMENT)
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onFollowRequest", true)
                        openActivity(intentMatchesActivity)
                    }
                    "7" -> { // For chat notification
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("isFromMessage", true)
                        intentMatchesActivity.putExtra("chatNotification", true)
                        Log.d("Chat notification :", "7 pass")
                        val matchesListBean =
                            intent.getParcelableExtra<MatchesListBean>("matchesListBean")
                        intentMatchesActivity.putExtra("matchesListBean", matchesListBean)
                        openActivity(intentMatchesActivity)
                    }
                    else -> { // Default
                        val intentMatchesActivity =
                            Intent(this@DashboardActivity, MatchesActivity::class.java)
                        intentMatchesActivity.putExtra("onClickMatches", false)
                        openActivity(intentMatchesActivity)
                    }
                }
            }
        }
    }

    private val notificationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            if (getCurrentFragment() is NotificationFragment && getFragmentRefreshListener() != null && intent!!.hasExtra(
                    Constants.KEY_NOTIFICATION
                )
            ) {
                getFragmentRefreshListener()!!.onRefresh(intent.getParcelableExtra(Constants.KEY_NOTIFICATION))
            } else {
                if (intent!!.hasExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE) && intent.hasExtra(
                        MyFirebaseMessagingService.KEY_NOTI_MESSAGE
                    )
                ) {
                    showNotification(
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE),
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_MESSAGE)
                    )
                }
            }
        }
    }

    private val myBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val bundle = intent.extras
            //timer = bundle!!.getString("timerCount")!!
//            timer = bundle!!.getLong("timerCount")
            Log.e("MyBroadcastReceiver", "timer: $timer")


            /*val dashboardIntent = Intent(context, DashboardActivity::class.java)
            dashboardIntent.putExtra("timer", timer)
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // adding this flag starts the new Activity in a new Task
            context!!.startActivity(dashboardIntent)*/
        }
    }

    override fun onResume() {
        super.onResume()
        //Log.e("DashboardActivity", "onResume")
        if (NamasteyApplication.instance.isUpdateProfile() || isFromSetting) {
            isFromSetting = false
            feedList.clear()
            currentPage = 1
            videoIdList.clear()
            totalCount = 1
//            getFeedListApi(0)
            Handler(Looper.getMainLooper()).postDelayed({
                setupPermissions()
            }, 2000)

        } else {
//            if (::videoAutoPlayHelper.isInitialized)
//                videoAutoPlayHelper.reset()
            mRecyclerView!!.onRestartPlayer()
        }
        registerReceiver(
            notificationBroadcast,
            IntentFilter(MyFirebaseMessagingService.NOTIFICATION_ACTION)
        )

        LocalBroadcastManager.getInstance(this@DashboardActivity).registerReceiver(
            myBroadcastReceiver, IntentFilter("countDown")
        )
    }

    override fun onPause() {
        super.onPause()
        if (mIntent != null) {
            unregisterReceiver(myBroadcastReceiver)
            mIntent = null
        }
//        if (::videoAutoPlayHelper.isInitialized)
//            videoAutoPlayHelper.removePlayer()
        mRecyclerView!!.onPausePlayer()
    }

    override fun onStop() {
        super.onStop()
        mRecyclerView!!.onPausePlayer()
//        if (::videoAutoPlayHelper.isInitialized)
//            videoAutoPlayHelper.removePlayer()

    }

//    override fun onRestart() {
//        super.onRestart()
//        mRecyclerView!!.onRestartPlayer()
//    }

    private fun addFragmentWithoutCurrentFrag(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment).commitAllowingStateLoss()
    }

    fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.flContainer)
    }

    fun getFragmentRefreshListener(): FragmentRefreshListener? {
        return fragmentRefreshListener
    }

    fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener
    }

    override fun onPostViewer(postId: Long) {
        dashboardViewModel.postView(postId)
    }

    override fun onSuccessGetComment(data: ArrayList<CommentBean>) {
        bottomSheetDialogComment.rvMentionList.visibility = View.GONE
        bottomSheetDialogComment.tvTotalComment.text =
            data.size.toString().plus(" ").plus(getString(R.string.comments))

        bottomSheetDialogComment.rvPostComment.addItemDecoration(
            DividerItemDecoration(
                this@DashboardActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        commentAdapter = CommentAdapter(data, this@DashboardActivity, this, this)
        bottomSheetDialogComment.rvPostComment.adapter = commentAdapter

        val params: ViewGroup.LayoutParams =
            bottomSheetDialogComment.rvPostComment.layoutParams

        if (data.size > 6) {
            params.height = 1000
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (data[viewHolder.adapterPosition].user_id != sessionManager.getUserId()) return 0
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    swipeDirection: Int
                ) {

                    if (sessionManager.getUserId() == data[viewHolder.adapterPosition].user_id) {
                        dashboardViewModel.deleteComment(data[viewHolder.adapterPosition].id)
                        data.removeAt(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRangeChanged(
                            viewHolder.adapterPosition,
                            commentAdapter.itemCount
                        )
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(bottomSheetDialogComment.rvPostComment)

    }

    override fun onSuccess(msg: String) {
        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

        isUpdateComment = true
        commentCount = commentAdapter.itemCount
    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)
        bottomSheetDialogComment.rvPostComment.scrollToPosition(commentAdapter.itemCount - 1)

        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
        commentCount = commentAdapter.itemCount
        isUpdateComment = true
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
        val feedItems = feedList[position]
        feedItems.is_match = dashboardBean.is_match
        feedItems.is_like = dashboardBean.is_like

        /*if (dashboardBean.is_like == 1) {
            animationLike.visibility = View.VISIBLE
            Handler().postDelayed({ animationLike.visibility = View.GONE }, 2000)
        }*/

        if (feedItems.is_match == 1 && feedItems.is_like == 1) {
            val intent = Intent(this@DashboardActivity, MatchesScreenActivity::class.java)
            intent.putExtra("username", feedItems.username)
            intent.putExtra("profile_url", feedItems.profile_url)
            openActivity(intent)
        }

        feedList[position] = feedItems
        feedAdapter.notifyItemChanged(position)

//        Handler(Looper.getMainLooper()).postDelayed({
        if (position < feedList.size) {
//            // viewpagerFeed.currentItem = position + 1
            mRecyclerView!!.layoutManager!!.scrollToPosition(position + 1)
            val nextPosition = position + 1
            mLayoutManager!!.scrollToPositionWithOffset(nextPosition, 0)
            mRecyclerView!!.playVideo(false, false, nextPosition)
        }
//        }, 1000)

        // Handler().postDelayed({ mbtn.setEnabled(true) }, 2000)
    }

    override fun onSuccessFollow(msg: String) {
        val dashboardBean = feedList[position]
        if (dashboardBean.is_follow == 1)
            dashboardBean.is_follow = 0
        else
            dashboardBean.is_follow = 1

        feedList[position] = dashboardBean
        feedAdapter.notifyItemChanged(position)
    }

    override fun onSuccessReport(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessSavePost(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessMention(mentionList: ArrayList<MentionListBean>) {
        if (mentionList.size > 0) {
            bottomSheetDialogComment.rvMentionList.addItemDecoration(
                DividerItemDecoration(
                    this@DashboardActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            bottomSheetDialogComment.rvMentionList.visibility = View.GONE

            for (i in mentionList.indices) {
                mentionArrayAdapter.addAll(
                    if (mentionList[i].profile_url != "") Mention(
                        mentionList[i].username,
                        "",
                        mentionList[i].profile_url
                    )
                    else Mention(mentionList[i].username)
                )
            }

        } else {
            bottomSheetDialogComment.rvMentionList.visibility = View.GONE
        }
    }

    override fun onFailedMaxLike(msg: String, error: Int) {
        Log.e("DashboardActivity", "onFailedMaxLike: msg:\t  $msg \t error:\t  $error")
        sessionManager.setBooleanValue(true, Constants.KEY_MAX_USER_LIKE)
        showMembershipDialog(1)
    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
        this.membershipViewList = membershipView
    }

    override fun onSuccessPurchaseStatus(purchaseBean: PurchaseBean) {
        sessionManager.setIntegerValue(purchaseBean.is_purchase, Constants.KEY_IS_PURCHASE)
        sessionManager.setLongValue(purchaseBean.purchase_date, Constants.KEY_PURCHASE_DATE)
        sessionManager.setIntegerValue(
            purchaseBean.number_of_boost_available,
            Constants.KEY_NO_OF_BOOST
        )
    }

    override fun onSuccessBoostUse(boostBean: BoostBean) {
        sessionManager.setIntegerValue(
            boostBean.number_of_boost_available,
            Constants.KEY_NO_OF_BOOST
        )
        feedAdapter.notifyDataSetChanged()
    }

    override fun onSuccessStartChat(msg: String) {
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
        followingList = list
        if (followingList.size != 0) {
            userShareAdapter =
                UserShareAdapter(followingList, this, false, this, this)
            bottomSheetDialogShareApp.rvProfileShare.adapter = userShareAdapter
        }
    }

    override fun onSuccessPostShare(msg: String) {
        Log.e("DashboardActivity", "onSuccessPostShare: msg:\t  $msg")
        feedList[position].share = feedList[position].share + 1
        feedAdapter.notifyDataSetChanged()
    }

    override fun onSuccessBlockUser(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                feedList.clear()
                videoIdList.clear()
                currentPage = 1
                totalCount = 1
                /*dashboardViewModel.getNewFeedListV2(
                    currentPage,
                    0,
                    latitude,
                    longitude,
                    videoIdList
                )*/

                getFeedListApi(0, false)

                //dashboardViewModel.getNewFeedList(currentPage, 0, latitude, longitude)
            }
        }.show()
    }

    override fun onMentionItemClick(userId: Long, position: Int, username: String) {
        bottomSheetDialogComment.edtComment.setText(username)
        bottomSheetDialogComment.rvMentionList.visibility = View.GONE
    }

//    private fun getLocation() {
//        appLocationService = AppLocationService(this)
//
//        val gpsLocation: Location? = appLocationService.getLocation(LocationManager.GPS_PROVIDER)
//        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        var gps_enabled = false
//        var network_enabled = false
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//        } catch (ex: java.lang.Exception) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            // notify user
//            displayGPSDialog()
//        }
//        if (gpsLocation != null) {
//
//            if (currentLocationFromDB != null) {
//                latitude = currentLocationFromDB!!.latitude
//                longitude = currentLocationFromDB!!.longitude
//            } else {
//                latitude = gpsLocation.latitude
//                longitude = gpsLocation.longitude
//            }
//            getFeedListApi(0)
//            Log.e("DashboardActivity", "latitude: $latitude")
//            Log.e("DashboardActivity", "longitude: $longitude")
//        } else {
////            turnGPSOn()
//            displayGPSDialog()
//            //showSettingsAlert()
//        }
//    }

    private fun displayGPSDialog() {
        object : CustomAlertDialog(
            this@DashboardActivity,
            resources.getString(R.string.gps_disable_message),
            getString(R.string.go_to_settings),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        isFromSetting = true;
                        val intent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()

    }

    //    private fun turnGPSOn() {
//        val provider =
//            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
//        Log.e("Dashboard", "provider: $provider")
//        if (!provider.contains("gps")) { //if gps is disabled
//            val poke = Intent()
//            poke.setClassName(
//                "com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider"
//            )
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
//            poke.data = Uri.parse("3")
//            this.sendBroadcast(poke)
//        }
//    }
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    if (currentLocationFromDB != null) {
                        latitude = currentLocationFromDB!!.latitude
                        longitude = currentLocationFromDB!!.longitude
                    } else {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                    getFeedListApi(0, false)
                    // use your location object
                    // get latitude , longitude and other info from this
                } else {
                    displayGPSDialog()
                }

            }

    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        Log.e("DashboardActivity", "latitude: $latitude")
        Log.e("DashboardActivity", "longitude: $longitude")

        if (feedList.size == 0) {
            getFeedListApi(0, false)
        }
    }

    override fun onFailed(msg: String, error: Int, status: Int) {
        super.onFailed(msg, error, status)
        Log.e("DashboardActivity", "onFailed  error: $error")
        Log.e("DashboardActivity", "onFailed  msg: $msg")
    }

    /*CategoryAdapter Item Click*/
    override fun onItemClickCategoty(categoryBean: CategoryBean) {

        bottomSheetCategoryDialog.rvCategory.visibility = View.GONE
        bottomSheetCategoryDialog.rvSubCategory.visibility = View.VISIBLE
        bottomSheetCategoryDialog.clToolbar.visibility = View.VISIBLE
        bottomSheetCategoryDialog.tvFollowing.visibility = View.GONE


        val subCategoryAdapter = SubCategoryAdapter(
            categoryBean,
            this, this
        )
        bottomSheetCategoryDialog.tvTypeName.text = categoryBean.name
        bottomSheetCategoryDialog.rvSubCategory.adapter = subCategoryAdapter
    }

    /*SubCategoryAdapter Item Click*/
    override fun onItemClick(subCategoryId: Int) {
        Log.e("Subcategory : ", subCategoryId.toString())
        bottomSheetCategoryDialog.dismiss()
        if (sessionManager.isGuestUser()) {
            this.addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            feedList.clear()
            currentPage = 1
            totalCount = 1
            videoIdList.clear()
            firstTime = true
            getFeedListApi(subCategoryId, false)

        }
    }

    override fun onItemClick(value: Long, position: Int) {
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {
    }

}