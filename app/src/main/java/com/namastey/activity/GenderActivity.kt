package com.namastey.activity

import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityGenderBinding
import com.namastey.viewModel.BaseViewModel

class GenderActivity : BaseActivity<ActivityGenderBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)
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

    fun onClickGenderBack(view: View) {
        onBackPressed()
    }
}