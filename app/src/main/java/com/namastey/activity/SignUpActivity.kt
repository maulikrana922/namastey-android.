package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import javax.inject.Inject


class SignUpActivity : BaseActivity<ActivitySignUpBinding>(), SignUpView
//    ,LoginStateController.OnLoginStateChangedListener,
//    FetchUserDataCallback
    {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager
//    private lateinit var mLoginStateChangedListener: LoginStateController
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var providerId = ""

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
        initializeGoogleApi()
    }

    override fun skipLogin() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(
            SelectGenderFragment.getInstance("user"),
            Constants.SELECT_GENDER_FRAGMENT
        )
    }

    override fun onSuccessResponse(user: User) {
        tvSkipSignUp.visibility = View.INVISIBLE

        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
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
            supportFragmentManager.findFragmentByTag(Constants.VIDEO_LANGUAG_EFRAGMENT)
        val chooseInterestFragment =
            supportFragmentManager.findFragmentByTag(Constants.CHOOSE_INTEREST_FRAGMENT)
        val signupWithPhoneFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_WITH_PHONE_FRAGMENT)


        if (videoLanguageFrgment != null || chooseInterestFragment != null) {
            supportFragmentManager.popBackStack()
        } else if (selectGenderFragment != null) {
            tvSkipSignUp.visibility = View.VISIBLE
            removeAllFragment()
        } else if (signupWithPhoneFragment != null) {
            var childFm = signupWithPhoneFragment.getChildFragmentManager()
            if (childFm.getBackStackEntryCount() > 0) {
                childFm.popBackStack();
            } else {
                tvSkipSignUp.visibility = View.VISIBLE
                supportFragmentManager.popBackStack()
            }
        } else
            super.onBackPressed()
    }

    /**
     * Remove all fragment from stack
     */
    private fun removeAllFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                var task = GoogleSignIn.getSignedInAccountFromIntent(data);

                handleSignInResult(task)
            }
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
                providerId
            )
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignUpActivity", "signInResult:failed code=" + e.getStatusCode());
        }
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

    /**
     * Click on login with SnapChat
     */
    private fun loginWithSnapchat() {
//        SnapLogin.getAuthTokenManager(this).startTokenGrant()
    }

    /**
     * Click on continue with Google
     */
    private fun googleLogin() {
        var signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                                    providerId
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
                "signUp"
            ),
            Constants.SIGNUP_WITH_PHONE_FRAGMENT
        )
    }

    private fun initializeGoogleApi() {
//        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this)

        //For facebook used initializer
        callbackManager =
            CallbackManager.Factory.create()

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun onDestroy() {
        signUpViewModel.onDestroy()
        super.onDestroy()
    }

//    override fun onSuccess(p0: UserDataResponse?) {
//        val me = p0!!.data.me
//        val name = me.displayName
//        val avatar = me.bitmojiData.avatar
//    }
//
//    override fun onFailure(p0: Boolean, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onLogout() {
//        print("onLogout")
//    }
//
//    override fun onLoginFailed() {
//        print("failed")
//    }
//
//    override fun onLoginSucceeded() {
//        fetchUserData()
//    }
//    private fun fetchUserData() {
//        val query = "{me{bitmoji{avatar},displayName}}"
//        SnapLogin.fetchUserData(this, query, null, this)
//    }
}
