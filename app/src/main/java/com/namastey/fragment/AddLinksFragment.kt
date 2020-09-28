package com.namastey.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.EditProfileActivity
import com.namastey.activity.ProfileInterestActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddLinksBinding
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileInterestViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.fragment_add_links.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder
import javax.inject.Inject

class AddLinksFragment : BaseFragment<FragmentAddLinksBinding>(), ProfileInterestView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddLinksBinding: FragmentAddLinksBinding
    private lateinit var layoutView: View
    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private lateinit var auth: FirebaseAuth

    //    private val REQUEST_CODE = 1337
    private lateinit var twitter: Twitter
    lateinit var twitterDialog: Dialog
    var accToken: AccessToken? = null
    var provider: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
    override fun getLayoutId() = R.layout.fragment_add_links

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(fromEditProfile: Boolean, socialAccountList: ArrayList<SocialAccountBean>) =
            AddLinksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("fromEditProfile", fromEditProfile)
                    putSerializable("socialAccountList", socialAccountList)
                }
            }
    }

    private fun setupViewModel() {
        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        profileInterestViewModel.setViewInterface(this)

        fragmentAddLinksBinding = getViewBinding()
        fragmentAddLinksBinding.viewModel = profileInterestViewModel

        initListener()
        initData()
    }

    private fun initListener() {
        ivCloseAddLink.setOnClickListener(this)
        tvAddLinkSave.setOnClickListener(this)
        tvFacebook.setOnClickListener(this)
//        tvSpotify.setOnClickListener(this)
        edtSpotify.setOnClickListener(this)
        edtTwitter.setOnClickListener(this)
    }

    private fun initData() {

        // Set the connection parameters
//        val connectionParams = ConnectionParams.Builder(getString(R.string.spotify_client_id))
//            .setRedirectUri(getString(R.string.spotify_redirect_uri))
//            .showAuthView(true)
//            .build()
//
//        SpotifyAppRemote.connect(requireActivity(), connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                spotifyAppRemote = appRemote
//                Log.d("Social Login", "Connected! Yay!")
//                // Now you can start interacting with App Remote
//
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Log.e("Social Login", throwable.message, throwable)
//                // Something went wrong when attempting to connect! Handle errors here
//            }
//        })

        //For facebook used initializer
        callbackManager =
            CallbackManager.Factory.create()

        if (arguments!!.containsKey("socialAccountList")) {
            val data: ArrayList<SocialAccountBean> =
                arguments!!.getSerializable("socialAccountList") as ArrayList<SocialAccountBean>
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
                mainFacebook.visibility = View.VISIBLE
                edtFacebook.setText(data.single { s -> s.name == getString(R.string.facebook) }
                    .link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
                mainInstagram.visibility = View.VISIBLE
                edtInstagram.setText(data.single { s -> s.name == getString(R.string.instagram) }
                    .link)
            }

            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
                mainTwitter.visibility = View.VISIBLE
                edtTwitter.setText(data.single { s -> s.name == getString(R.string.twitter) }
                    .link)
            }
//            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.snapchat) }) {
//                mainSnapchat.visibility = View.VISIBLE
//                edtSnapchat.setText(data.single { s -> s.name == getString(R.string.snapchat) }
//                    .link)
//            }

//            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.tiktok) }) {
//                mainTikTok.visibility = View.VISIBLE
//                edtTiktok.setText(data.single { s -> s.name == getString(R.string.tiktok) }
//                    .link)
//            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
                mainSpotify.visibility = View.VISIBLE
                edtSpotify.setText(data.single { s -> s.name == getString(R.string.spotify) }
                    .link)
            }
