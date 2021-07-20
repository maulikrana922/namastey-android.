package com.namastey.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.util.Util
import com.namastey.R
import com.namastey.databinding.ActivitySocialLinkBinding
import com.namastey.utils.Utils
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_social_link.*

class SocialLinkActivity : BaseActivity<ActivitySocialLinkBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_link)

        initUI()
    }

    private fun initUI(){
        Utils.rectangleShapeBorder(tvFacebook, ContextCompat.getColor(this,R.color.color_blue_facebook),true)
        Utils.rectangleShapeBorder(tvInstagram, ContextCompat.getColor(this,R.color.color_instagram),true)
        Utils.rectangleShapeBorder(tvTwitter, ContextCompat.getColor(this,R.color.color_twitter),true)
        Utils.rectangleShapeBorder(tvSpotify, ContextCompat.getColor(this,R.color.color_spotify),true)

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

    fun onClickContinue(view: View) {
        openActivity(this@SocialLinkActivity, AddVideoActivity())
    }

    override fun onBackPressed() {
        finishActivity()
    }
    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickSkip(view: View) {
        openActivity(this@SocialLinkActivity, AddVideoActivity())
    }
}