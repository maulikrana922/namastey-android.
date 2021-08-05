package com.namastey.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySignUpBinding
import com.namastey.fragment.OTPFragment
import com.namastey.fragment.SelectGenderFragment
import com.namastey.fragment.SignupWithPhoneFragment
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import com.namastey.utils.Constants.RC_SIGN_IN
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.SignUpViewModel
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController
import com.snapchat.kit.sdk.login.models.UserDataResponse
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_signup_with_phone.*
import org.json.JSONException
import java.util.*
import javax.inject.Inject


class SignUpActivity : BaseActivity<ActivitySignUpBinding>(),
    SignUpView,
    LoginStateController.OnLoginStateChangedListener,
    FetchUserDataCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var mLoginStateChangedListener: LoginStateController
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var providerId = ""
    private val PERMISSION_REQUEST_CODE = 101
    private var TAG = "SignUpActivity"
    private var androidId = ""

    override fun getViewModel() = signUpViewModel

    override fun getLayoutId() = R.layout.activity_sign_up

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        activitySignUpBinding = bindViewData()
        activitySignUpBinding.viewModel = signUpViewModel

        initData()
    }

    private fun initData() {
        //constraintMain.setBackground(ContextCompat.getDrawable(this, R.drawable.video_tint))

        sessionManager.logout()
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.signupvideo)
        videoViewSignup.setVideoURI(uri)
        videoViewSignup.start()
        androidId =
            Settings.Secure.getString(this@SignUpActivity.contentResolver, Settings.Secure.ANDROID_ID)

        videoViewSignup.setOnPreparedListener { mp ->
            //Start Playback
            videoViewSignup.start()
            //Loop Video
            mp!!.isLooping = true
        }

        tvTermsConditions.makeLinks(
            Pair(getString(R.string.tv_terms), View.OnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(getString(R.string.tv_term_link))
                startActivity(browserIntent)
            }),
            Pair(getString(R.string.tv_policy), View.OnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(getString(R.string.tv_policy_link))
                startActivity(browserIntent)
            }))

        initializeGoogleApi()

        setupPermissions()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            sessionManager.setFirebaseToken(token)
        })
//        if (sessionManager.getFirebaseToken() == "" || sessionManager.getFirebaseToken() == "null") {
////            val token = FirebaseInstanceId.getInstance().token
//            FirebaseMessaging.getInstance().token.addOnCompleteListener {
//                if (it.isComplete){
//                    val token = it.result.toString()
//                    sessionManager.setFirebaseToken(token)
//                }
//            }
//        }

    }

    private fun initializeGoogleApi() {
        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this)
        FacebookSdk.sdkInitialize(applicationContext)

        //For facebook used initializer
        callbackManager = CallbackManager.Factory.create()

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setupPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarselocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

//        val cameraPermission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        )
//
//        val storagePermission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )

