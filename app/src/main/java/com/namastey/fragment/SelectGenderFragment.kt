package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bruce.pickerview.popwindow.DatePickerPopWin
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectGenderBinding
import com.namastey.uiView.SelectGenderView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SelectGenderViewModel
import kotlinx.android.synthetic.main.fragment_select_gender.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class SelectGenderFragment : BaseFragment<FragmentSelectGenderBinding>(), SelectGenderView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentSelectGenderBinding: FragmentSelectGenderBinding
    private lateinit var selectGenderViewModel: SelectGenderViewModel
    private lateinit var layoutView: View
    private var year = 0;
    var month = 0;
    var day = 0
    private lateinit var calendar: Calendar
    override fun onClose() {
        activity!!.onBackPressed()
    }

    override fun onNext() {
        if (sessionManager.getUserGender().isNotEmpty()) {
            if (ageDifferent()) {
                (activity as SignUpActivity).addFragment(
                    VideoLanguageFragment.getInstance(
                        tvDOB.text.toString().trim()
                    ),
                    Constants.VIDEO_LANGUAG_EFRAGMENT
                )
            } else {
                object : CustomAlertDialog(
                    activity!!,
                    resources.getString(R.string.date_of_birth_message), getString(R.string.ok), ""
                ) {
                    override fun onBtnClick(id: Int) {
                        dismiss()
                    }
                }.show()
            }

        } else {
            object : CustomAlertDialog(
                activity!!,
                resources.getString(R.string.msg_select_gender), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }

    override fun getViewModel() = selectGenderViewModel

    override fun getLayoutId() = R.layout.fragment_select_gender

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            SelectGenderFragment().apply {
                arguments = Bundle().apply {
                    putString("user", title)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {
        val date = System.currentTimeMillis()
        sessionManager.setUserGender("")

        val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY)
        tvDOB.text = sdf.format(date)

        tvDOB.setOnClickListener(this)
        ivMale.setOnClickListener(this)
        ivFemale.setOnClickListener(this)

        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun setupViewModel() {
        selectGenderViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SelectGenderViewModel::class.java)
        selectGenderViewModel.setViewInterface(this)

        fragmentSelectGenderBinding = getViewBinding()
        fragmentSelectGenderBinding.viewModel = selectGenderViewModel
    }

    private fun showDatePickerDialog() {
//        val selectedDate = Calendar.getInstance()
//        val startDate = Calendar.getInstance()
//        startDate[2013, 0] = 1
//        val endDate = Calendar.getInstance()
//        endDate[2020, 11] = 1
//
//        var pvTime = TimePickerBuilder(activity,
//            OnTimeSelectListener { date, v ->
//                //callback
//                tvDOB.setText(date.time.toString())
//            })
//            .setType(booleanArrayOf(true, true, true, false, false, false)) // type of date
//            .setCancelText("Cancel")
//            .setSubmitText("Sure")
//            .setTitleSize(20)
//            .setTitleText("")
//            .setOutSideCancelable(false) // default is true
//            .isCyclic(true) // default is false
//            .setTitleColor(Color.WHITE)
//            .setSubmitColor(Color.GREEN)
//            .setCancelColor(Color.GRAY)
//            .setRangDate(startDate, endDate)
//            .setLabel("", "", "", "hours", "mins", "seconds")
//            .build()
//
//        pvTime.show()

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        val pickerPopWin =
            DatePickerPopWin.Builder(activity,
                DatePickerPopWin.OnDatePickedListener { year, month, day, dateDesc ->

                    val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.ENGLISH)
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, day)

                    this.year = year
                    this.month = month
                    this.day = day
                    tvDOB.text = sdf.format(calendar.time)
//                    Toast.makeText(
//                        activity,
//                        dateDesc,
//                        Toast.LENGTH_SHORT
//                    ).show()
                }).textConfirm("CONFIRM")
                .textCancel("CANCEL")
                .btnTextSize(16)
                .viewTextSize(28)
                .colorCancel(Color.parseColor("#999999"))
                .colorConfirm(Color.parseColor("#009900"))
                .minYear(1970)
                .maxYear(year + 1)
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .build()

        pickerPopWin.showPopWin(activity)


//        SingleDateAndTimePickerDialog.Builder(activity)
//            .displayMinutes(false)
//            .displayHours(false)
//            .displayDays(false)
//            .displayMonth(true)
//            .displayYears(true)
//            .displayDaysOfMonth(true)
//            .display()

//        dobPicker.visibility = View.VISIBLE
//        val changeListener =
//            SingleDateAndTimePicker.OnDateChangedListener { displayed: String?, date: Date? ->
////                display(
////                    displayed
////                )
//            }
//        dobPicker.addOnDateChangedListener(changeListener)


//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = c.get(Calendar.DAY_OF_MONTH)
//
//
//        val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//
////            val myFormat = "dd/MM/yyyy" // mention the format you need
//            val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.US)
//            c.set(Calendar.YEAR, year)
//            c.set(Calendar.MONTH, monthOfYear)
//            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//            tvDOB.text = sdf.format(c.time)
//
//        }, year, month, day)
//        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
//        dpd.show()
    }

    override fun onClick(v: View?) {
        when (v) {
            tvDOB -> {
                showDatePickerDialog()
            }
            ivMale -> {
//                ivMale.alpha = 0.6f
//                ivFemale.alpha = 1f
                ivMale.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
                ivFemale.setBackgroundResource(R.drawable.rounded_white_solid)
                sessionManager.setUserGender(Constants.Gender.male.name)
            }

            ivFemale -> {
//                ivMale.alpha = 1f
//                ivFemale.alpha = 0.6f
                ivFemale.setBackgroundResource(R.drawable.rounded_white_solid_red_border)
                ivMale.setBackgroundResource(R.drawable.rounded_white_solid)
                sessionManager.setUserGender(Constants.Gender.female.name)
            }
        }
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
}