package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.namastey.R
import com.namastey.databinding.ActivityNotInvitedBinding
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_not_invited.*

class NotInvitedActivity : BaseActivity<ActivityNotInvitedBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_invited)

        tvWebsite.makeLinks(Pair("Website", View.OnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.tv_early_access))
            startActivity(browserIntent)
        }))
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

    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.color = Color.RED
                    textPaint.isUnderlineText = true
                    textPaint.typeface = Typeface.create(Typeface.createFromAsset(context.assets, "DMSans-Bold.ttf"), Typeface.BOLD)

                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
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