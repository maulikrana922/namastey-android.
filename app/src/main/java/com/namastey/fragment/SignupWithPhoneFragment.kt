package com.namastey.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ProfileActivity
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSignupWithPhoneBinding
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignupWithPhoneView
import com.namastey.utils.*
import com.namastey.viewModel.SignupWithPhoneModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.fragment_signup_with_phone.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


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
    private var isFromProfile = false
    override fun getViewModel() = signupWithPhoneModel
    private var isFirstTime = false

    override fun getLayoutId() = R.layout.fragment_signup_with_phone

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(isFromProfile: Boolean) =
            SignupWithPhoneFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isFromProfile", isFromProfile)
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
        if (arguments != null && arguments!!.containsKey("isFromProfile")) {
            isFromProfile = arguments!!.getBoolean("isFromProfile", false)
        }
        signupWithPhoneModel.getCountry()

    }

    override fun onCloseSignup() {
        Utils.hideKeyboard(requireActivity())
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
            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

            if (sessionManager.getLoginType().equals(Constants.EMAIL))
                jsonObject.addProperty(Constants.EMAIL, edtEmailPhone.text.toString().trim())
//                signupWithPhoneModel.sendOTP("", edtEmailPhone.text.toString().trim(), false)
            else {
                if (country != null) {
                    countryCode =
                        country.phonecode.toString()
//                countryCode =
//                    spinnerPhoneCode.selectedItem.toString()
                }
                jsonObject.addProperty(
                    Constants.MOBILE,
                    "+" + countryCode + edtEmailPhone.text.toString().trim()
                )
//                signupWithPhoneModel.sendOTP(
//                    "+" + countryCode + edtEmailPhone.text.toString().trim(),
//                    "",
//                    true
//                )
            }

            if (isFromProfile) {
                jsonObject.addProperty(Constants.IS_GUEST, 1)
                jsonObject.addProperty(Constants.USER_UNIQUEID, sessionManager.getUserUniqueId())
            }
            signupWithPhoneModel.sendOTP(jsonObject)
        }
    }

    override fun onClickCountry() {
        Utils.hideKeyboard(activity!!)
        if (isFromProfile) {
            (activity as ProfileActivity).addFragmentChild(
                childFragmentManager,
                CountryFragment.getInstance(
                    countryList
                ),
                Constants.COUNTRY_FRAGMENT
            )
        } else {
            (activity as SignUpActivity).addFragmentChild(
                childFragmentManager,
                CountryFragment.getInstance(
                    countryList
                ),
                Constants.COUNTRY_FRAGMENT
            )
        }
    }

    override fun onSuccessResponse(user: User) {
        sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        edtEmailPhone.text!!.clear()
        Utils.hideKeyboard(requireActivity())

        if (isFromProfile) {
            (activity as ProfileActivity).addFragment(
                OTPFragment.getInstance(
                    sessionManager.getUserPhone(),
                    sessionManager.getUserEmail(),
                    isFromProfile
                ),
                Constants.OTP_FRAGMENT
            )
        } else {
            (activity as SignUpActivity).addFragment(
                OTPFragment.getInstance(
                    sessionManager.getUserPhone(),
                    sessionManager.getUserEmail(),
                    isFromProfile
                ),
                Constants.OTP_FRAGMENT
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onGetCountry(countryList: ArrayList<Country>) {

        this.countryList = countryList

        if (countryList.size > 0) {
            country = countryList[0]  // default if location not found

            var longitude = 0.0
            var latitude = 0.0
            var finder: LocationFinder = LocationFinder(activity!!)
            if (finder.canGetLocation()) {
                if (finder.getCurrentLocation() != null) {

                    latitude = finder.getCurrentLocation()!!.latitude
                    longitude = finder.getCurrentLocation()!!.longitude
                    Log.d("Location", "location :$latitude  $longitude")
                    if (!isFirstTime) {
                        isFirstTime = true
                        val gcd = Geocoder(activity, Locale.getDefault())
                        val addresses: List<Address> =
                            gcd.getFromLocation(latitude, longitude, 1)

                        country = if (addresses != null && addresses.isNotEmpty()) {
                            val countryCode: String = addresses[0].countryCode
                            Log.d("Location : ", countryCode)

                            countryList.find { it.sortname == countryCode } ?: countryList[0]

                        } else {
                            countryList[0]
                        }

                        tvCountrySelected.text = country.sortname + " " + country.phonecode
                    }
                }
            } else {
                object : CustomAlertDialog(
                    activity!!,
                    resources.getString(R.string.gps_disable_message), getString(R.string.go_to_settings), getString(R.string.cancel)
                ) {
                    override fun onBtnClick(id: Int) {
                        when(id){
                            btnPos.id ->{
                                val intent =
                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(intent)
                            }
                            btnNeg.id ->{
                                dismiss()
                            }
                        }
                    }
                }.show()

//                finder.showSettingsAlert();
            }

            tvCountrySelected.text = country.sortname + " " + country.phonecode

        }
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