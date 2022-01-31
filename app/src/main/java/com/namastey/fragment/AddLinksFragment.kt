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
import com.namastey.customViews.AppPreferences
import com.namastey.customViews.AuthenticationDialog
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddLinksBinding
import com.namastey.listeners.AuthenticationListener
import com.namastey.model.InstagramData
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileInterestViewModel
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.fragment_add_links.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AddLinksFragment : BaseFragment<FragmentAddLinksBinding>(), ProfileInterestView,
    View.OnClickListener, AuthenticationListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddLinksBinding: FragmentAddLinksBinding
    private lateinit var layoutView: View
    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    lateinit var mTwitterAuthClient: TwitterAuthClient
    private lateinit var mProfileTracker: ProfileTracker
    private var token: String? = null
    private var appPreferences: AppPreferences? = null
    private val client = OkHttpClient()
    private lateinit var authenticationDialog: AuthenticationDialog

    override fun getLayoutId() = R.layout.fragment_add_links

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModel() = profileInterestViewModel

    companion object {
        fun getInstance(fromEditProfile: Boolean, socialAccountList: ArrayList<SocialAccountBean>) =
            AddLinksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("fromEditProfile", fromEditProfile)
                    putSerializable("socialAccountList", socialAccountList)
                }
            }
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

    private fun setupViewModel() {
        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        profileInterestViewModel.setViewInterface(this)

        fragmentAddLinksBinding = getViewBinding()
        fragmentAddLinksBinding.viewModel = profileInterestViewModel
        appPreferences = AppPreferences(activity);

        initListener()
        initData()
        showLinkDeleteIcons()
    }

    private fun initListener() {
        ivCloseAddLink.setOnClickListener(this)
        tvAddLinkSave.setOnClickListener(this)
//        tvFacebook.setOnClickListener(this)
        edtFacebook.setOnClickListener(this)
        edtInstagram.setOnClickListener(this)
        edtSpotify.setOnClickListener(this)
        edtTwitter.setOnClickListener(this)

        ivFacebookDelete.setOnClickListener(this)
        ivTwitterDelete.setOnClickListener(this)
        ivInstagramDelete.setOnClickListener(this)
        ivSpotifyDelete.setOnClickListener(this)
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
        callbackManager = CallbackManager.Factory.create()

        if (arguments!!.containsKey("socialAccountList")) {
            val data: ArrayList<SocialAccountBean> = arguments!!.getSerializable("socialAccountList") as ArrayList<SocialAccountBean>
            Log.e("AddLinksFragment", "socialAccountList: ${data.size}")
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
                mainFacebook.visibility = View.VISIBLE
                edtFacebook.setText(data.single { s -> s.name == getString(R.string.facebook) }.link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
                mainInstagram.visibility = View.VISIBLE
                edtInstagram.setText(data.single { s -> s.name == getString(R.string.instagram) }.link)
            }

            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
                mainTwitter.visibility = View.VISIBLE
                edtTwitter.setText(data.single { s -> s.name == getString(R.string.twitter) }.link)
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
                edtTwitter.setText(
                    Uri.parse("twitter://user?screen_name=" + user.screenName).toString()
                )

            }
        })
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
        Log.e("AddLinkRequest : ", jsonObject.toString())
        profileInterestViewModel.addSocialLink(jsonObject)
    }

    private fun connectFacebook() {

        LoginManager.getInstance().logOut()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance()
            .logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile", "user_link", "email")
            )
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_link"))


        val mcallbackLogin: FacebookCallback<LoginResult?> =
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    if (loginResult!!.accessToken != null) {
                        Log.e(
                            "AddLinksFragment",
                            "LoginButton FacebookCallback onSuccess token : " + loginResult.accessToken
                                .token
                        )
                        Log.e(
                            "AddLinksFragment",
                            "LoginButton FacebookCallback onSuccess userId : " + loginResult.accessToken
                                .userId
                        )
//                        GraphRequest.newMeRequest(
//                            AccessToken.getCurrentAccessToken(),
//                            object : GraphRequest.GraphJSONObjectCallback {
//                                override fun onCompleted(
//                                    `object`: JSONObject?,
//                                    response: GraphResponse?
//                                ) {
//                                    if (null != `object`) {
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            "FaceBookLogin object: \t $`object`"
//                                        )
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            "FaceBookLogin response: \t $response"
//                                        )
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            `object`.optString("name") +
//                                                    `object`.optString("first_name") +
//                                                    `object`.optString("email")
//                                        )
//                                    }
//                                }
//                            }).executeAsync()

                        val profile = Profile.getCurrentProfile()
                        Log.d("Facebook profile :", profile!!.linkUri.toString())
                        edtFacebook.setText(profile!!.linkUri.toString())

//                        val request = GraphRequest.newMeRequest(
//                            AccessToken.getCurrentAccessToken(),
//                            object : GraphRequest.GraphJSONObjectCallback {
//                                override fun onCompleted(
//                                    `object`: JSONObject?,
//                                    response: GraphResponse?
//                                ) {
//                                    if (null != `object`) {
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            "FaceBookLogin object: \t $`object`"
//                                        )
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            "FaceBookLogin response: \t $response"
//                                        )
//                                        Log.e(
//                                            "AddLinksFragment",
//                                            `object`.optString("name") +
//                                                    `object`.optString("first_name") +
//                                                    `object`.optString("email")
//                                        )
//
//
//                                        if (`object`.has("id")) {
//                                            val id = `object`.getString("id")
//                                            Log.e("AddLinksFragment", "FaceBookLogin id: \t $id")
//                                            val url = "https://www.facebook.com/$id"
//                                            Log.e("AddLinksFragment", "FaceBookLogin url: \t $url")
//                                        }
//
//                                        if (`object`.has("name")) {
//                                            val name = `object`.getString("name")
//                                            Log.e("AddLinksFragment", "FaceBookLogin id: \t $name")
//                                            val url = "https://www.facebook.com/$name"
//                                            Log.e("AddLinksFragment", "FaceBookLogin url: \t $url")
//                                        }
//                                    }
//                                }
//                            })

//                        val parameters = Bundle()
//                        parameters.putString("fields", "id,name,link")
//                        parameters.putString("fields", "link")
//                        request.parameters = parameters
//                        request.executeAsync()
                    }
                }

                override fun onCancel() {
                    Log.e("AddLinksFragment", "LoginButton FacebookCallback onCancel")
                }

                override fun onError(exception: FacebookException) {
                    exception.printStackTrace()
                    Log.e("AddLinksFragment", "Exception:: " + exception.message)
                    Log.e("AddLinksFragment", "Exception:: " + exception.stackTrace)
                }
            }
        LoginManager.getInstance().registerCallback(callbackManager, mcallbackLogin)

