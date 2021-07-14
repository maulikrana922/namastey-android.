package com.namastey.activity

import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityNameBinding
import com.namastey.viewModel.BaseViewModel

class NameActivity : BaseActivity<ActivityNameBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)
    }

    override fun onBackPressed() {
        finishActivity()
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

    fun onClickNameBack(view: View) {
        onBackPressed()
    }

    fun onClickContinue(view: View) {
        openActivity(this@NameActivity, GenderActivity())
    }
}