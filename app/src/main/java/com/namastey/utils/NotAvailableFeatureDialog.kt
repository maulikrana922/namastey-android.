package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_boost_not_available.*
import kotlinx.android.synthetic.main.dialog_boosts.*

abstract class NotAvailableFeatureDialog(
    activity: Activity,
    private val title: String,
    private val msg: String,
    private val icons: Int
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_boost_not_available)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertMsg.text = msg
        tvTitle.text = title

        ivIcons.setImageResource(icons)
        btnAlertOk.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
    }

    abstract fun onBtnClick(id: Int)
}