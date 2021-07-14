package com.namastey.activity

import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityEmailBinding
import com.namastey.viewModel.BaseViewModel

class EmailActivity : BaseActivity<ActivityEmailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)


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
        openActivity(this@EmailActivity, NameActivity())
    }
}