//        LoginManager.getInstance()
//            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    val credential =
//                        FacebookAuthProvider.getCredential(loginResult.accessToken.token)
//                    Log.e(
//                        "AddLinksFragment",
//                        "FaceBookLogin credential: \t ${credential.signInMethod}"
//                    )
//                    auth = FirebaseAuth.getInstance()
//
//                    val graphRequest = GraphRequest(
//                        loginResult.accessToken,
//                        "/user",
//                        null,
//                        HttpMethod.GET,
//                        GraphRequest.Callback { /* handle the result */ response: GraphResponse? ->
//                            Log.e("Facebook Response ", response.toString())
//
//                        }
//                    )
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id,user_link, link")
//                    graphRequest.parameters = parameters
//                    graphRequest.executeAsync()
//
//                    /*auth.signInWithCredential(credential)
//                        .addOnCompleteListener(requireActivity()) { task ->
//                            if (task.isSuccessful) {
//                                // Sign in success, update UI with the signed-in user's information
//                                val user = auth.currentUser
//                                Log.e("AddLinksFragment", "signInWithCredential:success")
//                                Log.e("AddLinksFragment", "FaceBookLogin user: \t $user")
//                                val graphRequest = GraphRequest(
//                                    loginResult.accessToken,
//                                    "/user",
//                                    null,
//                                    HttpMethod.GET,
//                                    GraphRequest.Callback { *//* handle the result *//* response: GraphResponse? ->
//                                        Log.e("Facebook Response ", response.toString())
//
//                                    }
//                                )
//                                val parameters = Bundle()
//                                parameters.putString("fields", "id,user_link, link")
//                                graphRequest.parameters = parameters
//                                graphRequest.executeAsync()
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.e("TAG", "signInWithCredential:failure", task.exception)
//                                Toast.makeText(
//                                    requireActivity(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }*/
//
//                    /*Log.e("AddLinksFragment", "Facebook accessToken : ${loginResult.accessToken}")
//                    val request: GraphRequest = GraphRequest.newGraphPathRequest(
//                        loginResult.accessToken,
//                        "",
//                        object : GraphRequest.GraphJSONObjectCallback, GraphRequest.Callback {
//                            override fun onCompleted(
//                                `object`: JSONObject?,
//                                response: GraphResponse?
//                            ) {
//                                Log.e(
//                                    "AddLinksFragment",
//                                    "Facebook onCompleted1 : ${response.toString()}"
//                                )
//                            }
//
//                            override fun onCompleted(response: GraphResponse?) {
//                                Log.e(
//                                    "AddLinksFragment",
//                                    "Facebook onCompleted2 : ${response.toString()}"
//                                )
//                            }
//                        })
//
//                    val parameters = Bundle()
//                    parameters.putString("fields", "link,birthday,email,hometown")
//                    request.parameters = parameters
//                    request.executeAsync()*/
//
//                    /* val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken)
//                     { jsonObj, _ ->
//                         if (jsonObj != null) {
//                             try {
// //                               var providerId = jsonObj.getString("id")
//
//                                 Log.e("AddLinksFragment", "Facebook jsonObj : ${ jsonObj.toString()}")
//                                 Log.d("Facebook resposne : ", jsonObj.toString())
// //                                if (jsonObj.has("id")){
// //                                    edtFacebook.setText("fb://profile/".plus(jsonObj.getInt("id")))
// ////                                    edtFacebook.setText("https://www.facebook.com/".plus(jsonObj.getInt("id")))
// //                                }
//                                 if (jsonObj.has("link") && !TextUtils.isEmpty(jsonObj.getString("link"))) {
//                                     val userLink = jsonObj.getString("link")
//                                     Log.e("AddLinksFragment", "Facebook userLink : $userLink")
//                                     edtFacebook.setText(userLink)
//                                 }
//                             } catch (e: JSONException) {
//                                 Log.e("AddLinksFragment", "Facebook JSONException : ${e.message}")
//                                 Log.e("AddLinksFragment", "Facebook JSONException : ${e.printStackTrace()}")
//                                 e.printStackTrace()
//                                 Toast.makeText(
//                                     activity,
//                                     "" + e.printStackTrace(),
//                                     Toast.LENGTH_SHORT
//                                 ).show()
//                             }
//                         }
//                     }
//                     val parameters = Bundle()
//                     parameters.putString("fields", "id,link")
//                     graphRequest.parameters = parameters
//                     graphRequest.executeAsync()*/
//                }
//
//                override fun onCancel() {
//                }
//
//                override fun onError(error: FacebookException) {
//                    error.printStackTrace()
//                    Log.e("AddLinksFragment", "onError: ${error.message}")
//                    Log.e("AddLinksFragment", "onError: $error")
//                    var msg = ""
//                    if (error is java.net.UnknownHostException) {
//                        msg = getString(R.string.no_internet)
//                    } else if (error is java.net.SocketTimeoutException || error is java.net.ConnectException) {
//                        msg = getString(R.string.slow_internet)
//                    }
//                    if (!TextUtils.isEmpty(msg)) {
//                        showMsg(error.message!!)
//                    }
//                }
//            })
        /* loginManager.logInWithReadPermissions(
             this,
             listOf("email", "public_profile", "link")
         )*/
    }
