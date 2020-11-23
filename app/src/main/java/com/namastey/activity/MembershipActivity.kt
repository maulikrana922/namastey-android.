package com.namastey.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MembershipDialogSliderAdapter
import com.namastey.adapter.MembershipSliderAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMembershipBinding
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BaseViewModel
import com.namastey.viewModel.MembershipViewModel
import kotlinx.android.synthetic.main.activity_membership.*
import kotlinx.android.synthetic.main.dialog_membership.view.*
import javax.inject.Inject


class MembershipActivity : BaseActivity<ActivityMembershipBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityMembershipBinding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel

    lateinit var arrayList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_membership)
        getActivityComponent().inject(this)

        membershipViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MembershipViewModel::class.java)
        activityMembershipBinding = bindViewData()
        activityMembershipBinding.viewModel = membershipViewModel
        initData()
    }


    override fun getViewModel() = membershipViewModel

    override fun getLayoutId() = R.layout.activity_membership

    override fun getBindingVariable() = BR.viewModel

    fun initData() {
        arrayList = ArrayList()
        arrayList.clear()
        for (i in 0 until 5)
            arrayList.add("test $i")

        vpSlide.adapter = MembershipSliderAdapter(this@MembershipActivity, arrayList)
        tlIndicator.setupWithViewPager(vpSlide, true)

        btnCreateAlbumNext.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MembershipActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_membership, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)
        manageVisiblity(dialogView)
        viewpager.adapter = MembershipDialogSliderAdapter(this@MembershipActivity, arrayList)
        tabview.setupWithViewPager(viewpager, true)

    }

    /*Select membership plan*/
    fun manageVisiblity(view: View) {
        val conTwel = view.findViewById<ConstraintLayout>(R.id.conTwel)
        val conSix = view.findViewById<ConstraintLayout>(R.id.conSix)
        val conOne = view.findViewById<ConstraintLayout>(R.id.conOne)

        conOne.setOnClickListener {
            view.tvOnemonth.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvOnemonthText.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvOnemonthText1.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.viewBgOneColor.setBackgroundColor(resources.getColor(R.color.white))
            view.tvMostpopular.visibility = View.VISIBLE
            view.viewSelected.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvSixmonth.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvSixtext1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvSixText2.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwel.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwelText1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwelText2.setTextColor(resources.getColor(R.color.colorDarkGray))
        }

        conSix.setOnClickListener {
            view.tvSixmonth.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvSixtext1.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvSixText2.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvBgSixColor.setBackgroundColor(resources.getColor(R.color.white))
            view.tvMostpopularSix.visibility = View.VISIBLE
            view.viewSelectedSix.visibility = View.VISIBLE

            view.viewBgOneColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvOnemonthText.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvOnemonthText1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwel.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwelText1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvTwelText2.setTextColor(resources.getColor(R.color.colorDarkGray))
        }

        conTwel.setOnClickListener {
            view.tvTwel.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvTwelText1.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.tvTwelText2.setTextColor(resources.getColor(R.color.colorBlueLight))
            view.viewBgTwelColor.setBackgroundColor(resources.getColor(R.color.white))
            view.tvMostpopularTwel.visibility = View.VISIBLE
            view.viewSelectedTwel.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgOneColor.setBackgroundColor(resources.getColor(R.color.colorLightPink))
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvOnemonthText.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvOnemonthText1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvSixmonth.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvSixtext1.setTextColor(resources.getColor(R.color.colorDarkGray))
            view.tvSixText2.setTextColor(resources.getColor(R.color.colorDarkGray))
        }
    }
}