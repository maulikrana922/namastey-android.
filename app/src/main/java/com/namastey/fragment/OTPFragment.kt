package com.namastey.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.iid.FirebaseInstanceId
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.activity.ProfileActivity
import com.namastey.activity.ProfileBasicInfoActivity
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentOtpBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.OTPView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.OTPViewModel
import kotlinx.android.synthetic.main.fragment_otp.*
import javax.inject.Inject

class OTPFragment : BaseFragment<FragmentOtpBinding>(), OTPView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentOtpBinding: FragmentOtpBinding
    private lateinit var otpViewModel: OTPViewModel
    private lateinit var layoutView: View

    override fun onCloseOtp() {
        activity!!.onBackPressed()
    }

    override fun onConfirm() {

        otpViewModel.verifyOTP(
            sessionManager.getUserPhone(),
            sessionManager.getUserEmail(),
            etOtp.text.toString(),
            sessionManager.getFirebaseToken()
        )
    }

    override fun onSuccessResponse(user: User) {
        Utils.hideKeyboard(requireActivity())
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        sessionManager.setGuestUser(false)
        sessionManager.setIntegerValue(user.purchase, Constants.KEY_IS_PURCHASE)
        // sessionManager.setIntegerValue(user.is_completly_signup, Constants.KEY_IS_COMPLETE_PROFILE)
        if (user.is_completly_signup == 1) {
            sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
        } else {
            sessionManager.setBooleanValue(false, Constants.KEY_IS_COMPLETE_PROFILE)
        }
        var isFromProfile = false
        var isRegister = 0
        if (arguments != null && arguments!!.containsKey("isFromProfile")) {
            isFromProfile = arguments!!.getBoolean("isFromProfile", false)
            isRegister = arguments!!.getInt("isRegister")
            if (isRegister == 1)
                sessionManager.setUserId(user.user_id)
        }
        if (isFromProfile) {
            sessionManager.setGuestUser(false)
            removeAllFragment(fragmentManager!!)
            var isFromDashboard = false
            if (arguments != null && arguments!!.containsKey("isFromDashboard")) {
                isFromDashboard = arguments!!.getBoolean("isFromDashboard", false)
            }
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                openActivity(requireActivity(), ProfileBasicInfoActivity())
            }else {
                if (isFromDashboard)   // Open dashboard activity
                {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(activity, DashboardActivity::class.java))
                        activity!!.finish()
                    }, 1000)
                }else{
                    openActivity(requireActivity(), ProfileActivity())
                }
            }
            /*if (isFromDashboard)   // Open dashboard activity
                openActivity(requireActivity(), DashboardActivity())
            else
                openActivity(requireActivity(), ProfileBasicInfoActivity())*/
        } else {
            if (isRegister == 1) {
                ivSplashBackground.visibility = View.VISIBLE

                if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    openActivity(requireActivity(), ProfileBasicInfoActivity())
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(activity, DashboardActivity::class.java))
                        activity!!.finish()
                    }, 1000)
                }

                /* Handler(Looper.getMainLooper()).postDelayed({
                     startActivity(Intent(activity, DashboardActivity::class.java))
                     activity!!.finish()
                 }, 1000)*/
            } else {
                (activity as SignUpActivity).addFragment(
                    SelectGenderFragment.getInstance(
                        "user"
                    ),
                    Constants.SELECT_GENDER_FRAGMENT
                )
            }
        }
    }

    override fun getViewModel() = otpViewModel

    override fun getLayoutId() = R.layout.fragment_otp

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(
            mobile: String,
            email: String,
            isFromProfile: Boolean,
            isFromDashboard: Boolean,
            isRegister: Int
        ) =
            OTPFragment().apply {
                arguments = Bundle().apply {
                    putString("mobile", mobile)
                    putString("email", email)
                    putBoolean("isFromProfile", isFromProfile)
                    putBoolean("isFromDashboard", isFromDashboard)
                    putInt("isRegister", isRegister)
                }
            }
    }

    private fun setupViewModel() {
        otpViewModel = ViewModelProviders.of(this, viewModelFactory).get(OTPViewModel::class.java)
        otpViewModel.setViewInterface(this)

        fragmentOtpBinding = getViewBinding()
        fragmentOtpBinding.viewModel = otpViewModel

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {
        if (sessionManager.getFirebaseToken() == "") {
            Log.e("OTPFragment", "getFirebaseToken: Empty")
            /*val token = FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
               sessionManager.setFirebaseToken(task.result)
            }*/
            val token = FirebaseInstanceId.getInstance().token
            Log.e("OTPFragment", "FCM Registration Token: ${token.toString()}")
            sessionManager.setFirebaseToken(token.toString())
        } else {
            Log.e("OTPFragment", "getFirebaseToken(): \t ${sessionManager.getFirebaseToken()}")
        }
    }

    private fun dialogAdminBlockUser() {
        /* val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
         val dialogView: View =
             LayoutInflater.from(requireActivity())
                 .inflate(R.layout.dialog_admin_block_user, null, false)
         builder.setView(dialogView)
         val alertDialog: AlertDialog = builder.create()
         alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         alertDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
         alertDialog.show()*/

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_admin_block_user)
        dialog.show()
    }

    override fun onDestroy() {
        otpViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onFailed(msg: String, error: Int, status: Int) {
        //super.onFailed(msg, error, status)
        Log.e("OTPFragment", "onFailed: msg \t $msg")
        Log.e("OTPFragment", "onFailed: error \t $error")
        if (status == Constants.ADMIN_BLOCK_USER_CODE) {
            //dialogAdminBlockUser()
            addFragment(
                AdminBlockUserFragment(),
                Constants.ACCOUNT_SETTINGS_FRAGMENT
            )
        } else {
            showMsg(msg.toString())
        }
    }
}