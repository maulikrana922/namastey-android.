package com.namastey.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MemberDialogSliderAdapter
import com.namastey.adapter.MemberSliderAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMemberBinding
import com.namastey.fragment.SignUpFragment
import com.namastey.model.MembershipPriceBean
import com.namastey.model.MembershipSlide
import com.namastey.uiView.MemberShipView
import com.namastey.utils.Constants
import com.namastey.utils.CurrencySymbol
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.MembershipViewModel
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.dialog_boost_member.*
import kotlinx.android.synthetic.main.dialog_boost_member.view.*
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvBoostDayRemaining
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvNextFreeBoost
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvOutOfBoost
import kotlinx.android.synthetic.main.dialog_boosts.view.*
import kotlinx.android.synthetic.main.dialog_member.view.*
import kotlinx.android.synthetic.main.dialog_member.view.btnContinue
import kotlinx.android.synthetic.main.dialog_member.view.constHigh
import kotlinx.android.synthetic.main.dialog_member.view.constLow
import kotlinx.android.synthetic.main.dialog_member.view.constMedium
import kotlinx.android.synthetic.main.dialog_member.view.tvNothanks
import kotlinx.android.synthetic.main.dialog_member.view.tvOfferHigh
import kotlinx.android.synthetic.main.dialog_member.view.tvOfferMedium
import kotlinx.android.synthetic.main.dialog_member.view.tvTextBoostHigh
import kotlinx.android.synthetic.main.dialog_member.view.tvTextBoostLow
import kotlinx.android.synthetic.main.dialog_member.view.tvTextBoostMedium
import kotlinx.android.synthetic.main.dialog_member.view.tvTextHigh
import kotlinx.android.synthetic.main.dialog_member.view.tvTextHighEachBoost
import kotlinx.android.synthetic.main.dialog_member.view.tvTextLow
import kotlinx.android.synthetic.main.dialog_member.view.tvTextLowEachBoost
import kotlinx.android.synthetic.main.dialog_member.view.tvTextMedium
import kotlinx.android.synthetic.main.dialog_member.view.tvTextMediumEachBoost
import kotlinx.android.synthetic.main.dialog_member.view.viewSelectedHigh
import kotlinx.android.synthetic.main.dialog_member.view.viewSelectedLow
import kotlinx.android.synthetic.main.dialog_member.view.viewSelectedMedium
import java.sql.Timestamp
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
    private var isselected: Int = 0
    private val PERMISSION_REQUEST_CODE = 99
    private var inAppProductId = "b00200"
    //In App Product Price
    private lateinit var billingClient: BillingClient
    private val subscriptionSkuList = listOf("000010", "000020", "000030")
    private val subscriptionSkuBoostList = listOf("b00100", "b00200", "b00300")


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

        if (intent.hasExtra(Constants.ACTIVITY_TYPE)){
            showCustomDialog(3)
        }
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

    fun onClickElite(view: View) {
        showCustomDialog(1)
    }

    fun onAirportClick(view: View) {
        if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1) {

            if (sessionManager.isGuestUser()) {
                addFragment(
                    SignUpFragment.getInstance(
                        false
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            } else {
                if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    completeSignUpDialog()
                } else {
                    addLocationPermission()
                }
            }
        }else showCustomDialog(1)
    }

    private fun addLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openActivity(this, PassportContentActivity())
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PERMISSION_REQUEST_CODE
                )
            }
        } else {
            openActivity(this, PassportContentActivity())
        }
    }

    private fun setupBoostBillingClient(view: View) {
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

            private fun loadAllSubsSKUs(view: View) = if (billingClient.isReady) {
                val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(subscriptionSkuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build()

                billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                        for (i in skuDetailsList.indices) {

                            val skuDetails = skuDetailsList[i]

                            if (subscriptionSkuList.size >= 2) {
                                if (skuDetails.sku == subscriptionSkuList[0]) {
                                    val price = Utils.splitString(skuDetails.price, 1)
                                    val currencySymbol =
                                        CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                                    view.tvTextLowEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(resources.getString(R.string.per_month))
                                }

                                if (skuDetails.sku == subscriptionSkuList[1]) {
                                    val price = Utils.splitString(skuDetails.price, 6)
                                    val currencySymbol =
                                        CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                                    view.tvTextMediumEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(resources.getString(R.string.per_month))

                                }

                                if (skuDetails.sku == subscriptionSkuList[2]) {
                                    val price = Utils.splitString(skuDetails.price, 12)
                                    val currencySymbol =
                                        CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                                    view.tvTextHighEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(resources.getString(R.string.per_month))
                                }

                                membershipViewModel.setIsLoading(false)
                            }

                        }
                    } else if (billingResult.responseCode == 1) {
                        //user cancel
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 2) {
                        Toast.makeText(
                            this@MemberActivity,
                            "Internet required for purchase",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 3) {
                        Toast.makeText(
                            this@MemberActivity,
                            "Incompatible Google Play Billing Version",
                            Toast.LENGTH_LONG
                        ).show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 7) {
                        Toast.makeText(
                            this@MemberActivity,
                            "you already own Premium",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else
                        Toast.makeText(
                            this@MemberActivity,
                            "no skuDetails sorry",
                            Toast.LENGTH_LONG
                        )
                            .show()
                }
            } else {
                println("Billing Client not ready")
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
            .setSkusList(subscriptionSkuBoostList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (i in skuDetailsList.indices) {

                    val skuDetails = skuDetailsList[i]
                    // Log.e(TAG, "loadAllSubsSKUs: skuDetails $skuDetails")

                    if (skuDetails.sku == subscriptionSkuBoostList[0]) {
                        val price = Utils.splitString(skuDetails.price, 3)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        Log.e(TAG, "loadAllSubsSKUs: currencySymbol $currencySymbol")
                        view.tvTextLowEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.each))
                    }

                    if (skuDetails.sku == subscriptionSkuBoostList[1]) {
                        val price = Utils.splitString(skuDetails.price, 10)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        Log.e(TAG, "loadAllSubsSKUs: currencySymbol $currencySymbol")
                        view.tvTextMediumEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.each))
                    }

                    if (skuDetails.sku == subscriptionSkuBoostList[2]) {
                        val price = Utils.splitString(skuDetails.price, 20)
                        val currencySymbol =
                            CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                        Log.e(TAG, "loadAllSubsSKUs: currencySymbol $currencySymbol")
                        view.tvTextHighEachBoost.text =
                            currencySymbol.plus(price).plus(resources.getString(R.string.each))
                    }

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

    fun onClickBoost(view: View){
        showBoostDialog(R.layout.dialog_boost_member)
    }

    private fun showBoostDialog(layout: Int) {

        var isFromProfile = true
        inAppProductId = subscriptionSkuBoostList[1]

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MemberActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val view: View = LayoutInflater.from(this).inflate(layout, viewGroup, false)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        membershipViewModel.setIsLoading(true)
        alertDialog.show()

        //view.llRecurringTextView.visibility = View.GONE
        when {
            sessionManager.getIntegerValue(Constants.KEY_NO_OF_BOOST) > 0 -> {
                view.tvOutOfBoost.text = getString(R.string.need_more_boosts)
                view.tvBoostDayRemaining.visibility = View.GONE
                view.tvNextFreeBoost.visibility = View.GONE
            }
            else -> {
                if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1){
                    val purchaseTimeStamp = Timestamp(sessionManager.getLongValue(Constants.KEY_PURCHASE_DATE))
                    Log.d("purchase time : ", purchaseTimeStamp.time.toString())

                    val date = Date(purchaseTimeStamp.time)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.DATE, 30)
                    Log.d("expire time : ", calendar.time.toString())

                    val currentDate = Calendar.getInstance()
                    val diff: Long = calendar.timeInMillis - currentDate.timeInMillis
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24

                    Log.d("Days remaining : ", days.toString())
                    Log.d("Days remaining : ", hours.toString())
                    Log.d("Days remaining : ", minutes.toString())
                    Log.d("current time : ", currentDate.time.toString())

                    view.tvBoostDayRemaining.text = days.toString().plus(" ").plus(getString(R.string.boost_day_remaining))
                    view.tvBoostDayRemaining.visibility = View.VISIBLE
                    view.tvNextFreeBoost.visibility = View.VISIBLE
                }else{
                    view.tvOutOfBoost.text = getString(R.string.need_more_boosts)
                    view.tvBoostDayRemaining.visibility = View.GONE
                    view.tvNextFreeBoost.visibility = View.GONE
                }
            }
        }
        membershipViewModel.setIsLoading(false)
        manageVisibility(view)

        setupBoostBillingClient(view)

        view.btnBuyBoost.setOnClickListener {
            alertDialog.dismiss()

            val intent = Intent(this@MemberActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.IN_APP_PRODUCT_ID, inAppProductId)
            openActivity(intent)
        }
        view.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }    // var timer = ""

    }

    private fun manageVisibility(view: View) {

        view.constLow.setOnClickListener {
           // view.tvOfferLow.visibility = VISIBLE
            view.viewSelectedLow.visibility = VISIBLE
            view.tvTextLow.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

            view.tvOfferMedium.visibility = GONE
            view.viewSelectedMedium.visibility = GONE
            view.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))

            view.tvOfferHigh.visibility = GONE
            view.viewSelectedHigh.visibility = GONE
            view.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))


            inAppProductId = subscriptionSkuBoostList[0]
        }

        view.constMedium.setOnClickListener {
            view.tvOfferMedium.visibility = VISIBLE
            view.viewSelectedMedium.visibility = VISIBLE
            view.tvTextMedium.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

            view.tvOfferHigh.visibility = GONE
            view.viewSelectedHigh.visibility = GONE
            view.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))

           // view.tvOfferLow.visibility = GONE
            view.viewSelectedLow.visibility = GONE
            view.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))

            inAppProductId = subscriptionSkuBoostList[1]
        }

        view.constHigh.setOnClickListener {
            view.tvOfferHigh.visibility = VISIBLE
            view.viewSelectedHigh.visibility = VISIBLE
            view.tvTextHigh.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text_red))
            view.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

            view.tvOfferMedium.visibility = GONE
            view.viewSelectedMedium.visibility = GONE
            view.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))

           // view.tvOfferLow.visibility = GONE
            view.viewSelectedLow.visibility = GONE
            view.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
            view.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))
            inAppProductId = subscriptionSkuBoostList[2]

        }
    }
    private fun showCustomDialog(position: Int) {
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
            isselected = 0
            if (isselected == 0) {
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
              //  dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                //dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = GONE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))
                subscriptionId = "000030"

            }
        }
        dialogView.constMedium.setOnClickListener {
            isselected = 1
            if (isselected == 1) {
                dialogView.tvOfferMedium.visibility = VISIBLE
                dialogView.viewSelectedMedium.visibility = VISIBLE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text_red))
                //dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferHigh.visibility = GONE
                dialogView.viewSelectedHigh.visibility = GONE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))

                //dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = GONE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))

                subscriptionId = "000020"

            }
        }
        dialogView.constLow.setOnClickListener {
            isselected = 2
            if (isselected == 2) {
                //dialogView.tvOfferLow.visibility = VISIBLE
                dialogView.viewSelectedLow.visibility = VISIBLE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferMedium.visibility = GONE
                dialogView.viewSelectedMedium.visibility = GONE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))
                //dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                dialogView.tvOfferHigh.visibility = GONE
                dialogView.viewSelectedHigh.visibility = GONE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))

                subscriptionId = "000010"

            }
        }
        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)
        setupBillingClient(dialogView)
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

    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1) {
            cvMemberShip.visibility= VISIBLE
            tlIndicator.visibility= View.INVISIBLE
            vpSlide.visibility=GONE
        }else {
            tlIndicator.visibility= VISIBLE
            cvMemberShip.visibility = GONE
            vpSlide.visibility=VISIBLE
        }
    }
}
