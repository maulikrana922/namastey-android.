package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MemberSliderAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMemberBinding
import com.namastey.model.MembershipPriceBean
import com.namastey.model.MembershipSlide
import com.namastey.uiView.MemberShipView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MembershipViewModel
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.activity_member.tlIndicator
import kotlinx.android.synthetic.main.activity_member.vpSlide
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
