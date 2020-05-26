package com.namastey.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
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
import com.namastey.utils.Utils
import com.namastey.viewModel.SignupWithPhoneModel
import kotlinx.android.synthetic.main.fragment_signup_with_phone.*
import javax.inject.Inject


class SignupWithPhoneFragment : BaseFragment<FragmentSignupWithPhoneBinding>(),
    SignupWithPhoneView, CountryFragment.OnCountrySelectionListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentSignupWithPhoneBinding: FragmentSignupWithPhoneBinding
    private lateinit var signupWithPhoneModel: SignupWithPhoneModel
    private lateinit var layoutView: View
    private var countryList = ArrayList<Country>()
    private lateinit var country: Country

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
        sessionManager.setLoginType(Constants.MOBILE)


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
        var countryCode = ""
        if (signupWithPhoneModel.isValidPhone(edtEmailPhone.text.toString().trim())) {
            if (sessionManager.getLoginType().equals(Constants.EMAIL))
                signupWithPhoneModel.sendOTP("", edtEmailPhone.text.toString().trim(), false)
            else {
                if (country != null) {
                    countryCode =
                        country.phonecode.toString()
//                countryCode =
//                    spinnerPhoneCode.selectedItem.toString()
                }

                signupWithPhoneModel.sendOTP(
                    "+" + countryCode + edtEmailPhone.text.toString().trim(),
                    "",
                    true
                )
            }
        }
    }

    override fun onClickCountry() {
        Utils.hideKeyboard(activity!!)
        (activity as SignUpActivity).addFragmentChild(
            childFragmentManager,
            CountryFragment.getInstance(
                countryList
            ),
            Constants.COUNTRY_FRAGMENT
        )
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

        this.countryList = countryList

        if (countryList.size > 0)
            tvCountrySelected.text = countryList[0].sortname + " " + countryList[0].phonecode

//        val aa =
//            ArrayAdapter(activity, R.layout.spinner_item, listOfCountry)
//
        if (sessionManager.getLoginType().equals(Constants.MOBILE)) {
            viewDivider.visibility = View.VISIBLE
            tvCountrySelected.visibility = View.VISIBLE
        }
//
//        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//        spinnerPhoneCode!!.setAdapter(aa)
    }

    private fun selectPhone() {
        sessionManager.setLoginType(Constants.MOBILE)
        edtEmailPhone.inputType = InputType.TYPE_CLASS_NUMBER
        tvCountrySelected.visibility = View.VISIBLE
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
        tvCountrySelected.visibility = View.GONE
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

    /**
     * Click on select country get selected country model
     */
    override fun onCountrySelectionSet(country: Country) {
        childFragmentManager.popBackStack()
        this.country = country
        tvCountrySelected.text = country.sortname + " " + country.phonecode
    }

}