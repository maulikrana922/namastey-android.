package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.databinding.ActivityGenderBinding
import com.namastey.utils.Utils
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_gender.*

class GenderActivity : BaseActivity<ActivityGenderBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        initData()
    }

    fun initData() {

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

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickGenderBack(view: View) {
        onBackPressed()
    }

    fun onClickSelectInterest(view: View) {
        when (view) {
            tvSelectInterest -> {
                if (groupSelectInterest.visibility == View.VISIBLE)
                    groupSelectInterest.visibility = View.GONE
                else
                    groupSelectInterest.visibility = View.VISIBLE
            }

            tvSelectMale -> {
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.men)
            }
            tvSelectFemale -> {
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.female)
            }
            tvSelectEveryone -> {
                groupSelectInterest.visibility = View.GONE
                tvSelectInterest.text = getString(R.string.everyone)
            }

            flMen -> {
                Utils.roundedCornerShape(flMen, "#F8F8F8", "#30B7D6")
                flFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flNonBinary.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }
            flFemale -> {
                Utils.roundedCornerShape(flFemale, "#F8F8F8", "#EB3EB0")
                flMen.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flNonBinary.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }
            flNonBinary -> {
                Utils.roundedCornerShape(flNonBinary, "#F8F8F8", "#9748FC")
                flMen.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
                flFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_white_solid_white_corner)
            }

            btnContinue ->{
                openActivity(this@GenderActivity, BirthdayActivity())
            }
        }
    }
}