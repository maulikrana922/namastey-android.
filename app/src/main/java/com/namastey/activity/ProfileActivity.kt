package com.namastey.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
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
import com.namastey.model.MembershipBean
import com.namastey.model.ProfileBean
import com.namastey.uiView.ProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
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
    private val REQUEST_CODE_CAMERA = 102
    private var profileFile: File? = null
    private var profileBean = ProfileBean()
    private var isCompletlySignup = 0
    private var membershipList = ArrayList<MembershipBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        activityProfileBinding = bindViewData()
        activityProfileBinding.viewModel = profileViewModel

        initData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        profileViewModel.getUserDetails()

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
    }

    override fun onSuccessResponse(profileBean: ProfileBean) {
        this.profileBean = profileBean
        isCompletlySignup = profileBean.is_completly_signup
        tvFollowersCount.text = profileBean.followers.toString()
        tvFollowingCount.text = profileBean.following.toString()
        sessionManager.setStringValue(profileBean.username, Constants.KEY_CASUAL_NAME)
        sessionManager.setUserGender(profileBean.gender)
        sessionManager.setUserId(profileBean.user_id)

        if (profileBean.is_completly_signup == 1) {
            sessionManager.setStringValue(profileBean.profileUrl, Constants.KEY_PROFILE_URL)
            sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
            sessionManager.setStringValue(profileBean.about_me, Constants.KEY_TAGLINE)
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
                btnProfileSignup.text = getString(R.string.btn_complete_profile)
            }
            btnProfileSignup.visibility = View.VISIBLE
            groupButtons.visibility = View.GONE
        }
        if (profileBean.username.isNotBlank()) {
            tvProfileUsername.text = profileBean.username
            tvAbouteDesc.text = profileBean.about_me
        }
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile

    override fun getBindingVariable() = BR.viewModel

    fun onClickProfileBack(view: View) {
        onBackPressed()
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
                childFm.popBackStack();
            } else {
                supportFragmentManager.popBackStack()
            }
        } else if (signUpFragment != null || otpFragment != null)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    override fun onDestroy() {
        profileViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * click on followers and following
     */
    fun onClickFollow(view: View) {
        if (!sessionManager.isGuestUser()){
            val intent = Intent(this@ProfileActivity,FollowingFollowersActivity::class.java)
            intent.putExtra(Constants.PROFILE_BEAN, profileBean)
            intent.putExtra("isMyProfile",true)
            openActivity(intent)
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
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
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


    fun onClickProfile(view: View) {
        when (view) {
            ivProfileCamera -> {
                selectImage()
            }
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
        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.getStringValue(Constants.KEY_CASUAL_NAME).isNotEmpty()) {
            tvProfileUsername.text = sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
            tvAbouteDesc.text = sessionManager.getStringValue(Constants.KEY_TAGLINE)
        }
        profileViewModel.getUserDetails()

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
                        val selectedImage = data.data
                        val filePathColumn =
                            arrayOf(MediaStore.Images.Media.DATA)
                        val cursor: Cursor? = this@ProfileActivity.contentResolver.query(
                            selectedImage!!,
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
                            profileViewModel.updateProfilePic(profileFile!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            if (data != null) {
                var imageFile = Utils.getCameraFile(this@ProfileActivity)
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    imageFile
                )
                val bitmap: Bitmap = Utils.scaleBitmapDown(
                    MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
                    1200
                )!!
//                GlideLib.loadImageBitmap(this, ivProfileUser, data.extras.get("data") as Bitmap)
                GlideLib.loadImageBitmap(this, ivProfileUser, bitmap)
//                val photo = data.extras["data"] as Bitmap

//                val tempUri = Utils.getImageUri(applicationContext, photo)
//                profileFile = File(Utils.getRealPathFromURI(this@ProfileActivity, photoUri))

                if (imageFile.exists()) {
                    profileViewModel.updateProfilePic(imageFile)
                }
            }
        }
    }

    fun onClickSign(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            openActivity(this@ProfileActivity, ProfileBasicInfoActivity())
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

    /**
     * Temp set for display purpose
     */
    private fun setMembershipList(){
        for (number in 0..5) {
            val membershipBean = MembershipBean()
            membershipBean.name = "Unlimited matches"
            membershipBean.description = "Premium members get unlimited matches ".plus(number + 1)

            membershipList.add(membershipBean)
        }

        viewpagerMembership.adapter = SliderAdapter(this@ProfileActivity,membershipList)
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
}
