package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_boost_show.*

abstract class CustomBoostDialog(
    activity: Activity,
    private val boost:Int
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_boost_show)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertUsername.text="You have $boost Boosts"
        btnUseBoost.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
        btnNotNow.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
    }

    abstract fun onBtnClick(id: Int)
}