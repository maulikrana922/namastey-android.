package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_alert.*

abstract class CustomAlertDialog(
    activity: Activity,
    private val msg: String,
    private val posBtnName: String,
    private val negBtnName: String
) : Dialog(activity, R.style.AppTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_alert)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertMsg.text = msg
        btnPos.text = posBtnName
        btnNeg.text = negBtnName
        btnPos.visibility = if (!TextUtils.isEmpty(posBtnName)) View.VISIBLE else View.GONE
        btnNeg.visibility = if (!TextUtils.isEmpty(negBtnName)) View.VISIBLE else View.GONE

        btnPos.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
        btnNeg.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
        btnView.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
    }

    abstract fun onBtnClick(id: Int)
}