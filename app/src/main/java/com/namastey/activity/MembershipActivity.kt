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
                getString(R.string.dont_want_to_wait),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.swipe_around_the_world),
                getString(R.string.passport_to_anywher),
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
                getString(R.string.month_with_them_intantly),
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
        val conTwel = view.findViewById<ConstraintLayout>(R.id.conTen)
        val conSix = view.findViewById<ConstraintLayout>(R.id.confive)
        val conOne = view.findViewById<ConstraintLayout>(R.id.conOne)

        for (data in membershipViewList) {
            val membershipType = data.membership_type
            val price = data.price
            val discount = data.discount_pr

            Log.e("MembershipActivity", "numberOfBoost: \t $membershipType")
            Log.e("MembershipActivity", "price: \t $price")
            Log.e("MembershipActivity", "discount: \t $discount")

            if (membershipType == 0) {
                view.tvOnemonthText1.text =
                    resources.getString(R.string.dollars) + " " + price +
                            resources.getString(R.string.per_month) + "\n"
            }

            if (membershipType == 1) {
                view.tvSixText2.text =
                    resources.getString(R.string.dollars) + " " + price +
                            resources.getString(R.string.per_month) + "\n" +
                            resources.getString(R.string.save) + " " + discount +
                            resources.getString(R.string.percentage)

            }

            if (membershipType == 2) {
                view.tvTwelText2.text =
                    resources.getString(R.string.dollars) + " " + price +
                            resources.getString(R.string.per_month) + "\n" +
                            resources.getString(R.string.save) + " " + discount +
                            resources.getString(R.string.percentage)

            }
        }

        conOne.setOnClickListener {
            view.tvOnemonth.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvOnemonthText.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvOnemonthText1.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.viewBgOneColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvMostpopular.visibility = View.VISIBLE
            view.viewSelected.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvFivemonth.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvSixtext1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvSixText2.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwel.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwelText1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwelText2.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
        }

        conSix.setOnClickListener {
            view.tvFivemonth.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvSixtext1.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvSixText2.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvBgSixColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            view.tvMostpopularSix.visibility = View.VISIBLE
            view.viewSelectedSix.visibility = View.VISIBLE

            view.viewBgOneColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvOnemonthText.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvOnemonthText1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwel.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwelText1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvTwelText2.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
        }

        conTwel.setOnClickListener {
            view.tvTwel.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTwelText1.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.tvTwelText2.setTextColor(ContextCompat.getColor(this, R.color.colorBlueLight))
            view.viewBgTwelColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            //view.tvMostpopularTwel.visibility = View.VISIBLE
            view.viewSelectedTwel.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgOneColor.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightPink
                )
            )
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvOnemonthText.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvOnemonthText1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvFivemonth.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvSixtext1.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
            view.tvSixText2.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray))
        }
    }

    fun onClickMembershipBack(view: View) {
        onBackPressed()
    }

    fun onClickLike(view: View) {
        showCustomDialog(1)
    }

    fun onClickMessage(view: View) {
        showCustomDialog(3)
    }

    fun onClickBoost(view: View) {
        showCustomDialog(0)
    }

    fun onClickPassport(view: View) {
        showCustomDialog(2)
    }

    fun onClickYouLike(view: View) {
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