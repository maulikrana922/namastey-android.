package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ArrayAdapter
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSignupWithPhoneBinding
import com.namastey.uiView.SignupWithPhoneView
import com.namastey.utils.Constants
import com.namastey.viewModel.SignupWithPhoneModel
import kotlinx.android.synthetic.main.fragment_signup_with_phone.*
import javax.inject.Inject

class SignupWithPhoneFragment : BaseFragment<FragmentSignupWithPhoneBinding>(),SignupWithPhoneView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentSignupWithPhoneBinding: FragmentSignupWithPhoneBinding
    private lateinit var signupWithPhoneModel: SignupWithPhoneModel
    private lateinit var layoutView: View
    var listOfCountry = arrayOf("IND +91", "UK +1", "AUS +61")

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
        val aa = ArrayAdapter.createFromResource(activity,R.array.countryCode,R.layout.spinner_item)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerPhoneCode!!.setAdapter(aa)
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
        (activity as SignUpActivity).addFragment(
            OTPFragment.getInstance(
                "mobilenumber"
            ),
            Constants.OTP_FRAGMENT
        )
    }

    override fun onSuccess() {

    }

    private fun selectPhone(){
        spinnerPhoneCode.visibility = View.VISIBLE
        viewDivider.visibility = View.VISIBLE
        edtEmailPhone.hint = resources.getString(R.string.hint_phone)
        tvLabel.text = resources.getString(R.string.phone)
        tvPhoneSignup.background = ContextCompat.getDrawable(activity!!,R.drawable.rounded_bottom_left_red_solid)
        tvPhoneSignup.setTextColor(ContextCompat.getColor(activity!!,R.color.colorWhite))

        tvEmailSignup.background = ContextCompat.getDrawable(activity!!,R.drawable.rounded_top_right_pink_solid)
        tvEmailSignup.setTextColor(ContextCompat.getColor(activity!!,R.color.colorGray))
    }

    private fun selectEmail(){
        spinnerPhoneCode.visibility = View.GONE
        viewDivider.visibility = View.GONE
        edtEmailPhone.hint = resources.getString(R.string.hint_email)
        tvLabel.text = resources.getString(R.string.email)
        tvEmailSignup.background = ContextCompat.getDrawable(activity!!,R.drawable.rounded_top_right_red_solid)
        tvEmailSignup.setTextColor(ContextCompat.getColor(activity!!,R.color.colorWhite))

        tvPhoneSignup.background = ContextCompat.getDrawable(activity!!,R.drawable.rounded_bottom_left_pink_solid)
        tvPhoneSignup.setTextColor(ContextCompat.getColor(activity!!,R.color.colorGray))
    }

}