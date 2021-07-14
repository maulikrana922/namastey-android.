package com.namastey.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.activity.ProfileBasicInfoActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSignUpBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SignUpViewModel
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController
import com.snapchat.kit.sdk.login.models.UserDataResponse
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback
import kotlinx.android.synthetic.main.fragment_sign_up.*
import org.json.JSONException
import java.util.*
import javax.inject.Inject

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(), SignUpView,
    View.OnClickListener, LoginStateController.OnLoginStateChangedListener,
    FetchUserDataCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var layoutView: View
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var providerId = ""
    private var isFromDashboard = false

    override fun skipLogin() {
    }

    override fun onClickContinue() {

    }

    override fun onSuccessResponse(user: User) {
        sessionManager.setGuestUser(false)
        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        if (user.is_register == 1) {
            openActivity(requireActivity(), DashboardActivity())
        } else {
        }
        fragmentManager!!.popBackStack()
        if (isFromDashboard)
            openActivity(requireActivity(), ProfileBasicInfoActivity())
        else
            openActivity(requireActivity(), DashboardActivity())
    }

    override fun getViewModel() = signUpViewModel

    override fun getLayoutId() = R.layout.fragment_sign_up

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(isFromDashboard: Boolean) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isFromDashboard", isFromDashboard)
                }
            }
    }

    private fun setupViewModel() {
        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        signUpViewModel.setViewInterface(this)

        fragmentSignUpBinding = getViewBinding()
        fragmentSignUpBinding.viewModel = signUpViewModel

        initData()
    }

    private fun initData() {
        ivSignupClose.setOnClickListener(this)
        llSignupWithGoogle.setOnClickListener(this)
        llSignupWithFacebook.setOnClickListener(this)
        llSignupWithSnapchat.setOnClickListener(this)
        llSignupWithPhone.setOnClickListener(this)

        if (arguments != null && arguments!!.containsKey("isFromDashboard")) {
            isFromDashboard = arguments!!.getBoolean("isFromDashboard", false)
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
            })
        )

        initializeGoogleApi()

        if (sessionManager.getFirebaseToken() == "") {
//            val token = FirebaseInstanceId.getInstance().token
//            sessionManager.setFirebaseToken(token.toString())
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                sessionManager.setFirebaseToken(token)
            })

        }
    }

    private fun initializeGoogleApi() {
        SnapLogin.getLoginStateController(requireActivity()).addOnLoginStateChangedListener(this)
        FacebookSdk.sdkInitialize(requireContext())

        //For facebook used initializer
        callbackManager =
            CallbackManager.Factory.create()

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(v: View?) {
        when (v) {
            ivSignupClose -> {
                fragmentManager!!.popBackStack()
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
            llSignupWithPhone -> {
                loginWithPhoneEmail()
            }
        }
    }

    /**
     * Click on sign up with phone/email
     */
    private fun loginWithPhoneEmail() {
        addFragment(
            SignupWithPhoneFragment.getInstance(
                true, isFromDashboard
            ),
            Constants.SIGNUP_WITH_PHONE_FRAGMENT
        )
    }

    /**
     * Click on login with SnapChat
     */
    private fun loginWithSnapchat() {
        SnapLogin.getAuthTokenManager(requireActivity()).startTokenGrant()
    }

    /**
     * Click on continue with Google
     */
    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                handleSignInResult(task)
            }
        }
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
                                    sessionManager.getUserUniqueId(),
                                    sessionManager.getFirebaseToken()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireActivity(),
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
            requireActivity(),
            listOf("email")
        )
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
                sessionManager.getUserUniqueId(),
                sessionManager.getFirebaseToken()
            )
        } catch (e: ApiException) {
            e.printStackTrace()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignUpFragment", "signInResult:failed code=" + e.statusCode)
        }
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
            sessionManager.getUserUniqueId(),
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

    private fun fetchUserData() {
        val query = "{me{bitmoji{avatar},displayName,externalId}}"
        SnapLogin.fetchUserData(requireActivity(), query, null, this)
    }

    override fun onDestroy() {
        signUpViewModel.onDestroy()
        super.onDestroy()
    }

    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.color = Color.BLACK
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}