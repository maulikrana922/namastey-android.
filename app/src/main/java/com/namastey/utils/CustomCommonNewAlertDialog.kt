package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.namastey.R
import kotlinx.android.synthetic.main.dialog_common_new_alert.*

abstract class CustomCommonNewAlertDialog(
    private val activity: Activity,
    private val username: String,
    private val msg: String,
    private val profilePic: String,
    private val posBtnName: String,
    private val cancelBtnName: String
) : Dialog(activity, R.style.MyDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_common_new_alert)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        tvAlertMsg.text = msg
        btnAlertOk.text = posBtnName
        btnCancel.text = cancelBtnName
        tvAlertUsername.text = username
        GlideLib.loadImage(activity, ivProfileImage, profilePic)

        if (msg == activity.getString(R.string.msg_report_user))
            tvAlertMsg.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_flage
                ), null, null, null
            )
        else if (msg == activity.getString(R.string.msg_block_user)) tvAlertMsg.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(
                activity,
                R.drawable.ic_block_new
            ), null, null, null
        )

        btnAlertOk.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }
        btnCancel.setOnClickListener { v ->
            dismiss()
            onBtnClick(v.id)
        }

    }

    abstract fun onBtnClick(id: Int)
}