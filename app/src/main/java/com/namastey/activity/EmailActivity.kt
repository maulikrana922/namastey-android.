package com.namastey.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityEmailBinding
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_email.*
import javax.inject.Inject

class EmailActivity : BaseActivity<ActivityEmailBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        sessionManager = SessionManager(this)
    }

    override fun getViewModel(): BaseViewModel {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }
    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        finishActivity()
    }
    fun onClickEmailBack(view: View){
        onBackPressed()
    }

    fun onClickContinue(view: View) {
        if (!TextUtils.isEmpty(edtEmail.text.toString().trim())){
            sessionManager.setUserEmail(edtEmail.text.toString().toString())
            openActivity(this@EmailActivity, NameActivity())
        }else{
            showMsg(getString(R.string.msg_empty_email))
        }
    }
}