package com.namastey.utils

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.*
import com.namastey.R
import com.namastey.activity.InAppPurchaseActivity
import com.namastey.adapter.MemberDialogSliderAdapter
import com.namastey.model.MembershipSlide
import kotlinx.android.synthetic.main.dialog_member.*
import java.util.*

abstract class InAppPurchaseDialog(
    var activity: Activity
) : Dialog(activity, R.style.MyDialogTheme), PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private val subscriptionSkuList = listOf("000010", "000020", "000030")
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var subscriptionId = "000020"
    private var isselected: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_member)
        setSliderData()
        initData()
    }

    private fun initData() {
        constHigh.setOnClickListener {
            isselected = 0
            if (isselected == 0) {
                tvOfferHigh.visibility = View.VISIBLE
                viewSelectedHigh.visibility = View.VISIBLE
                tvTextHigh.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextBoostHigh.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextHighEachBoost.setTextColor(context.resources.getColor(R.color.color_text_red))

                tvOfferMedium.visibility = View.GONE
                viewSelectedMedium.visibility = View.GONE
                tvTextMedium.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostMedium.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextMediumEachBoost.setTextColor(context.resources.getColor(R.color.color_text))

                viewSelectedLow.visibility = View.GONE
                tvTextLow.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostLow.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextLowEachBoost.setTextColor(context.resources.getColor(R.color.color_text))
                subscriptionId = "000030"

            }
        }
        constMedium.setOnClickListener {
            isselected = 1
            if (isselected == 1) {
                tvOfferMedium.visibility = View.VISIBLE
                viewSelectedMedium.visibility = View.VISIBLE
                tvTextMedium.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextBoostMedium.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextMediumEachBoost.setTextColor(context.resources.getColor(R.color.color_text_red))

                tvOfferHigh.visibility = View.GONE
                viewSelectedHigh.visibility = View.GONE
                tvTextHigh.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostHigh.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextHighEachBoost.setTextColor(context.resources.getColor(R.color.color_text))

                viewSelectedLow.visibility = View.GONE
                tvTextLow.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostLow.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextLowEachBoost.setTextColor(context.resources.getColor(R.color.color_text))

                subscriptionId = "000020"

            }
        }
        constLow.setOnClickListener {
            isselected = 2
            if (isselected == 2) {
                viewSelectedLow.visibility = View.VISIBLE
                tvTextLow.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextBoostLow.setTextColor(context.resources.getColor(R.color.color_text_red))
                tvTextLowEachBoost.setTextColor(context.resources.getColor(R.color.color_text_red))

                tvOfferMedium.visibility = View.GONE
                viewSelectedMedium.visibility = View.GONE
                tvTextMedium.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostMedium.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextMediumEachBoost.setTextColor(context.resources.getColor(R.color.color_text))

                tvOfferHigh.visibility = View.GONE
                viewSelectedHigh.visibility = View.GONE
                tvTextHigh.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextBoostHigh.setTextColor(context.resources.getColor(R.color.color_text))
                tvTextHighEachBoost.setTextColor(context.resources.getColor(R.color.color_text))

                subscriptionId = "000010"

            }
        }

        /*Show dialog slider*/
        setupBillingClient()
        viewpagerMembership.adapter =
            MemberDialogSliderAdapter(activity, membershipSliderArrayList)
        tablayout.setupWithViewPager(viewpagerMembership, true)
        viewpagerMembership.currentItem = 1


        val timer: TimerTask = object : TimerTask() {
            override fun run() {
                activity.runOnUiThread(Runnable {
                    if (viewpagerMembership.currentItem < membershipSliderArrayList.size - 1) {
                        viewpagerMembership.currentItem = viewpagerMembership.currentItem + 1
                    } else {
                        viewpagerMembership.currentItem = 0
                    }
                })
            }
        }
        val time = Timer()
        time.schedule(timer, 0, 5000)

        btnContinue.setOnClickListener {
            dismiss()
            val intent = Intent(context, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.SUBSCRIPTION_ID, subscriptionId)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
        }

        tvNothanks.setOnClickListener {
            dismiss()
        }

    }

    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                activity.resources.getString(R.string.super_message),
                activity.getString(R.string.express_your_feeling_freely_with_the_people_you_like),
                R.drawable.ic_super_mesage,
                0,
                "",
                "x5"
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                activity.resources.getString(R.string.unlimited_likes),
                activity.resources.getString(R.string.Do_not_like_to_wait_Go_Unlimited),
                R.drawable.ic_unlimited_likes,
                0,
                "",
                ""
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                activity.resources.getString(R.string.airport),
                activity.resources.getString(R.string.travel_around_the_world_in_just_80_seconds),
                R.drawable.ic_airport,
                0,
                ""
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                activity.resources.getString(R.string.boost),
                activity.resources.getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_boost_new,
                0,
                "",
                "x1"
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                activity.getString(R.string.see_who_like_you1),
                activity.resources.getString(R.string.your_crush_is_waiting),
                R.drawable.ic_who_likes_you,
                0,
                "",
                ""
            )
        )
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "setupBillingClient: Setup Billing Done")
                    loadAllSubsSKUs()
                }
            }

            private fun loadAllSubsSKUs() = if (billingClient.isReady) {
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
                                    tvTextLowEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(activity.resources.getString(R.string.per_month))
                                }

                                if (skuDetails.sku == subscriptionSkuList[1]) {
                                    val price = Utils.splitString(skuDetails.price, 6)
                                    val currencySymbol =
                                        CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                                    tvTextMediumEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(activity.resources.getString(R.string.per_month))

                                }

                                if (skuDetails.sku == subscriptionSkuList[2]) {
                                    val price = Utils.splitString(skuDetails.price, 12)
                                    val currencySymbol =
                                        CurrencySymbol.getCurrencySymbol(skuDetails.priceCurrencyCode)
                                    tvTextHighEachBoost.text =
                                        currencySymbol.plus(price)
                                            .plus(activity.resources.getString(R.string.per_month))
                                }

                                //filterViewModel.setIsLoading(false)
                            }

                        }
                    } else if (billingResult.responseCode == 1) {
                        //user cancel
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 2) {
                        Toast.makeText(
                            activity,
                            "Internet required for purchase",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 3) {
                        Toast.makeText(
                            activity,
                            "Incompatible Google Play Billing Version",
                            Toast.LENGTH_LONG
                        ).show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 7) {
                        Toast.makeText(
                            activity,
                            "you already own Premium",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else
                        Toast.makeText(
                            activity,
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
}