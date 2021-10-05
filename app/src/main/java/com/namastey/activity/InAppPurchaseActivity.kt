package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.android.billingclient.api.*
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityInAppPurchaseBinding
import com.namastey.model.PurchaseBean
import com.namastey.uiView.InAppPurchaseView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.InAppPurchaseViewModel
import kotlinx.android.synthetic.main.activity_in_app_purchase.*
import javax.inject.Inject

class InAppPurchaseActivity : BaseActivity<ActivityInAppPurchaseBinding>(),
    PurchasesUpdatedListener, InAppPurchaseView {
    private val TAG = "InAppPurchaseActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityInAppPurchaseBinding: ActivityInAppPurchaseBinding
    private lateinit var inAppPurchaseViewModel: InAppPurchaseViewModel

    private lateinit var billingClient: BillingClient
    private var subscriptionId = ""
    private var subscriptionSkuList: List<String> = emptyList() // = List()
    //private val subscriptionSkuList = listOf(subscriptionId)
    //private val subscriptionSkuList = listOf("000010")
    //private val skuList = listOf("android.test.purchased")

    private var inAppProductId = ""
    private var inAppProductSkuList: List<String> = emptyList()

    private var package_name = ""
    private var product_id = ""
    private var purchase_token = ""
    private var title = ""
    private var description = ""
    private var subscriptionPeriod = ""
    private var purchaseTime = ""
    private var price = ""
    private var price_currency_code = ""
    private var purchaseState = ""
    private var noOfBoost = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        inAppPurchaseViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(InAppPurchaseViewModel::class.java)
        activityInAppPurchaseBinding = bindViewData()
        activityInAppPurchaseBinding.viewModel = inAppPurchaseViewModel

        if (intent.hasExtra(Constants.SUBSCRIPTION_ID)) {
            subscriptionId = intent.getStringExtra(Constants.SUBSCRIPTION_ID)!!
            Log.e(TAG, "subscriptionId: \t $subscriptionId")
        }

        subscriptionSkuList = listOf(subscriptionId)

        if (intent.hasExtra(Constants.IN_APP_PRODUCT_ID)) {
            inAppProductId = intent.getStringExtra(Constants.IN_APP_PRODUCT_ID)!!
            Log.e(TAG, "inAppProductId: \t $inAppProductId")
        }

        inAppProductSkuList = listOf(inAppProductId)

        // if (subscriptionId != "" && subscriptionId != null)
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

    override fun getViewModel() = inAppPurchaseViewModel

    override fun getLayoutId() = R.layout.activity_in_app_purchase

    override fun getBindingVariable() = BR.viewModel

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
                    if (subscriptionId != "" && subscriptionId != null)
                        loadAllSubsSKUs()
                    else if (inAppProductId != "" && inAppProductId != null)
                        loadAllInAppSku()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e(TAG, "setupBillingClient: Failed")

            }
        })
    }

    private fun loadAllSubsSKUs() = if (billingClient.isReady) {
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

                    if (skuDetails.sku == subscriptionId) {
                        Log.e(TAG, "loadAllSubsSKUs ${skuDetails.description}")
                        Log.e(TAG, "loadAllSubsSKUs $skuDetails")
                        this.product_id = skuDetails.sku
                        this.title = skuDetails.title
                        this.description = skuDetails.description
                        this.subscriptionPeriod = skuDetails.subscriptionPeriod
                        this.price = skuDetails.price
                        this.price_currency_code = skuDetails.priceCurrencyCode

                        val billingFlowParams = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        billingClient.launchBillingFlow(this, billingFlowParams)
                    }

                    /*if (skuDetails.sku == "000020") {
                        val billingFlowParams = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        billingClient.launchBillingFlow(this, billingFlowParams)
                    }*/

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

    private fun loadAllSKUsTemp(subscriptionType: Int) = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(subscriptionSkuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e(TAG, "loadAllSKUs: billingResult ${billingResult.responseCode}")
            Log.e(TAG, "loadAllSKUs: skuDetailsList $skuDetailsList")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {

                    Log.e(TAG, "loadAllSKUs ${skuDetails.description}")
                    Log.e(TAG, "loadAllSKUs $skuDetails")

                    this.product_id = skuDetails.sku
                    this.title = skuDetails.title
                    this.description = skuDetails.description
                    this.subscriptionPeriod = skuDetails.subscriptionPeriod
                    this.price = skuDetails.price
                    this.price_currency_code = skuDetails.priceCurrencyCode

                    if (subscriptionType == 1) {
                        if (skuDetails.sku == "000010") {
                            val billingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }
                    } else if (subscriptionType == 2) {
                        if (skuDetails.sku == "000020") {
                            val billingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }
                    } else {
                        if (skuDetails.sku == "000010") {
                            val billingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }
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

    private fun purchaseVerification() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PACKAGE_NAME, packageName)
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PRODUCT_ID, product_id)
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PURCHASE_TOKEN, purchase_token)
        jsonObject.addProperty(Constants.InAppPurchaseConstants.TITLE, title)
        jsonObject.addProperty(Constants.InAppPurchaseConstants.DESCRIPTION, description)
        jsonObject.addProperty(
            Constants.InAppPurchaseConstants.SUBSCRIPTION_PERIOD,
            subscriptionPeriod
        )
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PURCHASE_TIME, purchaseTime)
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PRICE, price)
        jsonObject.addProperty(
            Constants.InAppPurchaseConstants.PRICE_CURRENCY_CODE,
            price_currency_code
        )
        jsonObject.addProperty(Constants.InAppPurchaseConstants.PURCHASE_STATE, purchaseState)

        if (subscriptionId != "" && subscriptionId != null)
            jsonObject.addProperty(Constants.InAppPurchaseConstants.PURCHASE_TYPE, 1)
        else if (inAppProductId != "" && inAppProductId != null) {
            jsonObject.addProperty(Constants.InAppPurchaseConstants.PURCHASE_TYPE, 0)
            Log.e(TAG, "product_id: \t  ${product_id}")
            when (product_id) {
                "b00100" -> {
                    noOfBoost = "3"
                }
                "b00200" -> {
                    noOfBoost = "10"
                }
                "b00300" -> {
                    noOfBoost = "20"
                }
            }
            jsonObject.addProperty(Constants.InAppPurchaseConstants.NO_OF_BOOST, noOfBoost)
        }
        Log.e(TAG, "jsonObject: \t  ${jsonObject}")
        inAppPurchaseViewModel.purchaseReceiptVerify(jsonObject)
    }

    private fun loadAllInAppSku() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(inAppProductSkuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e(TAG, "loadAllInAppSku: billingResult ${billingResult.responseCode}")
            Log.e(TAG, "loadAllInAppSku: skuDetailsList $skuDetailsList")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (i in skuDetailsList.indices) {

                    val skuDetails = skuDetailsList[i]
                    Log.e(TAG, "loadAllInAppSku $skuDetails")

                    if (skuDetails.sku == inAppProductId) {
                        Log.e(TAG, "loadAllInAppSku ${skuDetails.description}")
                        this.product_id = skuDetails.sku
                        this.title = skuDetails.title
                        this.description = skuDetails.description
                        this.subscriptionPeriod = skuDetails.subscriptionPeriod
                        this.price = skuDetails.price
                        this.price_currency_code = skuDetails.priceCurrencyCode

                        val billingFlowParams = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        billingClient.launchBillingFlow(this, billingFlowParams)
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

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.e(TAG, "purchase: \t $purchase")

                if (subscriptionId != "" && subscriptionId != null)
                    acknowledgeSubsPurchase(purchase.purchaseToken)
                else if (inAppProductId != "" && inAppProductId != null)
                    handleConsumedPurchases(purchase)
                //acknowledgeInAppPurchase(purchase)

                Log.e(TAG, "purchaseToken: \t ${purchase.purchaseToken}")
                Log.e(TAG, "purchaseToken: \t $purchase")

                this.package_name = purchase.packageName.toString()
                this.purchase_token = purchase.purchaseToken.toString()
                this.purchaseTime = purchase.purchaseTime.toString()
                this.purchaseState = purchase.purchaseState.toString()

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

    private fun acknowledgeInAppPurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        Log.e(TAG, "consumeParams: \t $consumeParams")

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        }

        /* if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
             if (!purchase.isAcknowledged) {
                 val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                     .setPurchaseToken(purchase.purchaseToken)
                     .build()
                 billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                     val responseCode = billingResult.responseCode
                     val debugMessage = billingResult.debugMessage

                     Log.e(TAG, "acknowledgePurchase: debugMessage $debugMessage")
                     Log.e(TAG, "acknowledgePurchase: responseCode $responseCode")
                 }
             }
         }*/
    }

    private fun handleConsumedPurchases(purchase: Purchase) {
        Log.e(TAG, "handleConsumablePurchasesAsync foreach it is $purchase")
        val params =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.consumeAsync(params) { billingResult, purchaseToken ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchaseVerification()
                    Log.e(
                        TAG,
                        " Update the appropriate tables/databases to grant user the items"
                    )
                }
                else -> {
                    Log.e(TAG, billingResult.debugMessage)
                }
            }
        }
    }

    private fun acknowledgeSubsPurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            purchaseVerification()

            Log.e(TAG, "acknowledgePurchase: debugMessage $debugMessage")
            Log.e(TAG, "acknowledgePurchase: responseCode $responseCode")
        }
    }

    override fun onSuccess(purchaseBean: PurchaseBean) {
        Log.e(
            TAG,
            "onSuccess: purchaseBean acknowledgementState \t ${purchaseBean.acknowledgementState}"
        )
        Log.e(TAG, "onSuccess: purchaseBean purchaseType \t ${purchaseBean.purchaseType}")
        Log.e(TAG, "onSuccess: purchaseBean paymentState \t ${purchaseBean.paymentState}")
        inAppPurchaseViewModel.getPurchaseStatus()
    }

    override fun onSuccessPurchaseStatus(purchaseBean: PurchaseBean) {
        sessionManager.setIntegerValue(purchaseBean.is_purchase, Constants.KEY_IS_PURCHASE)
        sessionManager.setIntegerValue(
            purchaseBean.number_of_boost_available,
            Constants.KEY_NO_OF_BOOST
        )

       finishActivity()
    }
}

