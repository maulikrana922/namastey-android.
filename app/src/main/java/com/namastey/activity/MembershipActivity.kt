package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
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
import kotlinx.android.synthetic.main.dialog_boosts.view.*
import kotlinx.android.synthetic.main.dialog_membership.view.*
import kotlinx.android.synthetic.main.dialog_membership.view.tvNothanks
import kotlinx.android.synthetic.main.dialog_membership.view.tvOfferHigh
import kotlinx.android.synthetic.main.dialog_membership.view.tvOfferLow
import kotlinx.android.synthetic.main.dialog_membership.view.tvOfferMedium
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextBoostHigh
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextBoostLow
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextBoostMedium
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextHigh
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextHighEachBoost
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextLow
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextLowEachBoost
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextMedium
import kotlinx.android.synthetic.main.dialog_membership.view.tvTextMediumEachBoost
import kotlinx.android.synthetic.main.dialog_membership.view.viewBgHigh
import kotlinx.android.synthetic.main.dialog_membership.view.viewBgLow
import kotlinx.android.synthetic.main.dialog_membership.view.viewBgMedium
import kotlinx.android.synthetic.main.dialog_membership.view.viewSelectedHigh
import kotlinx.android.synthetic.main.dialog_membership.view.viewSelectedLow
import kotlinx.android.synthetic.main.dialog_membership.view.viewSelectedMedium
import java.util.*
import javax.inject.Inject


