package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSignupWithPhoneBinding
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignupWithPhoneView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SignupWithPhoneModel
import kotlinx.android.synthetic.main.fragment_signup_with_phone.*
import javax.inject.Inject

class SignupWithPhoneFragment : BaseFragment<FragmentSignupWithPhoneBinding>(),
    SignupWithPhoneView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentSignupWithPhoneBinding: FragmentSignupWithPhoneBinding
    private lateinit var signupWithPhoneModel: SignupWithPhoneModel
    private lateinit var layoutView: View
    val listOfCountry = ArrayList<String>()

    override fun getViewModel() = signupWithPhoneModel

    override fun getLayoutId() = R.layout.fragment_signup_with_phone

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            SignupWithPhoneFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }

    private fun setupViewModel() {
        signupWithPhoneModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignupWithPhoneModel::class.java)
        signupWithPhoneModel.setViewInterface(this)
        fragmentSignupWithPhoneBinding = getViewBinding()
        fragmentSignupWithPhoneBinding.viewModel = signupWithPhoneModel
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

        signupWithPhoneModel.getCountry()

    }

    override fun onCloseSignup() {
        activity!!.onBackPressed()
    }

    override fun onClickPhone() {
        selectPhone()
    }

    override fun onClickEmail() {
        selectEmail()
    }

    override fun onClickNext() {
        if (spinnerPhoneCode.selectedItem != null){
            var phoneNumber =
                spinnerPhoneCode.selectedItem.toString() + " " + edtEmailPhone.text.toString()
        }
        if (sessionManager.getLoginType().equals(Constants.EMAIL))
            signupWithPhoneModel.sendOTP("", edtEmailPhone.text.toString(), false)
        else
            signupWithPhoneModel.sendOTP(edtEmailPhone.text.toString(), "", true)

    }

    override fun onSuccessResponse(user: User) {
        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        edtEmailPhone.text!!.clear()

        (activity as SignUpActivity).addFragment(
            OTPFragment.getInstance(
                sessionManager.getUserPhone(), sessionManager.getUserEmail()
            ),
            Constants.OTP_FRAGMENT
        )
    }

    override fun onGetCountry(countryList: ArrayList<Country>) {

        for ((index, value) in countryList.withIndex()) {
            listOfCountry.add(value.sortname + " " + value.phonecode)
        }
        val aa =
            ArrayAdapter(activity, R.layout.spinner_item, listOfCountry)

        viewDivider.visibility = View.VISIBLE
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerPhoneCode!!.setAdapter(aa)
    }

    private fun selectPhone() {
        sessionManager.setLoginType(Constants.MOBILE)
        edtEmailPhone.inputType = InputType.TYPE_CLASS_NUMBER
        spinnerPhoneCode.visibility = View.VISIBLE
        viewDivider.visibility = View.VISIBLE
        edtEmailPhone.hint = resources.getString(R.string.hint_phone)
        tvLabel.text = resources.getString(R.string.phone)
        tvPhoneSignup.background =
            ContextCompat.getDrawable(activity!!, R.drawable.rounded_bottom_left_red_solid)
        tvPhoneSignup.setTextColor(ContextCompat.getColor(activity!!, R.color.colorWhite))

        tvEmailSignup.background =
            ContextCompat.getDrawable(activity!!, R.drawable.rounded_top_right_pink_solid)
        tvEmailSignup.setTextColor(ContextCompat.getColor(activity!!, R.color.colorGray))
    }

    private fun selectEmail() {
        sessionManager.setLoginType(Constants.EMAIL)
        edtEmailPhone.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        spinnerPhoneCode.visibility = View.GONE
        viewDivider.visibility = View.GONE
        edtEmailPhone.hint = resources.getString(R.string.hint_email)
        tvLabel.text = resources.getString(R.string.email)
        tvEmailSignup.background =
            ContextCompat.getDrawable(activity!!, R.drawable.rounded_top_right_red_solid)
        tvEmailSignup.setTextColor(ContextCompat.getColor(activity!!, R.color.colorWhite))

        tvPhoneSignup.background =
            ContextCompat.getDrawable(activity!!, R.drawable.rounded_bottom_left_pink_solid)
        tvPhoneSignup.setTextColor(ContextCompat.getColor(activity!!, R.color.colorGray))
    }

    override fun onDestroy() {
        signupWithPhoneModel.onDestroy()
        super.onDestroy()
    }
}