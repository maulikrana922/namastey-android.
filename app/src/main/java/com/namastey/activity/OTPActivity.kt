package com.namastey.activity

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
import com.namastey.viewModel.OTPViewModel
import kotlinx.android.synthetic.main.activity_otp.*
import javax.inject.Inject


class OTPActivity : BaseActivity<ActivityOtpBinding>(), OTPView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

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
        openActivity(this@OTPActivity, EmailActivity())
    }

    override fun onSuccessResponse(user: User) {
        TODO("Not yet implemented")
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
    
    fun onClickOtpBack(view: View){
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }
}