class MembershipActivity : BaseActivity<ActivityMembershipBinding>(),
    PurchasesUpdatedListener, MemberShipView {

    private val TAG = "MembershipActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityMembershipBinding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var membershipViewList = ArrayList<MembershipPriceBean>()
    private var selectedMonths = 1
    private var isFromAirport = false
    private var isFromMatchProfile = false
    private var subscriptionId = "000020"

    //In App Product Price
    private lateinit var billingClient: BillingClient
    private val subscriptionSkuList = listOf("000010", "000020", "000030")

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

    }

    private fun initData() {
        membershipViewModel.getMembershipPriceList()

        if (intent.hasExtra("isFromAirport"))
            isFromAirport = intent.getBooleanExtra("isFromAirport", false)

        if (intent.hasExtra("isFromMatchProfile"))
            isFromMatchProfile = intent.getBooleanExtra("isFromMatchProfile", false)

        setSliderData()

        vpSlide.adapter =
            MembershipSliderAdapter(this@MembershipActivity, membershipSliderArrayList)
        tlIndicator.setupWithViewPager(vpSlide, true)
        val timer = Timer()
        timer.scheduleAtFixedRate(SliderTimer(), 4000, 6000)

        if (isFromAirport) {
            vpSlide.currentItem = 2
            showCustomDialog(2)
        }

        if (isFromMatchProfile) {
            vpSlide.currentItem = 4
            showCustomDialog(4)
        }

        /* if (isFromAirport) {
             vpSlide.currentItem = 2
             showCustomDialog(2)
         }

         if (isFromMatchProfile) {
             vpSlide.currentItem =4
             showCustomDialog(4)
         }*/

    }

    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.boost_your_love_life),
                //resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes1),
                resources.getString(R.string.do_not_want_to_wait_slider),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                //resources.getString(R.string.swipe_around_the_world),
                resources.getString(R.string.explore_the_globe),
                resources.getString(R.string.around_the_world_in_80_seconds),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.super_message),
                resources.getString(R.string.express_your_feelings),
                //getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                // resources.getString(R.string.see_who_like_you),
                getString(R.string.see_who_like_you1),
                resources.getString(R.string.your_crush_is_waiting),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
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

        if( !isFinishing)
         alertDialog.show()

        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)
        setupBillingClient(dialogView)
        viewpager.adapter =
            MembershipDialogSliderAdapter(this@MembershipActivity, membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position
        dialogView.btnContinue.setOnClickListener {
            // alertDialog.dismiss()
            val intent = Intent(this@MembershipActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.SUBSCRIPTION_ID, subscriptionId)
            openActivity(intent)
        }

        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setupBillingClient(view: View) {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "setupBillingClient: Setup Billing Done")
                    loadAllSubsSKUs(view)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e(TAG, "setupBillingClient: Failed")

            }
        })
    }

    private fun loadAllSubsSKUs(view: View) = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(subscriptionSkuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e(TAG, "loadAllSubsSKUs: billingResult ${billingResult.responseCode}")
            Log.e(TAG, "loadAllSubsSKUs: skuDetailsList $skuDetailsList")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (i in skuDetailsList.indices) {

                    val skuDetails = skuDetailsList[i]
                    Log.e(TAG, "loadAllSubsSKUs: skuDetails $skuDetails")

                    if (skuDetails.sku == "000010") {
                        val price = skuDetails.price
                        view.tvTextLowEachBoost.text =
                            price.plus(resources.getString(R.string.per_month))
                        Log.e(TAG, "loadAllSubsSKUs: price $price")
                    }

                    if (skuDetails.sku == "000020") {
                        val price = skuDetails.price
                        view.tvTextMediumEachBoost.text = price
                            .plus(resources.getString(R.string.per_month))
                        /*.plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))*/
                        Log.e(TAG, "loadAllSubsSKUs: price $price")
                    }

                    if (skuDetails.sku == "000030") {
                        val price = skuDetails.price
                        view.tvTextHighEachBoost.text = price
                            .plus(resources.getString(R.string.per_month))
                        /*.plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))*/
                        Log.e(TAG, "loadAllSubsSKUs: price $price")
                    }

                    manageVisibility(view)
                }
            } else if (billingResult.responseCode == 1) {
                //user cancel
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 2) {
                Toast.makeText(this, "Internet required for purchase", Toast.LENGTH_LONG)
                    .show()
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 3) {
                Toast.makeText(
                    this,
                    "Incompatible Google Play Billing Version",
                    Toast.LENGTH_LONG
                ).show()
                return@querySkuDetailsAsync
            } else if (billingResult.responseCode == 7) {
                Toast.makeText(this, "you already own Premium", Toast.LENGTH_LONG)
                    .show()
                return@querySkuDetailsAsync
            } else
                Toast.makeText(this, "no skuDetails sorry", Toast.LENGTH_LONG)
                    .show()
        }
    } else {
        println("Billing Client not ready")
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.e(TAG, "onPurchasesUpdated: debugMessage $billingResult")
        Log.e(TAG, "onPurchasesUpdated: responseCode ${billingResult.responseCode}")
        //Log.e(TAG, "onPurchasesUpdated: purchases $purchases")
        //Log.e(TAG, "onPurchasesUpdated: purchases ${purchases!!.size}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.e(TAG, "purchase: \t $purchase")

                //acknowledgeSubsPurchase(purchase.purchaseToken)

                Log.e(TAG, "purchaseToken: \t ${purchase.purchaseToken}")
                Log.e(TAG, "purchaseToken: \t $purchase")

                finish()
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.e(TAG, "onPurchasesUpdated User Cancelled")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Service Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Billing Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Item Unavailable")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            Log.e(TAG, "onPurchasesUpdated Developer Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR) {
            Log.e(TAG, "onPurchasesUpdated  Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item already owned")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item not owned")
            finish()
        } else {
            Log.e(TAG, "onPurchasesUpdated: debugMessage ${billingResult.debugMessage}")
            finish()
        }
    }


    /*Select membership plan*/
    private fun manageVisibility(view: View) {
        val constHigh = view.findViewById<ConstraintLayout>(R.id.constHigh)
        val constMedium = view.findViewById<ConstraintLayout>(R.id.constMedium)
        val constLow = view.findViewById<ConstraintLayout>(R.id.constLow)

        /*for (data in membershipViewList) {
            val membershipType = data.membership_type
            val price = data.price
            val discount = data.discount_pr
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
        }*/

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

            subscriptionId = "000010"
            /*   val intent = Intent(this@MembershipActivity, InAppPurchaseActivity::class.java)
               intent.putExtra(Constants.SUBSCRIPTION_ID, subscriptionId)
               openActivity(intent)*/
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
            subscriptionId = "000020"

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
            subscriptionId = "000030"
        }
    }

    fun onClickContinue(view: View) {
        Log.d("Membership :", "Selected month ".plus(selectedMonths))
        Log.d(
            "Membership :",
            "Selected month Price ".plus(membershipViewList[selectedMonths].price)
        )

    }

    fun onClickMembershipBack(view: View) {
        onBackPressed()
    }

    fun onClickLike(view: View) {
        //if (membershipViewList.size != 0)
        showCustomDialog(1)
    }

    fun onClickMessage(view: View) {
        // if (membershipViewList.size != 0)
        showCustomDialog(3)
    }

    fun onClickBoost(view: View) {
        // if (membershipViewList.size != 0)
        showCustomDialog(0)
    }

    fun onClickPassport(view: View) {
        //  if (membershipViewList.size != 0)
        showCustomDialog(2)
    }

    fun onClickYouLike(view: View) {
        // if (membershipViewList.size != 0)
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