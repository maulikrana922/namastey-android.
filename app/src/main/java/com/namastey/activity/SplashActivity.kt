package com.namastey.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
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

    override fun openLoginActivity() {
//        Log.d("Login","user not login")

        splash_view.splashAndDisappear(object : ISplashListener {
            override fun onStart() {}
            override fun onUpdate(completionFraction: Float) {}
            override fun onEnd() {
                startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        })
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

                if (intent.data != null){
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
}
