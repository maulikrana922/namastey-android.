package com.namastey.activity

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.namastey.R
import kotlinx.android.synthetic.main.activity_banned_user.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ActivityBannedUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banned_user)

        tvTermsOfUse.makeLinks(
            Pair(getString(R.string.terms_if_use), View.OnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(getString(R.string.tv_term_link))
                startActivity(browserIntent)
            }))
    }


    /**
     * To make terms and privacy policy clickable.
     */
    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
//                    textPaint.color = Color.BLACK
                    textPaint.typeface = Typeface.create(Typeface.createFromAsset(context.assets, "DMSans-Bold.ttf"), Typeface.BOLD)
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
//                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}