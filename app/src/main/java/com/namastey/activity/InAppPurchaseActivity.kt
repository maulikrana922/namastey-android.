package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.namastey.R
import kotlinx.android.synthetic.main.activity_in_app_purchase.*

class InAppPurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private val TAG = "InAppPurchaseActivity"

    private lateinit var billingClient: BillingClient
    //private val skuList = listOf("000010")
    private val skuList = listOf("android.test.purchased")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_purchase)

        setupBillingClient()


        /* billingClient = BillingClient
             .newBuilder(this)
             .enablePendingPurchases()
             .setListener(this)
             .build()
         billingClient.startConnection(object : BillingClientStateListener {
             override fun onBillingSetupFinished(billingResult: BillingResult) {
                 if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                     Log.e(TAG, "Billing client successfully set up")
                     queryOneTimeProducts()
                 }
             }

             override fun onBillingServiceDisconnected() {
                 Log.e(TAG, "Billing service disconnected")
             }
         })*/
    }

    /* private fun queryOneTimeProducts() {
         val skuListToQuery = ArrayList<String>()

         skuListToQuery.add("000010")
         // ‘coins_5’ is the product ID that was set in the Play Console.
         // Here is where we can add more product IDs to query for based on
         //   what was set up in the Play Console.

         val params = SkuDetailsParams.newBuilder()
         params
             .setSkusList(skuListToQuery)
             .setType(BillingClient.SkuType.SUBS)
         // SkuType.INAPP refers to 'managed products' or one time purchases.
         // To query for subscription products, you would use SkuType.SUBS.

         billingClient.querySkuDetailsAsync(
             params.build(),
             object : SkuDetailsResponseListener {
                 override fun onSkuDetailsResponse(
                     p0: BillingResult,
                     skuDetails: MutableList<SkuDetails>?
                 ) {
                     Log.e(TAG, "onSkuDetailsResponse ${p0.responseCode}")
                     Log.e(TAG, "onSkuDetailsResponse $skuDetails")
                     if (skuDetails != null) {
                         for (skuDetail in skuDetails) {
                             Log.e(TAG, skuDetail.toString())
                         }
                     } else {
                         Log.e(TAG, "No skus found from query")
                     }
                 }
             })
     }

     override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
         Log.e(TAG, "onPurchasesUpdated ${p0.responseCode}")
         Log.e(TAG, "onPurchasesUpdated ${p1.toString()}")
     }*/

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "setupBillingClient: Setup Billing Done")
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e(TAG, "setupBillingClient: Failed")

            }
        })
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e(TAG, "loadAllSKUs: billingResult ${billingResult.responseCode}")
            Log.e(TAG, "loadAllSKUs: skuDetailsList $skuDetailsList")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList!!) {

                    Log.e(TAG, "loadAllSKUs ${skuDetails.description}")
                    Log.e(TAG, "loadAllSKUs $skuDetails")

                    if (skuDetails.sku == "000010")
                        buttonBuyProduct.setOnClickListener {
                            val billingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }
                }
            }

            if (billingResult.responseCode == 1) {
                //user cancel
                return@querySkuDetailsAsync
            }
            if (billingResult.responseCode == 2) {
                Toast.makeText(this, "Internet required for purchase", Toast.LENGTH_LONG).show()
                return@querySkuDetailsAsync
            }
            if (billingResult.responseCode == 3) {
                Toast.makeText(
                    this,
                    "Incompatible Google Play Billing Version",
                    Toast.LENGTH_LONG
                ).show()
                return@querySkuDetailsAsync
            }
            if (billingResult.responseCode == 7) {
                Toast.makeText(this, "you already own Premium", Toast.LENGTH_LONG)
                    .show()
                return@querySkuDetailsAsync
            }
            Toast.makeText(this, "no skuDetails sorry", Toast.LENGTH_LONG).show()
        }

    } else {
        println("Billing Client not ready")
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Log.e(TAG, "onPurchasesUpdated: debugMessage $billingResult")
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                acknowledgePurchase(purchase.purchaseToken)

            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e(TAG, "onPurchasesUpdated User Cancelled")
            Log.e(TAG, "onPurchasesUpdated: debugMessage ${billingResult?.debugMessage.toString()}")

        } else {
            Log.e(TAG, "onPurchasesUpdated: debugMessage ${billingResult?.debugMessage.toString()}")
            // Handle any other error codes.
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            Log.e(TAG, "onPurchasesUpdated: debugMessage $debugMessage")
            Log.e(TAG, "onPurchasesUpdated: responseCode $responseCode")
        }
    }
}


/*
class InAppPurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener,
    BillingClientStateListener,
    SkuDetailsResponseListener {
    private val TAG = "InAppPurchaseActivity"

    lateinit private var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_purchase)

        billingClient = BillingClient
            .newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "Billing client successfully set up")
                    queryOneTimeProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "Billing service disconnected")
            }
        })
    }

    private fun queryOneTimeProducts() {
        val skuListToQuery = ArrayList<String>()

        skuListToQuery.add("000010")
        // ‘coins_5’ is the product ID that was set in the Play Console.
        // Here is where we can add more product IDs to query for based on
        //   what was set up in the Play Console.

        val params = SkuDetailsParams.newBuilder()
        params
            .setSkusList(skuListToQuery)
            .setType(BillingClient.SkuType.SUBS)
        // SkuType.INAPP refers to 'managed products' or one time purchases.
        // To query for subscription products, you would use SkuType.SUBS.

        billingClient.querySkuDetailsAsync(
            params.build(),
            object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    p0: BillingResult,
                    skuDetails: MutableList<SkuDetails>?
                ) {
                    Log.e(TAG, "onSkuDetailsResponse ${p0?.responseCode}")
                    Log.e(TAG, "onSkuDetailsResponse $skuDetails")
                    if (skuDetails != null) {
                        for (skuDetail in skuDetails) {
                            Log.e(TAG, skuDetail.toString())
                        }
                    } else {
                        Log.e(TAG, "No skus found from query")
                    }
                }
            })
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        Log.e(TAG, "onPurchasesUpdated BillingResult ${p0?.responseCode}")
        Log.e(TAG, "onPurchasesUpdated BillingResult ${p0?.debugMessage}")
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        Log.e(TAG, "onBillingSetupFinished BillingResult ${p0.responseCode}")
    }

    override fun onBillingServiceDisconnected() {
        Log.e(TAG, "onBillingServiceDisconnected")
    }

    override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
        Log.e(TAG, "onSkuDetailsResponse BillingResult ${p0?.responseCode}")
        Log.e(TAG, "onSkuDetailsResponse BillingResult ${p0?.debugMessage}")
    }
}*/
