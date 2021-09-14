package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_report_warning.*

abstract class ReportWarningDialog(
    activity: Activity
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_report_warning)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnPromise.setOnClickListener { v ->
            if (ckbreport.isChecked) {
                dismiss()
                onBtnClick(v.id)
            }
        }

    }

    abstract fun onBtnClick(id: Int)
}