package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySignUpBinding
import com.namastey.fragment.SelectGenderFragment
import com.namastey.fragment.SignupWithPhoneFragment
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import javax.inject.Inject


class SignUpActivity : BaseActivity<ActivitySignUpBinding>(), SignUpView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var providerId = ""
    private var profileUrl = ""

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
        callbackManager =
            CallbackManager.Factory.create()
    }

    override fun skipLogin() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(
            SelectGenderFragment.getInstance("user"),
            Constants.SELECT_GENDER_FRAGMENT
        )
    }

    override fun onSuccessResponse(user: User) {
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
        tvSkipSignUp.visibility = View.VISIBLE

        if (videoLanguageFrgment != null || chooseInterestFragment != null) {
            supportFragmentManager.popBackStack()
        } else if (selectGenderFragment != null) {
            removeAllFragment()
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
        }
    }

    /**
     * Click on continue with Google
     */
    private fun googleLogin() {

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
                                profileUrl =
                                    "https://graph.facebook.com/$providerId/picture?type=large"
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

    override fun onDestroy() {
        signUpViewModel.onDestroy()
        super.onDestroy()
    }
}
