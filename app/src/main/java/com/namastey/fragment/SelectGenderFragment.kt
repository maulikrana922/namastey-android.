package com.namastey.fragment

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
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


class SelectGenderFragment : BaseFragment<FragmentSelectGenderBinding>(),SelectGenderView,View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentSelectGenderBinding: FragmentSelectGenderBinding
    private lateinit var selectGenderViewModel: SelectGenderViewModel
    private lateinit var layoutView: View

    override fun onClose() {
        activity!!.onBackPressed()
    }

    override fun onNext() {
        if (!sessionManager.getUserGender().equals("")){
            (activity as SignUpActivity).addFragment(
                VideoLanguageFragment.getInstance(
                    tvDOB.text.toString().trim()
                ),
                Constants.VIDEO_LANGUAG_EFRAGMENT
            )
        }else{
            object : CustomAlertDialog(
                activity!!,
                resources.getString(R.string.msg_select_gender), getString(R.string.ok), ""
            ){
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
    }

    private fun setupViewModel() {
        selectGenderViewModel = ViewModelProviders.of(this,viewModelFactory).get(SelectGenderViewModel::class.java)
        selectGenderViewModel.setViewInterface(this)

        fragmentSelectGenderBinding = getViewBinding()
        fragmentSelectGenderBinding.viewModel = selectGenderViewModel
    }

    private fun showDatePickerDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

//            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.US)
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            tvDOB.text = sdf.format(c.time)

        }, year, month, day)
        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    override fun onClick(v: View?) {
        when(v){
            tvDOB ->{
                showDatePickerDialog()
            }
            ivMale ->{
                ivMale.alpha = 0.6f
                ivFemale.alpha = 1f
                sessionManager.setUserGender(Constants.Gender.male.name)
            }

            ivFemale ->{
                ivMale.alpha = 1f
                ivFemale.alpha = 0.6f
                sessionManager.setUserGender(Constants.Gender.female.name)
            }
        }
    }
}