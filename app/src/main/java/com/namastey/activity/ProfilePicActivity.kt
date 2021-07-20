package com.namastey.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityProfilePicBinding
import com.namastey.viewModel.BaseViewModel

class ProfilePicActivity : BaseActivity<ActivityProfilePicBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic)
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
    fun onClickBack(view: View) {
        onBackPressed()
    }
    fun onClickContinue(view: View) {
        openActivity(this@ProfilePicActivity, LanguageActivity())
    }

    fun onclickSkip(view: View) {
        openActivity(this@ProfilePicActivity, LanguageActivity())
    }
}