package com.namastey.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.customViews.AppPreferences
import com.namastey.customViews.AuthenticationDialog
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySocialLinkBinding
import com.namastey.listeners.AuthenticationListener
import com.namastey.model.InstagramData
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileInterestViewModel
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.squareup.okhttp.OkHttpClient
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.activity_social_link.*
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


class SocialLinkActivity : BaseActivity<ActivitySocialLinkBinding>(), AuthenticationListener,
    ProfileInterestView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loginManager: LoginManager
    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var binding: ActivitySocialLinkBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    lateinit var mTwitterAuthClient: TwitterAuthClient
    private lateinit var mProfileTracker: ProfileTracker
    private var token: String? = null
    private var appPreferences: AppPreferences? = null
    private val client = OkHttpClient()
    private lateinit var authenticationDialog: AuthenticationDialog
    private var isEdit = false

    override fun getLayoutId() = R.layout.activity_social_link

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModel() = profileInterestViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
        //setContentView(R.layout.activity_social_link)
        FacebookSdk.sdkInitialize(applicationContext)

        setupViewModel()
    }

    private fun setupViewModel() {
        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        profileInterestViewModel.setViewInterface(this)

        binding = bindViewData()
        binding.viewModel = profileInterestViewModel
        appPreferences = AppPreferences(this)

        if (intent.hasExtra(Constants.KEY_IS_LOGIN))
            profileInterestViewModel.getSocialLink()

        initData()
        showLinkDeleteIcons()
    }

    private fun initData() {
        if (intent.hasExtra(Constants.ACTIVITY_EDIT)) {
            tvSkip.text = getString(R.string.done)
            btnContinue.visibility = View.GONE
        }

        Utils.rectangleShapeBorder(
            tvFacebook,
            ContextCompat.getColor(this, R.color.color_blue_facebook),
            true
        )
        Utils.rectangleShapeBorder(
            tvInstagram,
            ContextCompat.getColor(this, R.color.color_instagram),
            true
        )
        Utils.rectangleShapeBorder(
            tvTwitter,
            ContextCompat.getColor(this, R.color.color_twitter),
            true
        )
        Utils.rectangleShapeBorder(
            tvSpotify,
            ContextCompat.getColor(this, R.color.color_spotify),
            true
        )


        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    Constants.TwitterConstants.CONSUMER_KEY,
                    Constants.TwitterConstants.CONSUMER_SECRET
                )
            )
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        Twitter.initialize(config)

        mTwitterAuthClient = TwitterAuthClient()
        //For facebook used initializer
        callbackManager = CallbackManager.Factory.create()

        if (intent.hasExtra("socialAccountList")) {
            val data: ArrayList<SocialAccountBean> =
                intent.extras!!.getSerializable("socialAccountList") as ArrayList<SocialAccountBean>

            Log.e("SocialLinksActivity", "socialAccountList: ${data.size}")
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
//                mainFacebook.visibility = View.VISIBLE
                tvFacebook.text = data.single { s -> s.name == getString(R.string.facebook) }.link
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
//                mainInstagram.visibility = View.VISIBLE
                tvInstagram.text = data.single { s -> s.name == getString(R.string.instagram) }.link
            }

            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
//                mainTwitter.visibility = View.VISIBLE
                tvTwitter.text = data.single { s -> s.name == getString(R.string.twitter) }.link
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
//                mainSpotify.visibility = View.VISIBLE
                tvSpotify.text = data.single { s -> s.name == getString(R.string.spotify) }
                    .link
            }

        }

    }