//            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }) {
//                mainLinkedin.visibility = View.VISIBLE
//                edtLinkedin.setText(data.single { s -> s.name == getString(R.string.linkedin) }
//                    .link)
//            }
        }
    }


    override fun onStop() {
        super.onStop()
//        spotifyAppRemote?.let {
//            SpotifyAppRemote.disconnect(it)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(data: ArrayList<SocialAccountBean>) {
        if (activity is ProfileInterestActivity) {
            (activity as ProfileInterestActivity).onActivityReenter(
                Constants.ADD_LINK,
                Intent()
            )
        } else if (activity is EditProfileActivity) {
            targetFragment!!.onActivityResult(
                Constants.REQUEST_CODE,
                Activity.RESULT_OK,
                Intent().putExtra("fromAddLink", true)
            )
        }
        fragmentManager!!.popBackStack()
    }

    override fun getViewModel() = profileInterestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseAddLink -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            tvAddLinkSave -> {
                createRequest()
            }
            tvFacebook -> {
                connectFacebook()
            }

            edtTwitter -> {
//                twitterLogin()
//                getRequestToken()
            }
            edtSpotify -> {
                val builder: AuthenticationRequest.Builder =
                    AuthenticationRequest.Builder(
                        getString(R.string.spotify_client_id),
                        AuthenticationResponse.Type.TOKEN,
                        getString(R.string.spotify_redirect_uri)
                    )

                builder.setScopes(arrayOf("user-read-private", "streaming"))
                val request: AuthenticationRequest = builder.build()

                AuthenticationClient.openLoginActivity(
                    requireActivity(),
                    Constants.REQUEST_SPOTIFY,
                    request
                )
            }
        }
    }

    private fun createRequest() {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()

//        var jsonObjectInner: JsonObject = JsonObject()
//        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.snapchat))
//        jsonObjectInner.addProperty(Constants.LINK, edtSnapchat.text.toString().trim())
//        jsonArray.add(jsonObjectInner)

        var jsonObjectInner: JsonObject = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.facebook))
        jsonObjectInner.addProperty(Constants.LINK, edtFacebook.text.toString().trim())
        jsonArray.add(jsonObjectInner)

        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.twitter))
        jsonObjectInner.addProperty(Constants.LINK, edtTwitter.text.toString().trim())
        jsonArray.add(jsonObjectInner)

//        jsonObjectInner = JsonObject()
//        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.tiktok))
//        jsonObjectInner.addProperty(Constants.LINK, edtTiktok.text.toString().trim())
//        jsonArray.add(jsonObjectInner)

        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.instagram))
        jsonObjectInner.addProperty(Constants.LINK, edtInstagram.text.toString().trim())
        jsonArray.add(jsonObjectInner)

        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.spotify))
        jsonObjectInner.addProperty(Constants.LINK, edtSpotify.text.toString().trim())
        jsonArray.add(jsonObjectInner)

//        jsonObjectInner = JsonObject()
//        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.linkedin))
//        jsonObjectInner.addProperty(Constants.LINK, edtLinkedin.text.toString().trim())
//        jsonArray.add(jsonObjectInner)

        jsonObject.add("social_links_details", jsonArray)
        Log.d("AddLinkRequest : ", jsonObject.toString())
        profileInterestViewModel.addSocialLink(jsonObject)
    }

    private fun connectFacebook() {

        LoginManager.getInstance().logOut()
//        FirebaseAuth.getInstance().signOut()
        loginManager = LoginManager.getInstance()

        loginManager
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val credential =
                        FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                    auth = FirebaseAuth.getInstance()
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithCredential:success")
                                val user = auth.currentUser
//                                updateUI(user)
                                val graphRequest = GraphRequest(
                                    loginResult.accessToken,
                                    "/{user-link}/",
                                    null,
                                    HttpMethod.GET,
                                    GraphRequest.Callback { /* handle the result */ response: GraphResponse? ->
                                        Log.d("Facebook Response ", response.toString())

                                    }
                                )
                                val parameters = Bundle()
                                parameters.putString("fields", "id,link")
                                graphRequest.parameters = parameters
                                graphRequest.executeAsync()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithCredential:failure", task.exception)
                                Toast.makeText(
                                    requireActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                updateUI(null)
                            }

                            // ...
                        }


