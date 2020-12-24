package com.namastey.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySignUpBinding
import com.namastey.fragment.SelectGenderFragment
import com.namastey.fragment.SignupWithPhoneFragment
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import com.namastey.utils.Constants.RC_SIGN_IN
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SignUpViewModel
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController
import com.snapchat.kit.sdk.login.models.UserDataResponse
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
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
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.signupvideo)
        videoViewSignup.setVideoURI(uri)
        videoViewSignup.start()

        videoViewSignup.setOnPreparedListener { mp ->
            //Start Playback
            videoViewSignup.start()
            //Loop Video
            mp!!.isLooping = true
        }
        initializeGoogleApi()

        setupPermissions()
    }

    private fun initializeGoogleApi() {
        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this)

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
            var account = task.getResult(ApiException::class.java)

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
                ""
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
//        SnapLogin.getAuthTokenManager(this).clearToken()
        SnapLogin.getAuthTokenManager(this).startTokenGrant()
    }

    /**
     * Click on continue with Google
     */
    private fun googleLogin() {
       /* var signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)*/

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
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
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
                                    ""
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
                    parameters.putString("fields", "id,name,email")
                    graphRequest.parameters = parameters
                    graphRequest.executeAsync()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
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
            listOf("email", "public_profile")
        )
    }

    /**
     * click on sign up with phone/email
     */
    private fun signupWithPhone() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(
            SignupWithPhoneFragment.getInstance(
                false, false
            ),
            Constants.SIGNUP_WITH_PHONE_FRAGMENT
        )
    }

    private fun fetchUserData() {
        val query = "{me{bitmoji{avatar},displayName,externalId}}"
        SnapLogin.fetchUserData(this, query, null, this)
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
            ""
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

    override fun onSuccessResponse(user: User) {
        tvSkipSignUp.visibility = View.INVISIBLE
        sessionManager.setGuestUser(false)
        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        if (user.is_completly_signup == 1) {
            sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
        } else {
            sessionManager.setBooleanValue(false, Constants.KEY_IS_COMPLETE_PROFILE)
        }
        addFragment(
            SelectGenderFragment.getInstance(
                "user"
            ),
            Constants.SELECT_GENDER_FRAGMENT
        )
    }

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
            var childFm = signupWithPhoneFragment.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack()
            } else {
                tvSkipSignUp.visibility = View.VISIBLE
                supportFragmentManager.popBackStack()
            }
        } else
            super.onBackPressed()
    }
}
