package com.namastey.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.databinding.ActivityBirthdayBinding
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_birthday.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_select_gender.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BirthdayActivity : BaseActivity<ActivityBirthdayBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    var datePickerDialog: SingleDateAndTimePickerDialog.Builder? = null
    private lateinit var calendar: Calendar
    private var isDateSelected = false
    private var year = 0
    var month = 0
    var day = 0
    private var distance = ""
    private var minAge = ""
    private var maxAge = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthday)

        initData()
    }

    fun initData() {
        sessionManager = SessionManager(this)
        val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY)

        calendar = Calendar.getInstance()
        if (isDateSelected) {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
        } else {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }
        tvSelectBirthday.text = sdf.format(calendar.time)

        rangeDistance.setOnSeekbarChangeListener { value ->
            tvDistanceDisplay.text = String.format(getString(R.string.distance_msg_1), value)
        }

        rangeDistance.setOnSeekbarFinalValueListener { value ->
            val jsonObject = JsonObject()
            distance = value.toString()
            sessionManager.setStringValue(distance, Constants.DISTANCE)

        }
        rangeAge.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvAgeDisplay.text =
                String.format(getString(R.string.age_message_value), minValue, maxValue)
        }

        rangeAge.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Log.d("min max:", "$minValue $maxValue")
            minAge = minValue.toString()
            maxAge = maxValue.toString()

        }
    }

    private fun showDatePickerDialog() {

        datePickerDialog = SingleDateAndTimePickerDialog.Builder(this@BirthdayActivity).apply {
            bottomSheet()
            curved()
            mainColor(Color.BLACK)
            displayMinutes(false)
            displayHours(false)
            displayDays(false)
            displayMonth(true)
            displayYears(true)
            displayDaysOfMonth(true)
        }

        if (isDateSelected)
            datePickerDialog!!.defaultDate(calendar.time)

        datePickerDialog!!.displayListener { picker ->
            val view = picker?.rootView
            if (view != null) {
                val buttonOk =
                    view.findViewById(com.github.florent37.singledateandtimepicker.R.id.buttonOk) as TextView
                buttonOk.text = getString(R.string.done)
            }
        }
        datePickerDialog!!.listener { date: Date? ->
            val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.ENGLISH)

            calendar.time = date

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            isDateSelected = true
            tvSelectBirthday.text = sdf.format(date!!.time)
        }
        datePickerDialog!!.maxDateRange(Date(System.currentTimeMillis()))
        datePickerDialog!!.display()

    }

    override fun getViewModel(): BaseViewModel {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    fun onClickSelectDate(view: View) {
        showDatePickerDialog()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBirthdayBack(view: View) {
        onBackPressed()
    }

    private fun ageDifferent(): Boolean {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age

        return ageInt >= 18
    }

    fun onClickContinue(view: View) {
        if (ageDifferent()) {
            sessionManager.setStringValue(maxAge, Constants.KEY_AGE_MAX)
            sessionManager.setStringValue(minAge, Constants.KEY_AGE_MIN)
            sessionManager.setStringValue(
                tvSelectBirthday.text.toString().trim(),
                Constants.KEY_BIRTH_DAY
            )
            openActivity(this@BirthdayActivity, EducationActivity())
        } else {
            object : CustomAlertDialog(
                this@BirthdayActivity,
                resources.getString(R.string.date_of_birth_message), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }

}