//                    val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken)
//                    { jsonObj, _ ->
//                        if (jsonObj != null) {
//                            try {
////                               var providerId = jsonObj.getString("id")
//
//                                if (jsonObj.has("link") && !TextUtils.isEmpty(jsonObj.getString("link"))) {
//                                    val userLink = jsonObj.getString("link")
//                                    edtFacebook.setText(userLink)
//                                }
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                                Toast.makeText(
//                                    activity,
//                                    "" + e.printStackTrace(),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id,link")
//                    graphRequest.parameters = parameters
//                    graphRequest.executeAsync()
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
            this,
            listOf("email", "public_profile")
        )
    }

    override fun onSuccessSpotify(spotifyUrl: String) {

        Log.d("Spotify URL : ", spotifyUrl)
        edtSpotify.setText(spotifyUrl)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_SPOTIFY) {
            val response =
                AuthenticationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d("Social Login", response.accessToken)
                    profileInterestViewModel.getSpotifyLink(response.accessToken)
                }
                AuthenticationResponse.Type.ERROR -> {
                    Log.d("Social Login", response.error)
                }
                else -> {
                    Log.d("Social Login", response.toString())
                }
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

//    private fun twitterLogin(){
//        val pendingResultTask: Task<AuthResult> = firebaseAuth.getPendingAuthResult()
//        if (pendingResultTask != null) {
//            // There's something already here! Finish the sign-in for your user.
//            pendingResultTask
//                .addOnSuccessListener(
//                    object : OnSuccessListener<AuthResult?>() {
//                        fun onSuccess(authResult: AuthResult?) {
//                            // User is signed in.
//                            // IdP data available in
//                            // authResult.getAdditionalUserInfo().getProfile().
//                            // The OAuth access token can also be retrieved:
//                            // authResult.getCredential().getAccessToken().
//                            // The OAuth secret can be retrieved by calling:
//                            // authResult.getCredential().getSecret().
//                        }
//                    })
//                .addOnFailureListener(
//                    object : OnFailureListener() {
//                        fun onFailure(@NonNull e: Exception?) {
//                            // Handle failure.
//                        }
//                    })
//        } else {
//            // There's no pending result so you need to start the sign-in flow.
//            // See below.
//        }
//    }

    private fun getRequestToken() {
        GlobalScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(Constants.TwitterConstants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.TwitterConstants.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                withContext(Dispatchers.Main) {
                    setupTwitterWebviewDialog(requestToken.authorizationURL)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupTwitterWebviewDialog(url: String) {
        twitterDialog = Dialog(requireActivity())
        val webView = WebView(requireActivity())
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()
    }

    // A client to know about WebView navigations
    // For API 21 and above
    @Suppress("OverridingDeprecatedMember")
    inner class TwitterWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(Constants.TwitterConstants.CALLBACK_URL)) {
                Log.d("Authorization URL: ", request?.url.toString())
                handleUrl(request?.url.toString())

                // Close the dialog after getting the oauth_verifier
                if (request?.url.toString().contains(Constants.TwitterConstants.CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(Constants.TwitterConstants.CALLBACK_URL)) {
                Log.d("Authorization URL: ", url)
                handleUrl(url)

                // Close the dialog after getting the oauth_verifier
                if (url.contains(Constants.TwitterConstants.CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        // Get the oauth_verifier
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            GlobalScope.launch(Dispatchers.Main) {
                accToken =
                    withContext(Dispatchers.IO) { twitter.getOAuthAccessToken(oauthVerifier) }
                getUserProfile()
            }
        }
    }

    suspend fun getUserProfile() {
        val usr = withContext(Dispatchers.IO) { twitter.verifyCredentials() }

        //Twitter Id
        val twitterId = usr.id.toString()
        Log.d("Twitter Id: ", twitterId)

        //Twitter Handle
        val twitterHandle = usr.screenName
        Log.d("Twitter Handle: ", twitterHandle)

        //Twitter Name
        val twitterName = usr.name
        Log.d("Twitter Name: ", twitterName)

        //Twitter Email
        val twitterEmail = usr.email
        Log.d(
            "Twitter Email: ",
            twitterEmail
                ?: "'Request email address from users' on the Twitter dashboard is disabled"
        )

        // Twitter Profile Pic URL
        val twitterProfilePic = usr.profileImageURLHttps.replace("_normal", "")
        Log.d("Twitter Profile URL: ", twitterProfilePic)

        // Twitter Access Token
        Log.d("Twitter Access Token", accToken?.token ?: "")

    }

    override fun onDestroy() {
        profileInterestViewModel.onDestroy()
        super.onDestroy()
    }
}