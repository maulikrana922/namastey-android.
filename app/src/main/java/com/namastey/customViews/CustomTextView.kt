package com.namastey.customViews

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import com.namastey.R
import com.namastey.utils.Constants

class CustomTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private val mTypefaces = SparseArray<Typeface>(16)

    init {
        setTypeface(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs) {
        Log.isLoggable("Button", defStyle)
        setTypeface(context, attrs)
    }

    private fun setTypeface(context: Context, attrs: AttributeSet) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)

        val typefaceValue = values.getInt(R.styleable.CustomTextView_typeface, 0)
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
//            Constants.MULI_REGULAR -> Typeface.createFromAsset(context.assets, "Muli-Regular.ttf")

//            Constants.BAHNSCHRIFT_LIGHT -> Typeface.createFromAsset(context.assets, "Muli-Regular.ttf")

            Constants.MULI_REGULAR -> Typeface.createFromAsset(context.assets, "Muli-SemiBold.ttf")

            else -> Typeface.createFromAsset(context.assets, "Muli-Regular.ttf")
        }
    }
}