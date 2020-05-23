package com.namastey.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun hideKeyboard(context: Activity) {
        // Check if no view has focus
        val view = context.currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun convertDateToAPIFormate(date: String): String {
        val parser = SimpleDateFormat(Constants.DATE_FORMATE_API, Locale.getDefault())
        val formatter = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.getDefault())
        if (!TextUtils.isEmpty(date))
            return parser.format(formatter.parse(date))
        else
            return date
    }

}
