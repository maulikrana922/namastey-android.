package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
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

        var datePickerDialog: SingleDateAndTimePickerDialog.Builder? = null
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

        datePickerDialog = SingleDateAndTimePickerDialog.Builder(context).apply {
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

        datePickerDialog!!.displayListener { picker ->
            var view = picker?.rootView
            if (view != null) {
                val buttonOk =
                    view.findViewById(com.github.florent37.singledateandtimepicker.R.id.buttonOk) as TextView
                buttonOk.text = "Done"
            }
        }
        datePickerDialog!!.listener { date: Date? ->
            val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.ENGLISH)

            calendar.time = date

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            tvDOB.text = sdf.format(date!!.time)
            }
        datePickerDialog!!.display()

    }

//    private fun displayMessage(toDisplay: String) {
//        Toast.makeText(activity, toDisplay, Toast.LENGTH_SHORT).show()
//    }

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