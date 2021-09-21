package com.namastey.activity

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySplashBinding
import com.namastey.receivers.AppCloseService
import com.namastey.uiView.SplashNavigatorView
import com.namastey.utils.SessionManager
import com.namastey.utils.SplashView.ISplashListener
import com.namastey.viewModel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : BaseActivity<ActivitySplashBinding>(), SplashNavigatorView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var activitySplashBinding: ActivitySplashBinding
    private var MY_REQUEST_CODE = 105
    private lateinit var appUpdateManager : AppUpdateManager

    override fun openLoginActivity() {
//        Log.d("Login","user not login")
        inAppUpdate()
        /*splash_view.splashAndDisappear(object : ISplashListener {
            override fun onStart() {}
            override fun onUpdate(completionFraction: Float) {}
            override fun onEnd() {
                startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        })*/
//        Handler().postDelayed({
//        }, 1000)
//        openActivity(this,SignUpActivity())

    }

    override fun openMainActivity() {
//        Log.d("Login","user login")
//        ivSplashScreen.animate()
//            .setStartDelay(500)
//            .setDuration(1000)
//            .scaleX(20f)
//            .scaleY(20f)
//            .alpha(0f);

        splash_view.splashAndDisappear(object : ISplashListener {
            override fun onStart() {}
            override fun onUpdate(completionFraction: Float) {}
            override fun onEnd() {
                val intentDashboard = Intent(this@SplashActivity, DashboardActivity::class.java)

                if (intent.data != null) {
                    val data: Uri? = intent.data
                    val username = data?.getQueryParameter("username")
                    intentDashboard.putExtra("username", username)
                    intentDashboard.putExtra("fromLink", true)
                }
                startActivity(intentDashboard)
//                overridePendingTransition(0, 0)
                finish()
            }
        })

//        Handler().postDelayed({
//            startActivity(Intent(this, DashboardActivity::class.java))
//            overridePendingTransition(0, 0)
//            finish()
//        }, 1000)
    }

    override fun getViewModel() = splashViewModel

    override fun getLayoutId() = R.layout.activity_splash

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
       appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
        var mServiceIntent: Intent? = null
        mServiceIntent = Intent(this, AppCloseService()::class.java)
        if (!isMyServiceRunning(AppCloseService()::class.java)) {
            Log.e("SplashActivity", "onCreate In isMyServiceRunning")
            startService(mServiceIntent)

//            printHashKey(this@SplashActivity)
            /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 startForegroundService(mServiceIntent)
             } else {
                 startService(mServiceIntent)
             }*/
        }

        splashViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)
        activitySplashBinding = bindViewData()
        activitySplashBinding.viewModel = splashViewModel

        splashViewModel.nextScreen(this@SplashActivity, sessionManager.isLoginUser())
        val dynamicLink = intent.data
        if (dynamicLink != null) {
            Log.e("Dynamic_Link:", dynamicLink.toString())
            //dynamicLink.getQueryParameter()
        }
    }

//    fun printHashKey(pContext: Context) {
//        try {
//            val info: PackageInfo = pContext.getPackageManager()
//                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.i("FragmentActivity.TAG", "printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            Log.e("FragmentActivity.TAG", "printHashKey()", e)
//        } catch (e: Exception) {
//            Log.e("FragmentActivity.TAG", "printHashKey()", e)
//        }
//    }

    fun inAppUpdate() {

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    MY_REQUEST_CODE
                )
            }else{
                openActivity()
            }
        }

        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytesToDownload = state.totalBytesToDownload()
            }
            if (state.installStatus() == InstallStatus.DOWNLOADED){
                Toast.makeText(this,"An update has just been downloaded",Toast.LENGTH_LONG).show()
            }
        }
        appUpdateManager.registerListener(listener)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                inAppUpdate()
            }else{
                openActivity()
            }
        }
    }

    fun openActivity(){
        splash_view.splashAndDisappear(object : ISplashListener {
            override fun onStart() {}
            override fun onUpdate(completionFraction: Float) {}
            override fun onEnd() {
                startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    Toast.makeText(this,"An update has just been downloaded",Toast.LENGTH_LONG).show()
                }
            }

    }
}
