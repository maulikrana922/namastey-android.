package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityOtpBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.OTPView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.OTPViewModel
import kotlinx.android.synthetic.main.activity_otp.*
import javax.inject.Inject


class OTPActivity : BaseActivity<ActivityOtpBinding>(), OTPView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityOtpBinding: ActivityOtpBinding
    private lateinit var otpViewModel: OTPViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        otpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(OTPViewModel::class.java)
        activityOtpBinding = bindViewData()
        activityOtpBinding.viewModel = otpViewModel

        initData()
    }

    private fun initData() {

        val edit =
            arrayOf<EditText>(edtOne, edtTwo, edtThree, edtFour, edtFive, edtSix)

        edtOne.addTextChangedListener(GenericTextWatcher(edtOne, edit))
        edtTwo.addTextChangedListener(GenericTextWatcher(edtTwo, edit))
        edtThree.addTextChangedListener(GenericTextWatcher(edtThree, edit))
        edtFour.addTextChangedListener(GenericTextWatcher(edtFour, edit))
        edtFive.addTextChangedListener(GenericTextWatcher(edtFive, edit))
        edtSix.addTextChangedListener(GenericTextWatcher(edtSix, edit))

    }

    override fun onCloseOtp() {
        TODO("Not yet implemented")
    }

    override fun onConfirm() {
        val otp = edtOne.text.toString().plus(edtTwo.text.toString()).plus(edtThree.text.toString())
            .plus(edtFour.text.toString())
            .plus(edtFive.text.toString()).plus(edtSix.text.toString())
        otpViewModel.verifyOTP(
            sessionManager.getUserPhone(),
            otp,
            Constants.ANDROID,
            sessionManager.getFirebaseToken()
        )
//        openActivity(this@OTPActivity, EmailActivity())
    }

    override fun onSuccessResponse(user: User) {
        Utils.hideKeyboard(this)
        sessionManager.setVerifiedUser(user.is_verified)
        sessionManager.setuserUniqueId(user.user_uniqueId)
        sessionManager.setUserId(user.user_id)
        sessionManager.setGuestUser(false)
        sessionManager.setIntegerValue(user.is_invited,Constants.KEY_IS_INVITED)
        sessionManager.setIntegerValue(user.purchase, Constants.KEY_IS_PURCHASE)
        if (user.is_completly_signup == 1) {
            sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
        } else {
            sessionManager.setBooleanValue(false, Constants.KEY_IS_COMPLETE_PROFILE)
        }

        if (user.is_register == 1) {
            if (user.is_invited == 1) {
                sessionManager.setStringValue(user.username,Constants.USERNAME)
                sessionManager.setStringValue(user.profile_pic, Constants.KEY_PROFILE_URL)
                val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                openActivity(intent)
            } else {
                openActivity(this, NotInvitedActivity())
            }
        } else {
            openActivity(this, EmailActivity())
        }
    }

    override fun getViewModel() = otpViewModel

    override fun getLayoutId() = R.layout.activity_otp

    override fun getBindingVariable() = BR.viewModel

    class GenericTextWatcher(private val view: View, private val editText: Array<EditText>) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text: String = editable.toString()
            when (view.getId()) {
                R.id.edtOne -> if (text.length == 1) editText[1].requestFocus()
                R.id.edtTwo -> if (text.length == 1) editText[2].requestFocus() else if (text.isEmpty()) editText[0].requestFocus()
                R.id.edtThree -> if (text.length == 1) editText[3].requestFocus() else if (text.isEmpty()) editText[1].requestFocus()
                R.id.edtFour -> if (text.length == 1) editText[4].requestFocus() else if (text.isEmpty()) editText[2].requestFocus()
                R.id.edtFive -> if (text.length == 1) editText[5].requestFocus() else if (text.isEmpty()) editText[3].requestFocus()
                R.id.edtSix -> if (text.isEmpty()) editText[4].requestFocus()
            }
        }

        override fun beforeTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
        override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
    }

    fun onClickOtpBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }
}