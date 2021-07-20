package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityNotInvitedBinding
import com.namastey.viewModel.BaseViewModel

class NotInvitedActivity : BaseActivity<ActivityNotInvitedBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_invited)
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
        openSignup()
    }
    fun onClickGotInvite(view: View) {
        openSignup()
    }

    private fun openSignup(){
        val intent = Intent(this, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        openActivity(intent)
    }
}