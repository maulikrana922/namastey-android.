package com.namastey.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityNameBinding
import com.namastey.model.ErrorBean
import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.activity_name.*
import java.util.*
import javax.inject.Inject

class NameActivity : BaseActivity<ActivityNameBinding>(), ProfileBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var profileBasicViewModel: ProfileBasicViewModel
    private lateinit var binding: ActivityNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        binding = bindViewData()
        binding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {

        edtUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 5) {
                    Log.d("Username: ", "username :" + s.toString())
                    profileBasicViewModel.checkUniqueUsername(s.toString())
                } else {
                    tvUniqueNameError.visibility = View.GONE
                    edtUsername.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
        TODO("Not yet implemented")
    }

    override fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessUniqueName(msg: String) {
        tvUniqueNameError.visibility = View.GONE
        edtUsername.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_tick_square,
            0
        )
    }

    override fun onFailedUniqueName(error: ErrorBean?) {
        Log.e("TAG", "onFailedUniqueName: Error: \t ${error!!.user_name}")
        edtUsername.requestFocus()
        edtUsername.inputType = InputType.TYPE_CLASS_TEXT
        edtUsername.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0
        )
        tvUniqueNameError.visibility = View.VISIBLE
        tvUniqueNameError.text = error.user_name
    }

    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.activity_name

    override fun getBindingVariable() = BR.viewModel

    fun onClickNameBack(view: View) {
        onBackPressed()
    }

    fun onClickContinue(view: View) {
        when {
            TextUtils.isEmpty(edtFullName.text.toString().trim()) -> {
                showMsg(getString(R.string.empty_full_name))
            }
            TextUtils.isEmpty(edtUsername.text.toString().trim()) -> {
                showMsg(getString(R.string.empty_username))
            }
            edtUsername.text.toString().trim().length < 5 -> {
                showMsg(getString(R.string.empty_username_minimum))
            }
            else -> {
                if (tvUniqueNameError.visibility == View.GONE) {
                    sessionManager.setStringValue(
                        edtFullName.text.toString().trim(),
                        Constants.KEY_CASUAL_NAME
                    )
                    sessionManager.setStringValue(
                        edtUsername.text.toString().trim(),
                        Constants.KEY_MAIN_USER_NAME
                    )
                    openActivity(this@NameActivity, GenderActivity())
                }
            }
        }
    }
}