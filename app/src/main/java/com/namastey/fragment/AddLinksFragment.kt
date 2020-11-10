package com.namastey.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
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
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.fragment_add_links.*
import org.json.JSONException
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
    private lateinit var auth: FirebaseAuth
    lateinit var mTwitterAuthClient: TwitterAuthClient

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
//        tvFacebook.setOnClickListener(this)
        edtFacebook.setOnClickListener(this)
        edtSpotify.setOnClickListener(this)
        edtTwitter.setOnClickListener(this)
    }

    private fun initData() {

        val config = TwitterConfig.Builder(requireActivity())
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
            edtFacebook -> {
                connectFacebook()
            }

            edtTwitter -> {
                twitterLogin()
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
                                val user = auth.currentUser
                                Log.d("TAG", "signInWithCredential:success")
//                                val graphRequest = GraphRequest(
//                                    loginResult.accessToken,
//                                    "/user",
//                                    null,
//                                    HttpMethod.GET,
//                                    GraphRequest.Callback { /* handle the result */ response: GraphResponse? ->
//                                        Log.d("Facebook Response ", response.toString())
//
//                                    }
//                                )
//                                val parameters = Bundle()
//                                parameters.putString("fields", "id,user_link")
//                                graphRequest.parameters = parameters
//                                graphRequest.executeAsync()
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.w("TAG", "signInWithCredential:failure", task.exception)
//                                Toast.makeText(
//                                    requireActivity(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }

                            // ...
                        }


                    val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken)
                    { jsonObj, _ ->
                        if (jsonObj != null) {
                            try {
//                               var providerId = jsonObj.getString("id")

                                Log.d("Facebook resposne : ", jsonObj.toString())
//                                if (jsonObj.has("id")){
//                                    edtFacebook.setText("fb://profile/".plus(jsonObj.getInt("id")))
////                                    edtFacebook.setText("https://www.facebook.com/".plus(jsonObj.getInt("id")))
//                                }
                                if (jsonObj.has("link") && !TextUtils.isEmpty(jsonObj.getString("link"))) {
                                    val userLink = jsonObj.getString("link")
                                    edtFacebook.setText(userLink)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    activity,
                                    "" + e.printStackTrace(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,link")
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
            this,
            listOf("email", "public_profile")
        )
    }

    override fun onSuccessSpotify(spotifyUrl: String) {

        Log.d("Spotify URL : ", spotifyUrl)
        edtSpotify.setText(spotifyUrl)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }

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

    }

    private fun twitterLogin() {

        if (getTwitterSession() == null) {
            mTwitterAuthClient!!.authorize(requireActivity(), object : Callback<TwitterSession>() {
                override fun success(twitterSessionResult: Result<TwitterSession>) {
                    Toast.makeText(requireActivity(), "Success", Toast.LENGTH_SHORT).show()
                    val twitterSession = twitterSessionResult.data
                    fetchUserProfile(twitterSession)
                }

                override fun failure(e: TwitterException) {
                    Toast.makeText(requireActivity(), "Failure", Toast.LENGTH_SHORT).show()
                }
            })
        }
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
                Log.d(
                    "Twitter url : ",
                    Uri.parse("twitter://user?screen_name=" + user.screenName).toString()
                )
                edtTwitter.setText(
                    Uri.parse("twitter://user?screen_name=" + user.screenName).toString()
                )

            }
        })
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

    override fun onDestroy() {
        profileInterestViewModel.onDestroy()
        super.onDestroy()
    }
}