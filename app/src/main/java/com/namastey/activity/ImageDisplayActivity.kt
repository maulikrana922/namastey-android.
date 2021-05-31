package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.namastey.R
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.activity_image_display.*

class ImageDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        if (intent.hasExtra("imageUrl")) {
            val url = intent.getStringExtra("imageUrl")
            GlideLib.loadImage(this@ImageDisplayActivity, ivDisplay, url!!)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)    }

    fun onClickBack(view: View) {
        onBackPressed()
    }
}