package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_delete.*

abstract class CustomAlertNewDialog(
    activity: Activity,
    private val msg: String,
    private val icons: Int,
    private val posBtnName: String,
    private val negBtnName: String
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_delete)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertMsg.text = msg
        btnConfirm.text = posBtnName
        btnDeleteCancel.text = negBtnName
        btnConfirm.visibility = if (!TextUtils.isEmpty(posBtnName)) View.VISIBLE else View.GONE
        btnDeleteCancel.visibility = if (!TextUtils.isEmpty(negBtnName)) View.VISIBLE else View.GONE

        ivIcons.setImageResource(icons)
        btnConfirm.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
        btnDeleteCancel.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
//        btnView.setOnClickListener { v ->
//            dismiss()
//            onBtnClick(v.id)
//        }
    }

    abstract fun onBtnClick(id: Int)
}