/*

    private fun fetchProfile() {
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(
                    `object`: JSONObject,
                    response: GraphResponse?
                ) {
                    Log.e("fetched info", `object`.toString())
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link") //write the fields you need
        request.parameters = parameters
        request.executeAsync()
    }
*/

    private fun twitterLogin() {

        if (getTwitterSession() == null) {
            mTwitterAuthClient.authorize(requireActivity(), object : Callback<TwitterSession>() {
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

    private fun showLinkDeleteIcons() {
        if (edtFacebook.text.toString() == "") {
            ivFacebookDelete.visibility = View.GONE
        } else {
            ivFacebookDelete.visibility = View.VISIBLE
        }

        Log.e("AddLinkFragment", "edtTwitter: ${edtTwitter.text.toString()}")

        if (edtTwitter.text.toString() == "") {
            ivTwitterDelete.visibility = View.GONE
        } else {
            ivTwitterDelete.visibility = View.VISIBLE
        }

        if (edtInstagram.text.toString() == "") {
            ivInstagramDelete.visibility = View.GONE
        } else {
            ivInstagramDelete.visibility = View.VISIBLE
        }

        if (edtSpotify.text.toString() == "") {
            ivSpotifyDelete.visibility = View.GONE
        } else {
            ivSpotifyDelete.visibility = View.VISIBLE
        }
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

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseAddLink -> {
                Utils.hideKeyboard(requireActivity())
                //fragmentManager!!.popBackStack()
            }
            tvAddLinkSave -> {
                createRequest()
            }
            edtFacebook -> {
                connectFacebook()
                //getProfileFromFB()
            }
            edtInstagram ->{
                connectToInstagram()
            }
            edtTwitter -> {
                twitterLogin()
            }
            edtSpotify -> {
                /*val builder: AuthenticationRequest.Builder =
                    AuthenticationRequest.Builder(
                        getString(R.string.spotify_client_id),
                        AuthenticationResponse.Type.TOKEN,
                        Constants.SPOTIFY_REDIRECT_URL
                    )

                builder.setScopes(arrayOf("user-read-private", "streaming"))
                val request: AuthenticationRequest = builder.build()

                AuthenticationClient.openLoginActivity(
                    requireActivity(),
                    Constants.REQUEST_SPOTIFY,
                    request
                )*/
            }
            ivFacebookDelete -> {
                edtFacebook.setText("")
                ivFacebookDelete.visibility = View.GONE
            }
            ivTwitterDelete -> {
                edtTwitter.setText("")
                ivTwitterDelete.visibility = View.GONE
            }
            ivInstagramDelete -> {
                edtInstagram.setText("")
                ivInstagramDelete.visibility = View.GONE
            }
            ivSpotifyDelete -> {
                edtSpotify.setText("")
                ivSpotifyDelete.visibility = View.GONE
            }
        }
    }

    private fun connectToInstagram() {
        var instagramUrl = "https://api.instagram.com/oauth/authorize\n" +
                "  ?client_id="+ getString(R.string.instagram_app_id) +"\n" +
                "  &redirect_uri=https://httpstat.us/200\n" +
                "  &scope=user_profile\n" +
                "  &response_type=code"
//        profileInterestViewModel.getInstagram(instagramUrl)

        authenticationDialog = AuthenticationDialog(activity as EditProfileActivity, this)
        authenticationDialog.setCancelable(true)
//        authenticationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        authenticationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        token = null
        authenticationDialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("AddLinksFragment", "requestCode: \t $requestCode")
        Log.e("AddLinksFragment", "resultCode: \t $resultCode")
        Log.e("AddLinksFragment", "data: \t ${data!!.data} ")

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == Constants.REQUEST_SPOTIFY) {
           /* val response =
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
            }*/
        }
    }

    override fun onSuccessResponse(data: ArrayList<SocialAccountBean>) {

    }

    override fun onSuccessAddLinkResponse(data: ArrayList<SocialAccountBean>) {
        //Log.e("AddLinksFragment", "onSuccessResponse: \t link: ${data[0].link}")
        //Log.e("AddLinksFragment", "onSuccessResponse: \t name: ${data[0].name}")
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
       // fragmentManager!!.popBackStack()
    }

    override fun onSuccessSpotify(spotifyUrl: String,name:String) {
        Log.e("Spotify URL : ", spotifyUrl)
        edtSpotify.setText(spotifyUrl)
    }

    override fun onSuccessInstagram(instagramData: InstagramData) {
        val instagramLink = "http://instagram.com/_u/".plus(instagramData.username)
        edtInstagram.setText(instagramLink)
    }
    override fun onDestroy() {
        profileInterestViewModel.onDestroy()
        super.onDestroy()
    }
    private fun getUserInfoByAccessToken(token: String) {
        val redirect_url = getString(R.string.callback_url)

        val apidata = "https://api.instagram.com/oauth/access_token"
        profileInterestViewModel.getInstagramToken(apidata,getString(R.string.client_id),
            getString(R.string.instagram_client_secret), token, "authorization_code",
                redirect_url)
    }

    override fun onTokenReceived(auth_token: String?) {
        if (auth_token == null)
            return;
        appPreferences!!.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token!!);
    }

}