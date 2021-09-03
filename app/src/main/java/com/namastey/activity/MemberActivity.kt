package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MemberDialogSliderAdapter
import com.namastey.adapter.MemberSliderAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMemberBinding
import com.namastey.model.MembershipPriceBean
import com.namastey.model.MembershipSlide
import com.namastey.uiView.MemberShipView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MembershipViewModel
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.dialog_member.view.*
import java.util.*
import javax.inject.Inject

class MemberActivity : BaseActivity<ActivityMemberBinding>(),
    PurchasesUpdatedListener, MemberShipView {

    private val TAG = "MembershipActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityMemberBinding: ActivityMemberBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var membershipViewList = ArrayList<MembershipPriceBean>()
    private var selectedMonths = 1
    private var subscriptionId = "000020"
    private var isselected:Int = 0




    override fun getViewModel() = membershipViewModel

    override fun getLayoutId() = R.layout.activity_member

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        membershipViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MembershipViewModel::class.java)
        activityMemberBinding = bindViewData()
        activityMemberBinding.viewModel = membershipViewModel
        initData()

    }

    private fun initData() {
//        if (intent.hasExtra("isFromAirport"))
//            isFromAirport = intent.getBooleanExtra("isFromAirport", false)
//
//        if (intent.hasExtra("isFromMatchProfile"))
//            isFromMatchProfile = intent.getBooleanExtra("isFromMatchProfile", false)

        setSliderData()

        vpSlide.adapter =
            MemberSliderAdapter(this@MemberActivity, membershipSliderArrayList)
        tlIndicator.setupWithViewPager(vpSlide, true)
        val timer = Timer()
        timer.scheduleAtFixedRate(SliderTimer(), 4000, 6000)

    }

    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.super_message),
                //resources.getString(R.string._1_boost_each_month),
                getString(R.string.express_your_feeling_freely_with_the_people_you_like),
                R.drawable.ic_super_mesage,
                0,
                "",
                "x5"
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.unlimited_likes),
                resources.getString(R.string.Do_not_like_to_wait_Go_Unlimited),
                R.drawable.ic_unlimited_likes,
                0,
                "",
                ""
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                //resources.getString(R.string.swipe_around_the_world),
                resources.getString(R.string.airport),
                resources.getString(R.string.travel_around_the_world_in_just_80_seconds),
                R.drawable.ic_airport,
                0,
                ""
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.boost),
                resources.getString(R.string.skip_the_line_to_get_more_matches),
                //getString(R.string.your_3x_more_likes),
                R.drawable.ic_boost_new,
                0,
                "",
                "x1"
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                // resources.getString(R.string.see_who_like_you),
                getString(R.string.see_who_like_you1),
                resources.getString(R.string.your_crush_is_waiting),
                R.drawable.ic_who_likes_you,
                0,
                "",
                ""
            )
        )
    }

    inner class SliderTimer : TimerTask() {
        override fun run() {
            this@MemberActivity.runOnUiThread(Runnable {
                if (vpSlide.currentItem < membershipSliderArrayList.size - 1) {
                    vpSlide.currentItem = vpSlide.currentItem + 1
                } else {
                    vpSlide.currentItem = 0
                }
            })
        }
    }

    fun onClickElite(view: View){
        showCustomDialog(1)
    }

    private fun showCustomDialog(position:Int) {
        selectedMonths = 1
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MemberActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_member, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        membershipViewModel.setIsLoading(true)
        alertDialog.show()

        dialogView.constHigh.setOnClickListener {
            isselected=0
            if(isselected == 0) {
                dialogView.tvOfferHigh.visibility = VISIBLE
                dialogView.viewSelectedHigh.visibility = VISIBLE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferMedium.visibility = GONE
                dialogView.viewSelectedMedium.visibility = GONE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = GONE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))
            }
        }
        dialogView.constMedium.setOnClickListener {
            isselected=1
            if(isselected == 1){
                dialogView.tvOfferMedium.visibility = VISIBLE
                dialogView.viewSelectedMedium.visibility = VISIBLE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferHigh.visibility = GONE
                dialogView.viewSelectedHigh.visibility = GONE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))

                dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = GONE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))
            }
        }
        dialogView.constLow.setOnClickListener {
            isselected=2
            if(isselected == 2){
                dialogView.tvOfferLow.visibility = VISIBLE
                dialogView.viewSelectedLow.visibility = VISIBLE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferMedium.visibility = GONE
                dialogView.viewSelectedMedium.visibility = GONE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                dialogView.tvOfferHigh.visibility = GONE
                dialogView.viewSelectedHigh.visibility = GONE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))
            }
        }
        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)

        viewpager.adapter =
            MemberDialogSliderAdapter(this@MemberActivity, membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position


        val timer: TimerTask = object : TimerTask() {
            override fun run() {
                this@MemberActivity.runOnUiThread(Runnable {
                    if (viewpager.currentItem < membershipSliderArrayList.size - 1) {
                        viewpager.currentItem = viewpager.currentItem + 1
                    } else {
                        viewpager.currentItem = 0
                    }
                })
            }
        }
        val time = Timer()
        time.schedule(timer, 0, 5000)

        dialogView.btnContinue.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this@MemberActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.SUBSCRIPTION_ID, subscriptionId)
            openActivity(intent)
        }

        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }

        class SliderTime : TimerTask() {
            override fun run() {
                this@MemberActivity.runOnUiThread(Runnable {
                    if (vpSlide.currentItem < membershipSliderArrayList.size - 1) {
                        vpSlide.currentItem = vpSlide.currentItem + 1
                    } else {
                        vpSlide.currentItem = 0
                    }
                })
            }
        }

    }

    fun onClickMembershipBack(view: View) {
        onBackPressed()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
        this.membershipViewList = membershipView
    }


    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        TODO("Not yet implemented")
    }
}
