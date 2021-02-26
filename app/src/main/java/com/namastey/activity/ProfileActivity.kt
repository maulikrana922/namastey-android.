package com.namastey.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.SliderAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileBinding
import com.namastey.fragment.SignUpFragment
import com.namastey.model.BoostPriceBean
import com.namastey.model.DashboardBean
import com.namastey.model.MembershipBean
import com.namastey.model.ProfileBean
import com.namastey.receivers.BoostService
import com.namastey.uiView.ProfileView
import com.namastey.utils.*
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_album_detail.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_boost_success.view.*
import kotlinx.android.synthetic.main.dialog_boosts.view.*
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProfileActivity : BaseActivity<ActivityProfileBinding>(), ProfileView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val REQUEST_CODE = 101
    private var profileFile: File? = null
    private var profileBean = ProfileBean()
    private var isCompletlySignup = 0
    private var membershipList = ArrayList<MembershipBean>()
    private var isCameraOpen = false
    private val PERMISSION_REQUEST_CODE = 99
    private var boostProfileList = ArrayList<BoostPriceBean>()
    private var fromBuyBoost = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        activityProfileBinding = bindViewData()
        activityProfileBinding.viewModel = profileViewModel

        initData()
    }

    private fun initData() {

//        profileViewModel.getUserDetails()
        if (sessionManager.getUserGender() == Constants.Gender.female.name) {
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileUser)
        } else {
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileUser)
        }
        // Temp set UI
        setMembershipList()
        profileViewModel.getBoostPriceList()

        if (intent.hasExtra("fromBuyBoost")) {
            fromBuyBoost = intent.getBooleanExtra("fromBuyBoost", false)
            Log.e("ProfileActivity", "fromBuyBoost: \t $fromBuyBoost")
        }
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile
    // var timer = ""

    override fun getBindingVariable() = BR.viewModel

    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    /**
     * click on followers and following
     */
    fun onClickFollow(view: View) {
        if (!sessionManager.isGuestUser() && profileBean.is_completly_signup == 1) {
            val intent = Intent(this@ProfileActivity, FollowingFollowersActivity::class.java)
            intent.putExtra(Constants.PROFILE_BEAN, profileBean)
            intent.putExtra("isMyProfile", true)
            openActivity(intent)
        }
    }

    fun onClickProfile(view: View) {
        when (view) {
            ivProfileCamera -> {
                selectImage()
            }
        }
    }

    fun onClickSign(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    false
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            openActivity(this@ProfileActivity, ProfileBasicInfoActivity())
        }
    }

    fun onClickMembership(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    false
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                completeSignUpDialog()
            } else {
                openActivity(this@ProfileActivity, MembershipActivity())
            }
            // startActivity(Intent(this@ProfileActivity, MembershipActivity::class.java))
        }
    }

    fun onClickBoostMe(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    false
                ),
                Constants.SIGNUP_FRAGMENT
            )
            //showBoostDialog(R.layout.dialog_boost_skipline)
        } else {
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                completeSignUpDialog()
            } else {
                if (boostProfileList.size != 0)
                    showBoostDialog(R.layout.dialog_boosts)
            }
        }
    }

    /**
     * click on Edit info open edit profile activity
     */
    fun onClickEditProfile(view: View) {
        openActivity(this@ProfileActivity, EditProfileActivity())
    }

    /**
     * click on album open edit activity with album list
     */
    fun onClickAlbums(view: View) {
        val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
        intent.putExtra("onClickAlbum", true)
        openActivity(intent)
    }

    fun onClickViewProfile(view: View) {
        if (isCompletlySignup != 0) {
            val intent = Intent(this@ProfileActivity, ProfileViewActivity::class.java)
            intent.putExtra("profileBean", profileBean)
            openActivity(intent)
        }
    }

    fun onClickProfileMore(view: View) {
        if (!sessionManager.isGuestUser() && sessionManager.getUserId() == profileBean.user_id && profileBean.is_completly_signup == 1) {
            openActivity(this@ProfileActivity, SettingsActivity())
        } else {
            createPopUpMenu()

        }
    }

    fun onClickPassport(view: View) {
        if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1)
            addLocationPermission()
        else {
            // openActivity(this@ProfileActivity, InAppPurchaseActivity())
            val intent = Intent(this@ProfileActivity, MembershipActivity::class.java)
            intent.putExtra("isFromAirport", true)
            openActivity(intent)
        }
    }


    private fun createPopUpMenu() {
        val popupMenu = PopupMenu(this, ivProfileMore)
        popupMenu.menuInflater.inflate(R.menu.menu_logout, popupMenu.menu)
        val menuLogout = popupMenu.menu.findItem(R.id.action_logout)


        try {
            val fields: Array<Field> = PopupMenu::class.java.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    object : CustomAlertDialog(
                        this@ProfileActivity,
                        resources.getString(R.string.msg_logout),
                        getString(R.string.logout),
                        getString(R.string.cancel)
                    ) {
                        override fun onBtnClick(id: Int) {
                            when (id) {
                                btnPos.id -> {
                                    profileViewModel.logOut()
                                }
                                btnNeg.id -> {
                                    dismiss()
                                }
                            }
                        }
                    }.show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun setMembershipList() {
        val membershipBean1 = MembershipBean()
        membershipBean1.name = resources.getString(R.string._1_boost_each_month)
        membershipBean1.description = getString(R.string.skip_the_line_to_get_more_matches)
        membershipList.add(membershipBean1)

        val membershipBean2 = MembershipBean()
        membershipBean2.name = resources.getString(R.string.out_of_likes)
        membershipBean2.description = getString(R.string.do_not_want_to_wait_slider)
        membershipList.add(membershipBean2)

        val membershipBean3 = MembershipBean()
        membershipBean3.name = resources.getString(R.string.swipe_around_the_world)
        membershipBean3.description = getString(R.string.passport_to_anywhere)
        membershipList.add(membershipBean3)

        val membershipBean4 = MembershipBean()
        membershipBean4.name = resources.getString(R.string._5_free_super_message)
        membershipBean4.description = getString(R.string.your_3x_more_likes)
        membershipList.add(membershipBean4)

        val membershipBean5 = MembershipBean()
        membershipBean5.name = resources.getString(R.string.see_who_like_you)
        membershipBean5.description = getString(R.string.month_with_them_instantly)
        membershipList.add(membershipBean5)

        viewpagerMembership.adapter = SliderAdapter(this@ProfileActivity, membershipList)
        indicator.setupWithViewPager(viewpagerMembership, true)

        val timer = Timer()
        timer.scheduleAtFixedRate(SliderTimer(), 4000, 6000)

    }

    inner class SliderTimer : TimerTask() {
        override fun run() {
            this@ProfileActivity.runOnUiThread(Runnable {
                if (viewpagerMembership.currentItem < membershipList.size - 1) {
                    viewpagerMembership.currentItem = viewpagerMembership.currentItem + 1
                } else {
                    viewpagerMembership.currentItem = 0
                }
            })
        }
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

    /**
     * show dialog of boost
     */
    private fun showBoostDialog(layout: Int) {

        var isFromProfile = true

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View = LayoutInflater.from(this).inflate(layout, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        view.llRecurringTextView.visibility = View.GONE
        manageVisibility(view)
        view.btnBoost.setOnClickListener {
            Handler().postDelayed(Runnable {
                sessionManager.setBooleanValue(false, Constants.KEY_BOOST_ME)
                //sessionManager.setLongValue(System.currentTimeMillis(), Constants.KEY_BOOST_STAR_TIME)
                startBoostService()
            }, 1800000)
            sessionManager.setLongValue(System.currentTimeMillis(), Constants.KEY_BOOST_STAR_TIME)
            sessionManager.setBooleanValue(true, Constants.KEY_BOOST_ME)
            //startBoostService()
            // showBoostSuccessDialog()
            //startBoostService()
            alertDialog.dismiss()

            val intent = Intent(this@ProfileActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("isFromProfile", isFromProfile)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        view.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }    // var timer = ""

    }

    private fun startBoostService() {
        val calendar = Calendar.getInstance()
        // calendar[Calendar.HOUR_OF_DAY] = 23
        // calendar[Calendar.MINUTE] = 29
        calendar[Calendar.MINUTE] = 29
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 59
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, BoostService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )
    }

    /**
     * manage visibility of boost dialog*/
    private fun manageVisibility(view: View) {
        for (data in boostProfileList) {
            val numberOfBoost = data.number_of_boost
            val price = data.price

            Log.e("ProfileActivity", "numberOfBoost: \t $numberOfBoost")
            Log.e("ProfileActivity", "price: \t $price")

            if (numberOfBoost == 1) {
                // view.tvOnemonth.text = numberOfBoost.toString()
                view.tvTextLowEachBoost.text =
                    resources.getString(R.string.dollars) + price + resources.getString(R.string.each)
            }

            if (numberOfBoost == 5) {
                // view.tvFivemonth.text = numberOfBoost.toString()
                view.tvTextMediumEachBoost.text =
                    resources.getString(R.string.dollars) + price + resources.getString(R.string.each)
            }

            if (numberOfBoost == 10) {
                // view.tvTwel.text = numberOfBoost.toString()
                view.tvTextHighEachBoost.text =
                    resources.getString(R.string.dollars) + price + resources.getString(R.string.each)
            }
        }

        view.constLow.setOnClickListener {
            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextLowEachBoost.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.viewBgLow.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            //  view.tvOfferLow.visibility = View.VISIBLE
            view.viewSelectedLow.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )

            val intent = Intent(this@ProfileActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.IN_APP_PRODUCT_ID, "b00100")
            openActivity(intent)
            //openActivity(this@ProfileActivity, InAppPurchaseActivity())
        }

        view.constMedium.setOnClickListener {
            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextBoostMedium.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextMediumEachBoost.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.viewBgMedium.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvOfferMedium.visibility = View.VISIBLE
            view.viewSelectedMedium.visibility = View.VISIBLE

            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )

            val intent = Intent(this@ProfileActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.IN_APP_PRODUCT_ID, "b00200")
            openActivity(intent)
        }

        view.constHigh.setOnClickListener {
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.tvTextHighEachBoost.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            view.viewBgHigh.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvOfferHigh.visibility = View.VISIBLE
            view.viewSelectedHigh.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )

            val intent = Intent(this@ProfileActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.IN_APP_PRODUCT_ID, "b00300")
            openActivity(intent)
        }
    }

    /**
     * show dialog of boost success
     */
    private fun showBoostSuccessDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_boost_success, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        view.btnAlertOk.setOnClickListener {
            alertDialog.dismiss()
        }

        view.tvNoThanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun selectImage() {

        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.select_photo)
        )


        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(
            getString(R.string.upload_profile_pictuare)
        )
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> isCameraPermissionGranted()
                1 -> isReadWritePermissionGranted()
            }
        }
        builder.show()
    }

    private fun isReadWritePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryForImage()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Constants.PERMISSION_STORAGE
                )
            }
        } else {
            openGalleryForImage()
        }
    }

    private fun openGalleryForImage() {

        profileFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@ProfileActivity)
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA_IMAGE)
            } catch (ex: Exception) {
                showMsg(ex.localizedMessage)
            }
        }
    }

    private fun isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                capturePhoto()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PERMISSION_CAMERA
                )
            }
        } else {
            capturePhoto()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        profileViewModel.getUserDetails()
    }

    override fun onBackPressed() {
        val signUpFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_FRAGMENT)
        val signupWithPhoneFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_WITH_PHONE_FRAGMENT)
        val otpFragment =
            supportFragmentManager.findFragmentByTag(Constants.OTP_FRAGMENT)

        if (signupWithPhoneFragment != null) {
            val childFm = signupWithPhoneFragment.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack()
            } else {
                supportFragmentManager.popBackStack()
            }
        } else if (signUpFragment != null || otpFragment != null)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.getStringValue(Constants.KEY_CASUAL_NAME).isNotEmpty()) {
            tvProfileUsername.text = sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
            if (sessionManager.getStringValue(Constants.KEY_TAGLINE).isNotEmpty())
                tvAbouteDesc.text = sessionManager.getStringValue(Constants.KEY_TAGLINE)
        }
        if (!isCameraOpen)
            profileViewModel.getUserDetails()
        else
            isCameraOpen = false

    }

    override fun onDestroy() {
        profileViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
//            ivProfileUser.setImageURI(data?.data) // handle chosen image

            if (data != null) {
                val selectedImage = data.data

                if (selectedImage != null) {
//                    val inputStream: InputStream?
                    try {
//                        val selectedImage = data.data
                        val filePathColumn =
                            arrayOf(MediaStore.Images.Media.DATA)
                        val cursor: Cursor? = this@ProfileActivity.contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        cursor!!.moveToFirst()

                        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cursor.getString(columnIndex)
                        cursor.close()

                        GlideLib.loadImage(this, ivProfileUser, picturePath)
                        Log.d("Image Path", "Image Path  is $picturePath")
                        profileFile = Utils.saveBitmapToFile(File(picturePath))

                        if (profileFile != null && profileFile!!.exists()) {
                            isCameraOpen = true
                            profileViewModel.updateProfilePic(profileFile!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_CAMERA_IMAGE) {
//            if (data != null) {
            val imageFile = Utils.getCameraFile(this@ProfileActivity)
            val photoUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                imageFile
            )
            val bitmap: Bitmap = Utils.scaleBitmapDown(
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
                1200
            )!!

            GlideLib.loadImageBitmap(this, ivProfileUser, bitmap)

            if (imageFile.exists()) {
                isCameraOpen = true
                profileViewModel.updateProfilePic(imageFile)
            }
//            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {

            Constants.PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    openGalleryForImage()
                } else {
                    if (grantResults.isNotEmpty()) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                            // user rejected the permission
                            val permission: String =
                                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                                    permissions[0]
                                } else {
                                    permissions[1]
                                }
                            val showRationale = shouldShowRequestPermissionRationale(permission)
                            if (!showRationale) {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(getString(R.string.permission_denied_storage_message))
                                    .setTitle(getString(R.string.permission_required))

                                builder.setPositiveButton(
                                    getString(R.string.go_to_settings)
                                ) { dialog, id ->
                                    var intent = Intent(
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
                            } else {
                                // user also UNCHECKED "never ask again"
                                isReadWritePermissionGranted()
                            }
                        } else {
                            isReadWritePermissionGranted()
                        }
                    }
                }
            }
            Constants.PERMISSION_CAMERA -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                capturePhoto()
            } else {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        val permission: String =
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                                permissions[0]
                            } else {
                                permissions[1]
                            }
                        val showRationale = shouldShowRequestPermissionRationale(permission)
                        if (!showRationale) {
                            val builder = AlertDialog.Builder(this)
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                                builder.setMessage(getString(R.string.permission_denied_camera_message))

                            if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                                builder.setMessage(getString(R.string.permission_denied_storage_message))
                                    .setTitle(getString(R.string.permission_required))

                            builder.setPositiveButton(
                                getString(R.string.go_to_settings)
                            ) { dialog, id ->
                                var intent = Intent(
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
                        } else {
                            // user also UNCHECKED "never ask again"
                            isCameraPermissionGranted()
                        }
                    } else {
                        isCameraPermissionGranted()
                    }
                }
            }
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("PassportContentActivity", "Permission has been denied by user CAMERA")
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
                            /*ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_CODE
                            )*/

                            addLocationPermission()
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
                            var intent = Intent(
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
                    openActivity(this, PassportContentActivity())
                }
            }
        }
    }

    override fun onSuccessResponse(profileBean: ProfileBean) {
        this.profileBean = profileBean
        isCompletlySignup = profileBean.is_completly_signup
        tvFollowersCount.text = profileBean.followers.toString()
        tvFollowingCount.text = profileBean.following.toString()
        tvViewsCount.text = profileBean.viewers.toString()
        sessionManager.setStringValue(profileBean.username, Constants.KEY_CASUAL_NAME)
        sessionManager.setUserGender(profileBean.gender)
        sessionManager.setUserId(profileBean.user_id)

        //  sessionManager.setIntegerValue(profileBean.is_completly_signup, Constants.KEY_IS_COMPLETE_PROFILE)

        if (profileBean.is_completly_signup == 1) {
            sessionManager.setStringValue(profileBean.profileUrl, Constants.KEY_PROFILE_URL)
            sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
            sessionManager.setStringValue(profileBean.about_me, Constants.KEY_TAGLINE)
            sessionManager.setStringValue(profileBean.distance, Constants.DISTANCE)
            sessionManager.setIntegerValue(profileBean.is_hide, Constants.IS_HIDE)
            sessionManager.setStringValue(profileBean.min_age.toString(), Constants.KEY_AGE_MIN)
            sessionManager.setStringValue(profileBean.max_age.toString(), Constants.KEY_AGE_MAX)
            sessionManager.setInterestIn(profileBean.interest_in_gender)
            sessionManager.setIntegerValue(profileBean.user_profile_type, Constants.PROFILE_TYPE)
            sessionManager.setIntegerValue(profileBean.age, Constants.KEY_AGE)

            sessionManager.setIntegerValue(
                profileBean.notificationBean[0].is_mentions,
                Constants.KEY_IS_MENTIONS
            )
            sessionManager.setIntegerValue(
                profileBean.notificationBean[0].is_matches,
                Constants.KEY_IS_MATCHES
            )
            sessionManager.setIntegerValue(
                profileBean.notificationBean[0].is_follow,
                Constants.KEY_IS_NEW_FOLLOWERS
            )
            sessionManager.setIntegerValue(
                profileBean.notificationBean[0].is_comment,
                Constants.KEY_IS_COMMENTS
            )
            sessionManager.setIntegerValue(
                profileBean.notificationBean[0].is_suggest,
                Constants.KEY_IS_VIDEO_SUGGESTIONS
            )

            /*sessionManager.setIntegerValue(profileBean.safetyBean[0].is_download, Constants.KEY_IS_DOWNLOAD_VIDEO)
            sessionManager.setIntegerValue(profileBean.safetyBean[0].is_followers, Constants.KEY_IS_YOUR_FOLLOWERS)
            sessionManager.setIntegerValue(profileBean.safetyBean[0].is_suggest, Constants.KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS)
            sessionManager.setIntegerValue(profileBean.safetyBean[0].who_can_comment, Constants.KEY_CAN_COMMENT_YOUR_VIDEO)
            sessionManager.setIntegerValue(profileBean.safetyBean[0].who_can_send_message, Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE)
            sessionManager.setIntegerValue(profileBean.safetyBean[0].is_share, Constants.KEY_IS_SHARE_PROFILE_SAFETY)*/

            sessionManager.setIntegerValue(
                profileBean.safetyBean.is_download,
                Constants.KEY_IS_DOWNLOAD_VIDEO
            )
            sessionManager.setIntegerValue(
                profileBean.safetyBean.is_followers,
                Constants.KEY_IS_YOUR_FOLLOWERS
            )
            sessionManager.setIntegerValue(
                profileBean.safetyBean.is_suggest,
                Constants.KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS
            )
            sessionManager.setIntegerValue(
                profileBean.safetyBean.who_can_comment,
                Constants.KEY_CAN_COMMENT_YOUR_VIDEO
            )
            sessionManager.setIntegerValue(
                profileBean.safetyBean.who_can_send_message,
                Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE
            )
            sessionManager.setIntegerValue(
                profileBean.safetyBean.is_share,
                Constants.KEY_IS_SHARE_PROFILE_SAFETY
            )

            sessionManager.setLanguageList(profileBean.languageBean)

            btnProfileSignup.visibility = View.INVISIBLE
            groupButtons.visibility = View.VISIBLE
            ivProfileCamera.visibility = View.VISIBLE
            if (profileBean.profileUrl.isNotBlank()) {
                GlideLib.loadImage(this@ProfileActivity, ivProfileUser, profileBean.profileUrl)
            }
        } else {
            if (sessionManager.isGuestUser()) {
                btnProfileSignup.text = getString(R.string.btn_signup)
            } else {
                //sessionManager.setCompleteSignUp(profileBean.is_completly_signup)
                btnProfileSignup.text = getString(R.string.btn_complete_profile)
            }
            btnProfileSignup.visibility = View.VISIBLE
            groupButtons.visibility = View.GONE
        }
        if (profileBean.username.isNotBlank()) {
            tvProfileUsername.text = profileBean.username
            if (profileBean.about_me.isNotEmpty())
                tvAbouteDesc.text = profileBean.about_me
        }
    }

    override fun onSuccessFollow(profileBean: ProfileBean) {
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
    }

    override fun onSuccessReport(msg: String) {

    }

    override fun onSuccessBlockUser(msg: String) {

    }

    override fun onSuccessSavePost(msg: String) {
    }

    override fun onSuccessBoostPriceList(boostPriceBean: ArrayList<BoostPriceBean>) {
        this.boostProfileList = boostPriceBean
        if (fromBuyBoost) {
            showBoostDialog(R.layout.dialog_boosts)
        }
    }


    override fun onLogoutSuccess(msg: String) {
        sessionManager.logout()
        val intent = Intent(this@ProfileActivity, SignUpActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivity(intent)
    }

    override fun onLogoutFailed(msg: String, error: Int) {
    }
}
