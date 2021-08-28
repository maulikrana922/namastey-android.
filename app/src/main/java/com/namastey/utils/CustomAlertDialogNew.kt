package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_alert_new.*

abstract class CustomAlertDialogNew(
    activity: Activity,
    private val msg: String,
    private val posBtnName: String
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_alert_new)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertMsg.text = msg
        btnDone.text = posBtnName
        btnDone.visibility = if (!TextUtils.isEmpty(posBtnName)) View.VISIBLE else View.GONE

        btnDone.setOnClickListener { v ->
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