//        if (coarselocationPermission != PackageManager.PERMISSION_GRANTED || locationPermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED ||
//            storagePermission != PackageManager.PERMISSION_GRANTED
//        )
        if (coarselocationPermission != PackageManager.PERMISSION_GRANTED || locationPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to user")
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
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Remove all fragment from stack
     */
    private fun removeAllFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    /**
     * Handle google sign in result
     */
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            providerId = account?.id!!
            if (account.email != null || account.email != "") {
                email = account.email.toString()
            }
            if (account.displayName != null) {
                if (account.displayName.toString() != "" || !TextUtils.isEmpty(
                        account.displayName.toString()
                    )
                ) {
                    firstName = account.displayName.toString().split(" ")[0]
                    try {
                        lastName = account.displayName.toString().split(" ")[1]
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }

            signUpViewModel.socialLogin(
                email,
                "$firstName $lastName",
                Constants.GOOGLE,
                providerId,
                "",
                sessionManager.getFirebaseToken()
            )
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignUpActivity", "signInResult:failed code=" + e.statusCode)
        }
    }

    /**
     * Click on login with SnapChat
     */
    private fun loginWithSnapchat() {
        SnapLogin.getAuthTokenManager(this).clearToken()
        SnapLogin.getAuthTokenManager(this@SignUpActivity).startTokenGrant()
    }

    /**
     * Click on continue with Google
     */
    private fun googleLogin() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Click on continue with Facebook
     */
    private fun facebookLogin() {

        LoginManager.getInstance().logOut()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e("SignUpActivity", "accessToken: ${loginResult.accessToken}")

                    val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken)
                    { jsonObj, _ ->
                        if (jsonObj != null) {
                            try {
                                providerId = jsonObj.getString("id")
                                if (jsonObj.has("name")) {
                                    firstName = jsonObj.getString("name").split(" ")[0]
                                    lastName = jsonObj.getString("name").split(" ")[1]
                                }
                                if (jsonObj.has("email") && !TextUtils.isEmpty(jsonObj.getString("email"))) {
                                    email = jsonObj.getString("email")
                                }
                                signUpViewModel.socialLogin(
                                    email,
                                    "$firstName $lastName",
                                    Constants.FACEBOOK,
                                    providerId,
                                    "",
                                    sessionManager.getFirebaseToken()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "" + e.printStackTrace(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,link")
                    graphRequest.parameters = parameters
                    graphRequest.executeAsync()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    error.printStackTrace()
                    Log.e("SignUpActivity", "onError: ${error.message}")
                    Log.e("SignUpActivity", "onError: $error")

                    var msg = ""
                    if (error is java.net.UnknownHostException) {
                        msg = getString(R.string.no_internet)
                    } else if (error is java.net.SocketTimeoutException || error is java.net.ConnectException) {
                        msg = getString(R.string.slow_internet)
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        showMsg(error.message!!)
                    }
                }
            })

        loginManager.logInWithReadPermissions(
            this@SignUpActivity,
            listOf("email")
        )

    }

    /**
     * click on sign up with phone/email
     */
    private fun signupWithPhone() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(
            SignupWithPhoneFragment.getInstance(
                false,
                isFromDashboard = false
            ),
            Constants.SIGNUP_WITH_PHONE_FRAGMENT
        )
    }

    private fun fetchUserData() {
        val query = "{me{bitmoji{avatar},displayName,externalId}}"
        SnapLogin.fetchUserData(this@SignUpActivity, query, null, this)
    }

    fun onLoginClick(view: View) {
        when (view) {
            llSignupWithPhone -> {
                signupWithPhone()
            }

            llSignupWithFacebook -> {
                facebookLogin()
            }

            llSignupWithGoogle -> {
                googleLogin()
            }

            llSignupWithSnapchat -> {
                loginWithSnapchat()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("SignUpActivity", "resultCode: $resultCode")
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                handleSignInResult(task)
            }
        }
    }

    override fun onDestroy() {
        signUpViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSuccess(p0: UserDataResponse?) {
        val me = p0!!.data.me
        val name = me.displayName
        val avatar = me.bitmojiData.avatar
        providerId = me.externalId

        signUpViewModel.socialLogin(
            email,
            name,
            Constants.SNAPCHAT,
            providerId,
            "",
            sessionManager.getFirebaseToken()
        )
    }

    override fun onFailure(p0: Boolean, p1: Int) {
        signUpViewModel.setIsLoading(false)
        print("onFailure")
    }

    override fun onLogout() {
        print("onLogout")
    }

    override fun onLoginFailed() {
        signUpViewModel.setIsLoading(false)
        print("failed")
    }

    override fun onLoginSucceeded() {
        signUpViewModel.setIsLoading(true)
        fetchUserData()
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
                    Log.i(TAG, "Permission has been denied by user CAMERA")
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

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoViewSignup.pause()
    }

    override fun onResume() {
        super.onResume()
        if (videoViewSignup != null)
            videoViewSignup.start()
    }

    override fun skipLogin() {
        tvSkipSignUp.visibility = View.INVISIBLE
        sessionManager.setGuestUser(true)
        addFragment(
            SelectGenderFragment.getInstance("user"),
            Constants.SELECT_GENDER_FRAGMENT
        )
    }

    override fun onClickContinue() {
        if(Utils.isOpenRecently()) return

        if (signUpViewModel.isValidPhone(edtPhone.text.toString().trim())){
            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
            jsonObject.addProperty(Constants.USER_UNIQUE_ID, androidId)
            Log.d("country code :", ccp.selectedCountryCodeAsInt.toString())
            jsonObject.addProperty(
                Constants.MOBILE,
                "+".plus(ccp.selectedCountryCodeAsInt.toString()).plus(edtPhone.text.toString().trim())
            )
            signUpViewModel.sendOTP(jsonObject)

//            val intent = Intent(this@SignUpActivity,OTPActivity::class.java)
//            intent.putExtra(Constants.MOBILE,"sdfsdf")
//            openActivity(intent)
        }
    }

    override fun onSuccessResponse(user: User) {
        sessionManager.setAccessToken(user.token)
//        sessionManager.setUserEmail(user.email)
        sessionManager.setUserId(user.user_id)
        sessionManager.setStringValue(user.username,Constants.USERNAME)
        sessionManager.setUserPhone(user.mobile)
        edtPhone.text!!.clear()
        Utils.hideKeyboard(this@SignUpActivity)

        val intent = Intent(this@SignUpActivity,OTPActivity::class.java)
        intent.putExtra(Constants.MOBILE,user.mobile)
        openActivity(intent)
    }

//    override fun onSuccessResponse(user: User) {
//        Log.e("SignUpActivity", "user: ${user.token}")
//        tvSkipSignUp.visibility = View.INVISIBLE
//        sessionManager.setGuestUser(false)
//        sessionManager.setAccessToken(user.token)
//        sessionManager.setUserEmail(user.email)
//        sessionManager.setUserPhone(user.mobile)
//        sessionManager.setVerifiedUser(user.is_verified)
//        sessionManager.setuserUniqueId(user.user_uniqueId)
//        if (user.is_register == 1) {
//            val intent = Intent(this@SignUpActivity,DashboardActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            openActivity(intent)
//        } else {
//            addFragment(
//                SelectGenderFragment.getInstance(
//                    "user"
//                ),
//                Constants.SELECT_GENDER_FRAGMENT
//            )
//        }
//
//    }

    override fun onBackPressed() {
        val selectGenderFragment =
            supportFragmentManager.findFragmentByTag(Constants.SELECT_GENDER_FRAGMENT)
        val videoLanguageFrgment =
            supportFragmentManager.findFragmentByTag(Constants.VIDEO_LANGUAGE_FRAGMENT)
        val chooseInterestFragment =
            supportFragmentManager.findFragmentByTag(Constants.CHOOSE_INTEREST_FRAGMENT)
        val signupWithPhoneFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_WITH_PHONE_FRAGMENT)


        if (videoLanguageFrgment != null || chooseInterestFragment != null) {
            supportFragmentManager.popBackStack()
        } else if (selectGenderFragment != null) {
            if (SelectGenderFragment.datePickerDialog != null)
                SelectGenderFragment.datePickerDialog!!.dismiss()

            tvSkipSignUp.visibility = View.VISIBLE
            removeAllFragment()

        } else if (signupWithPhoneFragment != null) {
            val childFm = signupWithPhoneFragment.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack()
            } else {
                tvSkipSignUp.visibility = View.VISIBLE
                supportFragmentManager.popBackStack()
            }
        } else
            super.onBackPressed()
    }

    /**
     * To make terms and privacy policy clickable.
     */
    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
//                    textPaint.color = Color.BLACK
                    textPaint.typeface = Typeface.create(Typeface.createFromAsset(context.assets, "Muli-ExtraBold.ttf"), Typeface.BOLD)
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
//                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}