/*    fun printHashKey(pContext: Context) {
        try {
            val info = packageManager.getPackageInfo(
                "com.namastey",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }*/

    private fun showLinkDeleteIcons() {
        if (tvFacebook.text.toString() == getString(R.string.connect_facebook)) {
            ivFacebookDelete.visibility = View.GONE
        } else {
            ivFacebookDelete.visibility = View.VISIBLE
        }

        Log.e("SocialLinkActivity", "edtTwitter: ${tvTwitter.text.toString()}")

        if (tvTwitter.text.toString() == getString(R.string.connect_twitter)) {
            ivTwitterDelete.visibility = View.GONE
        } else {
            ivTwitterDelete.visibility = View.VISIBLE
        }

        if (tvInstagram.text.toString() == getString(R.string.connect_instagram)) {
            ivInstagramDelete.visibility = View.GONE
        } else {
            ivInstagramDelete.visibility = View.VISIBLE
        }

        if (tvSpotify.text.toString() == getString(R.string.connect_spotify)) {
            ivSpotifyDelete.visibility = View.GONE
        } else {
            ivSpotifyDelete.visibility = View.VISIBLE
        }
    }

    fun onClickContinue(view: View) {
        createRequest()
    }

    override fun onBackPressed() {
        finishActivity()
    }


    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickSkip(view: View) {
        if (intent.hasExtra(Constants.ACTIVITY_EDIT)) {
            createRequest()
        } else
            openActivity(this@SocialLinkActivity, AddVideoActivity())
    }

    private fun connectFacebook() {

        LoginManager.getInstance().logOut()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance()
            .logInWithReadPermissions(
                this@SocialLinkActivity,
                listOf("email", "public_profile")
            )
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_link"))


        val mcallbackLogin: FacebookCallback<LoginResult?> =
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    if (loginResult!!.accessToken != null) {
                        Log.e(
                            "SocialLinksActivity",
                            "LoginButton FacebookCallback onSuccess token : " + loginResult.accessToken
                                .token
                        )
                        Log.e(
                            "SocialLinksActivity",
                            "LoginButton FacebookCallback onSuccess userId : " + loginResult.accessToken
                                .userId
                        )
                        val profile = Profile.getCurrentProfile()
                        if (profile != null) {
                            if (profile.linkUri != null) {
                                Log.d("Facebook profile :", profile.linkUri.toString())
                                tvFacebook.text = profile.linkUri.toString()
                                ivFacebookDelete.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onCancel() {
                    Log.e("SocialLinksActivity", "LoginButton FacebookCallback onCancel")
                }

                override fun onError(exception: FacebookException) {
                    exception.printStackTrace()
                    Log.e("SocialLinksActivity", "Exception:: " + exception.message)
                    Log.e("SocialLinksActivity", "Exception:: " + exception.stackTrace)
                }
            }
        LoginManager.getInstance().registerCallback(callbackManager, mcallbackLogin)
    }

    private fun connectToInstagram() {
        var instagramUrl = "https://api.instagram.com/oauth/authorize\n" +
                "  ?client_id=" + getString(R.string.instagram_app_id) + "\n" +
                "  &redirect_uri=https://httpstat.us/200\n" +
                "  &scope=user_profile\n" +
                "  &response_type=code"
//        profileInterestViewModel.getInstagram(instagramUrl)

        authenticationDialog = AuthenticationDialog(this, this)
        authenticationDialog.setCancelable(true)
//        authenticationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        authenticationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        token = null
        authenticationDialog.show()

    }

    private fun getUserInfoByAccessToken(token: String) {
        val redirect_url = getString(R.string.callback_url)

        val apidata = "https://api.instagram.com/oauth/access_token"
        profileInterestViewModel.getInstagramToken(
            apidata, getString(R.string.client_id),
            getString(R.string.instagram_client_secret), token, "authorization_code",
            redirect_url
        )
    }

    private fun getTwitterSession(): TwitterSession? {

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        if (TwitterCore.getInstance().sessionManager.activeSession != null)
            TwitterCore.getInstance().sessionManager.clearActiveSession()

        return TwitterCore.getInstance().sessionManager.activeSession
    }

    fun fetchUserProfile(twitterSession: TwitterSession?) {
        val twitterApiClient = TwitterApiClient(twitterSession)
        val getUserCall = twitterApiClient.accountService.verifyCredentials(true, false, true)
        getUserCall.enqueue(object : Callback<User?>() {

            override fun failure(exception: TwitterException) {
                Log.e("Twitter", "Failed to get user data " + exception.message)
            }

            override fun success(result: Result<User?>?) {
                val user: User = result!!.data!!
                Log.e(
                    "Twitter url : ",
                    Uri.parse("twitter://user?screen_name=" + user.screenName).toString()
                )
                ivTwitterDelete.visibility = View.VISIBLE
                tvTwitter.text =
                    Uri.parse("twitter://user?screen_name=" + user.screenName).toString()

            }
        })
    }

    private fun twitterLogin() {

        if (getTwitterSession() == null) {
            mTwitterAuthClient.authorize(this, object : Callback<TwitterSession>() {
                override fun success(twitterSessionResult: Result<TwitterSession>) {
                    Toast.makeText(this@SocialLinkActivity, "Success", Toast.LENGTH_SHORT).show()
                    val twitterSession = twitterSessionResult.data
                    fetchUserProfile(twitterSession)
                }

                override fun failure(e: TwitterException) {
                    Toast.makeText(this@SocialLinkActivity, "Failure", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun createRequest() {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        var jsonObjectInner = JsonObject()

        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.facebook))
        if (tvFacebook.text.toString() != getString(R.string.connect_facebook)) {
            jsonObjectInner.addProperty(Constants.LINK, tvFacebook.text.toString().trim())
        } else jsonObjectInner.addProperty(Constants.LINK, "")
        jsonArray.add(jsonObjectInner)

        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.twitter))
        if (tvTwitter.text.toString() != getString(R.string.connect_twitter)) {
            jsonObjectInner.addProperty(Constants.LINK, tvTwitter.text.toString().trim())
        } else jsonObjectInner.addProperty(Constants.LINK, "")
        jsonArray.add(jsonObjectInner)


        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.instagram))
        if (tvInstagram.text.toString() != getString(R.string.connect_instagram)) {
            jsonObjectInner.addProperty(Constants.LINK, tvInstagram.text.toString().trim())
        } else jsonObjectInner.addProperty(Constants.LINK, "")
        jsonArray.add(jsonObjectInner)

        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.spotify))
        if (tvSpotify.text.toString() != getString(R.string.connect_spotify)) {
            jsonObjectInner.addProperty(Constants.LINK, tvSpotify.text.toString().trim())
        } else jsonObjectInner.addProperty(Constants.LINK, "")
        jsonArray.add(jsonObjectInner)

        jsonObject.add("social_links_details", jsonArray)
        Log.e("AddLinkRequest : ", jsonObject.toString())
        if (isEdit)
            profileInterestViewModel.addSocialLink(jsonObject)
        else finishActivity()
    }

    fun onClickButton(view: View) {
        isEdit=true
        when (view.id) {
            R.id.tvFacebook -> connectFacebook()
            R.id.tvInstagram -> connectToInstagram()
            R.id.tvTwitter -> twitterLogin()
            R.id.tvSpotify -> {
                val builder: AuthenticationRequest.Builder =
                    AuthenticationRequest.Builder(
                        getString(R.string.spotify_client_id),
                        AuthenticationResponse.Type.TOKEN,
                        Constants.SPOTIFY_REDIRECT_URL
                    )
                builder.setScopes(arrayOf("user-read-private", "streaming"))
                val request: AuthenticationRequest = builder.build()

                AuthenticationClient.openLoginActivity(
                    this,
                    Constants.REQUEST_SPOTIFY,
                    request
                )
            }
            R.id.ivFacebookDelete -> {
                ivFacebookDelete.visibility = View.GONE
                tvFacebook.text = getString(R.string.connect_facebook)
            }
            R.id.ivInstagramDelete -> {
                ivInstagramDelete.visibility = View.GONE
                tvInstagram.text = getString(R.string.connect_instagram)
            }
            R.id.ivTwitterDelete -> {
                ivTwitterDelete.visibility = View.GONE
                tvTwitter.text = getString(R.string.connect_twitter)
            }
            R.id.ivSpotifyDelete -> {
                ivSpotifyDelete.visibility = View.GONE
                tvSpotify.text = getString(R.string.connect_spotify)
            }
        }
    }

    override fun onTokenReceived(auth_token: String?) {
        if (auth_token == null)
            return;
        appPreferences!!.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("SocialLinksActivity", "requestCode: \t $requestCode")
        Log.e("SocialLinksActivity", "resultCode: \t $resultCode")
        Log.e("SocialLinksActivity", "data: \t ${data!!.data} ")

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == Constants.REQUEST_SPOTIFY) {
            val response =
                AuthenticationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.e("Social Login", response.accessToken)
                    profileInterestViewModel.getSpotifyLink(response.accessToken)
                }
                AuthenticationResponse.Type.ERROR -> {
                    Log.e("Social Login", response.error)
                }
                else -> {
                    Log.e("Social Login", response.toString())
                }
            }
        }
    }

    override fun onSuccessResponse(data: ArrayList<SocialAccountBean>) {
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
            ivFacebookDelete.visibility = View.VISIBLE
            tvFacebook.text = data.single { s -> s.name == getString(R.string.facebook) }
                .link
        } else {
            ivFacebookDelete.visibility = View.GONE
        }

        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
            ivInstagramDelete.visibility = View.VISIBLE
            tvInstagram.text = data.single { s -> s.name == getString(R.string.instagram) }
                .link
        } else {
            ivInstagramDelete.visibility = View.GONE
        }

        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
            ivTwitterDelete.visibility = View.VISIBLE
            tvTwitter.text = data.single { s -> s.name == getString(R.string.twitter) }
                .link
        } else {
            ivTwitterDelete.visibility = View.GONE
        }


        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
            ivSpotifyDelete.visibility = View.VISIBLE
            tvSpotify.text = data.single { s -> s.name == getString(R.string.spotify) }
                .link
        } else {
            ivSpotifyDelete.visibility = View.GONE
        }
    }

    override fun onSuccessAddLinkResponse(data: ArrayList<SocialAccountBean>) {
        if (intent.hasExtra(Constants.ACTIVITY_EDIT))
            finishActivity()
        else
            openActivity(this@SocialLinkActivity, AddVideoActivity())
    }

    override fun onSuccessSpotify(spotifyUrl: String) {
        tvSpotify.text = spotifyUrl
        ivSpotifyDelete.visibility = View.VISIBLE
    }

    override fun onSuccessInstagram(instagramData: InstagramData) {
        val instagramLink = "http://instagram.com/_u/".plus(instagramData.username)
        tvInstagram.text = instagramLink
        ivInstagramDelete.visibility = View.VISIBLE
    }

    override fun onHandleException(e: Throwable) {
        super.onHandleException(e)
        finishActivity()
    }
}