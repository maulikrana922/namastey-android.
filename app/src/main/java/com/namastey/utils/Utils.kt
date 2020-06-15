package com.namastey.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
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

    fun rectangleShapeBorder(v: View, borderColor: Int) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
//        shape.setColor(backgroundColor)
        shape.setStroke(3, borderColor)
        v.background = shape
    }

    fun rectangleShapeGradient(v: View, startColor: Int, endColor: Int) {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(
                startColor,
                endColor
            )
        )

        gd.shape = GradientDrawable.RECTANGLE
        gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
        v.background = gd
    }
}
