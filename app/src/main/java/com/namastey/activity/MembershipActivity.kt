package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MembershipDialogSliderAdapter
import com.namastey.adapter.MembershipSliderAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMembershipBinding
import com.namastey.model.MembershipPriceBean
import com.namastey.model.MembershipSlide
import com.namastey.uiView.MemberShipView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MembershipViewModel
import kotlinx.android.synthetic.main.activity_membership.*
import kotlinx.android.synthetic.main.dialog_membership.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MembershipActivity : BaseActivity<ActivityMembershipBinding>(), MemberShipView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityMembershipBinding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var membershipViewList = ArrayList<MembershipPriceBean>()
    private var selectedMonths = 1

    override fun getViewModel() = membershipViewModel

    override fun getLayoutId() = R.layout.activity_membership

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        membershipViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MembershipViewModel::class.java)
        activityMembershipBinding = bindViewData()
        activityMembershipBinding.viewModel = membershipViewModel
        initData()

        membershipViewModel.getMembershipPriceList()
    }

    private fun initData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes),
                getString(R.string.do_not_want_to_wait_slider),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.swipe_around_the_world),
                getString(R.string.passport_to_anywhere),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._5_free_super_message),
                getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.see_who_like_you),
                getString(R.string.month_with_them_instantly),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )

        vpSlide.adapter =
            MembershipSliderAdapter(this@MembershipActivity, membershipSliderArrayList)
        tlIndicator.setupWithViewPager(vpSlide, true)
        val timer = Timer()
        timer.scheduleAtFixedRate(SliderTimer(), 4000, 6000)
    }

    private fun showCustomDialog(position: Int) {
        selectedMonths = 1
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
        manageVisibility(dialogView)
        viewpager.adapter =
            MembershipDialogSliderAdapter(this@MembershipActivity, membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position
        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    /*Select membership plan*/
    private fun manageVisibility(view: View) {
        val constHigh = view.findViewById<ConstraintLayout>(R.id.constHigh)
        val constMedium = view.findViewById<ConstraintLayout>(R.id.constMedium)
        val constLow = view.findViewById<ConstraintLayout>(R.id.constLow)

        for (data in membershipViewList) {
            val membershipType = data.membership_type
            val price = data.price
            val discount = data.discount_pr

//            Log.e("MembershipActivity", "numberOfBoost: \t $membershipType")
//            Log.e("MembershipActivity", "price: \t $price")
//            Log.e("MembershipActivity", "discount: \t $discount")

            if (membershipType == 0) {
                view.tvTextLowEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
            }

            if (membershipType == 1) {
                view.tvTextMediumEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))
            }

            if (membershipType == 2) {
                view.tvTextHighEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))
            }
        }

        constLow.setOnClickListener {
            selectedMonths = 0
            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBlueLight
                )
            )
            view.viewBgLow.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            // view.tvOfferLow.visibility = View.VISIBLE
            view.viewSelectedLow.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
        }

        constMedium.setOnClickListener {
            selectedMonths = 1
            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBlueLight
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBlueLight
                )
            )
            view.viewBgMedium.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvOfferMedium.visibility = View.VISIBLE
            view.viewSelectedMedium.visibility = View.VISIBLE

            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
        }

        constHigh.setOnClickListener {
            selectedMonths = 2
            view.tvTextHigh.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTextBoostHigh.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBlueLight
                )
            )
            view.viewBgHigh.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvOfferHigh.visibility = View.VISIBLE
            view.viewSelectedHigh.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostLow.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
            view.tvTextMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextBoostMedium.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
            )
        }
    }

    fun onClickContinue(view: View){
        Log.d("Membership :", "Selected month ".plus(selectedMonths))
        Log.d("Membership :", "Selected month Price ".plus(membershipViewList[selectedMonths].price))

    }
    fun onClickMembershipBack(view: View) {
        onBackPressed()
    }

    fun onClickLike(view: View) {
        if (membershipViewList.size != 0)
            showCustomDialog(1)
    }

    fun onClickMessage(view: View) {
        if (membershipViewList.size != 0)
            showCustomDialog(3)
    }

    fun onClickBoost(view: View) {
        if (membershipViewList.size != 0)
            showCustomDialog(0)
    }

    fun onClickPassport(view: View) {
        if (membershipViewList.size != 0)
            showCustomDialog(2)
    }

    fun onClickYouLike(view: View) {
        if (membershipViewList.size != 0)
            showCustomDialog(4)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
        this.membershipViewList = membershipView
    }

    override fun onDestroy() {
        membershipViewModel.onDestroy()
        super.onDestroy()
    }

    inner class SliderTimer : TimerTask() {
        override fun run() {
            this@MembershipActivity.runOnUiThread(Runnable {
                if (vpSlide.currentItem < membershipSliderArrayList.size - 1) {
                    vpSlide.currentItem = vpSlide.currentItem + 1
                } else {
                    vpSlide.currentItem = 0
                }
            })
        }
    }

}