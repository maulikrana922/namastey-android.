package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.DashboardActivity
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
            sessionManager.getFirebaseToken()//"" // Todo: add firebase token
        )

    }

    override fun onSuccessResponse(user: User) {
        Utils.hideKeyboard(requireActivity())
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        sessionManager.setGuestUser(false)
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
            if (isFromDashboard)   // Open dashboard activity
                openActivity(requireActivity(), DashboardActivity())
            else
                openActivity(requireActivity(), ProfileBasicInfoActivity())
        } else {
            if (isRegister == 1) {
                ivSplashBackground.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(activity, DashboardActivity::class.java))
                    activity!!.finish()
                }, 1000)
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

    }

    override fun onDestroy() {
        otpViewModel.onDestroy()
        super.onDestroy()
    }
}