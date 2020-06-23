package com.namastey.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.namastey.activity.ProfileActivity
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

    override fun skipLogin() {
    }

    override fun onSuccessResponse(user: User) {
        sessionManager.setGuestUser(false)
        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        fragmentManager!!.popBackStack()
        openActivity(requireActivity(), ProfileBasicInfoActivity())
    }

    override fun getViewModel() = signUpViewModel

    override fun getLayoutId() = R.layout.fragment_sign_up

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            SignUpFragment().apply {

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

        initializeGoogleApi()
    }

    private fun initializeGoogleApi() {
        SnapLogin.getLoginStateController(requireActivity()).addOnLoginStateChangedListener(this)

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
            llSignupWithPhone ->{
                loginWithPhoneEmail()
            }
        }
    }

    /**
     * Click on sign up with phone/email
     */
    private fun loginWithPhoneEmail() {
        (requireActivity() as ProfileActivity).addFragment(
            SignupWithPhoneFragment.getInstance(
                true
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
        var signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RC_SIGN_IN) {
                var task = GoogleSignIn.getSignedInAccountFromIntent(data);

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
            listOf("email", "public_profile")
        )
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
            Log.w("SignUpFragment", "signInResult:failed code=" + e.getStatusCode());
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
            providerId
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
}