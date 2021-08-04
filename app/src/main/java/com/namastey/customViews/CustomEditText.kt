package com.namastey.customViews

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import com.namastey.R
import com.namastey.utils.Constants

class CustomEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private val mTypefaces = SparseArray<Typeface>(16)

    init {
        setTypeface(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs) {
        Log.isLoggable("Button", defStyle)
        setTypeface(context, attrs)
    }

    private fun setTypeface(context: Context, attrs: AttributeSet) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText)

        val typefaceValue = values.getInt(R.styleable.CustomEditText_typeface, 0)
        values.recycle()

        typeface = obtainTypeface(context, typefaceValue)
    }

    @Throws(IllegalArgumentException::class)
    private fun obtainTypeface(context: Context, typefaceValue: Int): Typeface? {
        var typeface: Typeface? = mTypefaces.get(typefaceValue)
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue)
            mTypefaces.put(typefaceValue, typeface)
        }
        return typeface
    }

    @Throws(IllegalArgumentException::class)
    private fun createTypeface(context: Context, typefaceValue: Int): Typeface {
        return when (typefaceValue) {
            Constants.MULI_REGULAR -> Typeface.createFromAsset(context.assets, "DMSans-Regular.ttf")

            Constants.MULI_LIGHT -> Typeface.createFromAsset(context.assets, "DMSans-Regular.ttf")

            Constants.MULI_BLACK -> Typeface.createFromAsset(context.assets, "DMSans-Regular.ttf")

            Constants.MULI_SEMI_BOLD -> Typeface.createFromAsset(context.assets, "DMSans-Bold.ttf")

            Constants.MULI_EXTRA_BOLD -> Typeface.createFromAsset(context.assets, "DMSans-Bold.ttf")

            else -> Typeface.createFromAsset(context.assets, "Muli-Regular.ttf")
        }
    }
}