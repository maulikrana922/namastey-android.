package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.*
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFilterBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.DashboardBean
import com.namastey.model.MembershipSlide
import com.namastey.model.VideoBean
import com.namastey.uiView.FilterView
import com.namastey.utils.*
import com.namastey.viewModel.FilterViewModel
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.dialog_boost_member.view.*
import kotlinx.android.synthetic.main.dialog_boost_member.view.constHigh
import kotlinx.android.synthetic.main.dialog_boost_member.view.constLow
import kotlinx.android.synthetic.main.dialog_boost_member.view.constMedium
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvNothanks
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvOfferHigh
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvOfferMedium
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextBoostHigh
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextBoostLow
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextBoostMedium
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextHigh
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextHighEachBoost
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextLow
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextLowEachBoost
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextMedium
import kotlinx.android.synthetic.main.dialog_boost_member.view.tvTextMediumEachBoost
import kotlinx.android.synthetic.main.dialog_boost_member.view.viewSelectedHigh
import kotlinx.android.synthetic.main.dialog_boost_member.view.viewSelectedLow
import kotlinx.android.synthetic.main.dialog_boost_member.view.viewSelectedMedium
import kotlinx.android.synthetic.main.dialog_member.view.*
import kotlinx.android.synthetic.main.row_filter_category.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class FilterActivity : BaseActivity<ActivityFilterBinding>(), FilterView,
    OnCategoryItemClick, OnItemClick, OnSelectUserItemClick, OnPostImageClick,
    PurchasesUpdatedListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityFilterBinding: ActivityFilterBinding
    private lateinit var filterViewModel: FilterViewModel

    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var categoryAdapter: FilterCategoryAdapter
    private lateinit var filterSubcategoryAdapter: FilterSubcategoryAdapter
    private lateinit var userSearchAdapter: UserSearchAdapter
    private var userList = ArrayList<DashboardBean>()
    private lateinit var albumDetailAdapter: AlbumDetailAdapter
    private var postList = ArrayList<VideoBean>()
    private var followingClick = false
    private val TAG = "FilterActivity"
    private lateinit var membershipSliderArrayList: java.util.ArrayList<MembershipSlide>

    //In App Product Price
    private lateinit var billingClient: BillingClient
    private val subscriptionSkuList = listOf("000010", "000020", "000030")
    private var selectedMonths = 1
    private var subscriptionId = "000020"
    private var isselected: Int = 0

    private var isFollowingSelected: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        filterViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FilterViewModel::class.java)

        activityFilterBinding = bindViewData()
        activityFilterBinding.viewModel = filterViewModel

        initData()
    }

    private fun initData() {
        deselectFollowing()
        setSliderData()
        searchFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (sessionManager.isGuestUser()) {
                    Utils.hideKeyboard(this@FilterActivity)
                } else {
                    if (newText!!.isNotEmpty() && newText.trim().length >= 2) {
                        rvFilterSearch.visibility = View.VISIBLE
                        filterViewModel.getSearchUser(newText.trim())
                    } else
                        rvFilterSearch.visibility = View.GONE
                }
                return true
            }
        })
        rvFilterTranding.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
        followingClick = false
        val jsonObject = JsonObject()
        jsonObject.addProperty("sub_cat_id", "")
        filterViewModel.getTredingVideoList(jsonObject)
        setCategoryList()


        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 0 && postList.size > 10) {
                    showCustomDialog(1)
                }
            }
        })

    }

    override fun onSuccessSearchList(arrayList: java.util.ArrayList<DashboardBean>) {
        this.userList = arrayList
        userSearchAdapter =
            UserSearchAdapter(this.userList, this@FilterActivity, false, this, this)
        rvFilterSearch.adapter = userSearchAdapter
    }

    override fun onSuccessTreding(data: java.util.ArrayList<VideoBean>) {
        postList = data

        if (postList.size <= 0) {
            if (followingClick) {
                llEmpty.visibility = View.VISIBLE
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_following), getString(R.string.you)
                )
                tvEmptyFollow.text = String.format(
                    getString(R.string.msg_empty_following_title),
                    getString(R.string.you)
                )
            } else {
                tvNoFilterVideo.visibility = View.VISIBLE
            }

            rvFilterTranding.visibility = View.GONE
        } else {
            llEmpty.visibility = View.GONE
            tvNoFilterVideo.visibility = View.GONE
            rvFilterTranding.visibility = View.VISIBLE
        }

        if (postList.size > 10)
            setAdapter(if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 0) 10 else postList.size)
        else setAdapter(postList.size)

        Log.e("FilterActivity", "onSuccessTreding: ${data[0].is_like}")
    }

    override fun getViewModel() = filterViewModel

    override fun getLayoutId() = R.layout.activity_filter

    override fun getBindingVariable() = BR.viewModel


//    private fun setTrandingList() {
//        for (number in 0..10) {
//            val trandingBean = User()
//            trandingBean.name = "User"
//            trandingList.add(trandingBean)
//        }
//
//        trandingsUserAdapter = TrandingsUserAdapter(trandingList, this@FilterActivity, this)
//        rvFilterTranding.adapter = trandingsUserAdapter
//    }


    private fun showCustomDialog(position: Int) {
        selectedMonths = 1
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@FilterActivity)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_member, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        filterViewModel.setIsLoading(true)
        alertDialog.show()

        dialogView.constHigh.setOnClickListener {
            isselected = 0
            if (isselected == 0) {
                dialogView.tvOfferHigh.visibility = View.VISIBLE
                dialogView.viewSelectedHigh.visibility = View.VISIBLE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferMedium.visibility = View.GONE
                dialogView.viewSelectedMedium.visibility = View.GONE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))
                //  dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                //dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = View.GONE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text))
                subscriptionId = "000030"

            }
        }
        dialogView.constMedium.setOnClickListener {
            isselected = 1
            if (isselected == 1) {
                dialogView.tvOfferMedium.visibility = View.VISIBLE
                dialogView.viewSelectedMedium.visibility = View.VISIBLE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text_red))
                //dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferHigh.visibility = View.GONE
                dialogView.viewSelectedHigh.visibility = View.GONE
                dialogView.tvTextHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostHigh.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextHighEachBoost.setTextColor(resources.getColor(R.color.color_text))

                //dialogView.tvOfferLow.visibility = GONE
                dialogView.viewSelectedLow.visibility = View.GONE
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
                dialogView.viewSelectedLow.visibility = View.VISIBLE
                dialogView.tvTextLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextBoostLow.setTextColor(resources.getColor(R.color.color_text_red))
                dialogView.tvTextLowEachBoost.setTextColor(resources.getColor(R.color.color_text_red))

                dialogView.tvOfferMedium.visibility = View.GONE
                dialogView.viewSelectedMedium.visibility = View.GONE
                dialogView.tvTextMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextBoostMedium.setTextColor(resources.getColor(R.color.color_text))
                dialogView.tvTextMediumEachBoost.setTextColor(resources.getColor(R.color.color_text))
                //dialogView.tvTextSaveBoost.setTextColor(resources.getColor(R.color.color_text))

                dialogView.tvOfferHigh.visibility = View.GONE
                dialogView.viewSelectedHigh.visibility = View.GONE
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
            MemberDialogSliderAdapter(this@FilterActivity, membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position


        val timer: TimerTask = object : TimerTask() {
            override fun run() {
                this@FilterActivity.runOnUiThread(Runnable {
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
            val intent = Intent(this@FilterActivity, InAppPurchaseActivity::class.java)
            intent.putExtra(Constants.SUBSCRIPTION_ID, subscriptionId)
            openActivity(intent)
        }

        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }

        class SliderTime : TimerTask() {
            override fun run() {
                this@FilterActivity.runOnUiThread(Runnable {
                    if (vpSlide.currentItem < membershipSliderArrayList.size - 1) {
                        vpSlide.currentItem = vpSlide.currentItem + 1
                    } else {
                        vpSlide.currentItem = 0
                    }
                })
            }
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

                                filterViewModel.setIsLoading(false)
                            }

                        }
                    } else if (billingResult.responseCode == 1) {
                        //user cancel
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 2) {
                        Toast.makeText(
                            this@FilterActivity,
                            "Internet required for purchase",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 3) {
                        Toast.makeText(
                            this@FilterActivity,
                            "Incompatible Google Play Billing Version",
                            Toast.LENGTH_LONG
                        ).show()
                        return@querySkuDetailsAsync
                    } else if (billingResult.responseCode == 7) {
                        Toast.makeText(
                            this@FilterActivity,
                            "you already own Premium",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@querySkuDetailsAsync
                    } else
                        Toast.makeText(
                            this@FilterActivity,
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


    private fun setAdapter(size: Int) {
        albumDetailAdapter =
            AlbumDetailAdapter(
                postList,
                this@FilterActivity,
                this,
                this,
                this,
                false,
                true,
                false,
                size
            )
        rvFilterTranding.adapter = albumDetailAdapter
    }

    /**
     * set category list
     */


    private fun setCategoryList() {
        if (intent.hasExtra("categoryList")) {
            categoryBeanList =
                intent.getParcelableArrayListExtra<CategoryBean>("categoryList") as ArrayList<CategoryBean>
        }

        categoryAdapter =
            FilterCategoryAdapter(categoryBeanList, this@FilterActivity, this)
        val horizontalLayout = androidx.recyclerview.widget.LinearLayoutManager(
            this@FilterActivity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        rvFilterCategory.layoutManager = horizontalLayout
        rvFilterCategory.adapter = categoryAdapter

        // Default display first category items
//        setSubcategoryList(categoryBeanList[0].sub_category)
    }

    private fun setSubcategoryList(
        subCategory: ArrayList<CategoryBean>,
        startColor: String,
        endColor: String
    ) {
        filterSubcategoryAdapter =
            FilterSubcategoryAdapter(
                subCategory,
                this@FilterActivity,
                this,
                false,
                startColor,
                endColor
            )
        rvSubcategory.adapter = filterSubcategoryAdapter
    }

    override fun onBackPressed() {
        val signUpFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_FRAGMENT)
        val signupWithPhoneFragment =
            supportFragmentManager.findFragmentByTag(Constants.SIGNUP_WITH_PHONE_FRAGMENT)
        val otpFragment =
            supportFragmentManager.findFragmentByTag(Constants.OTP_FRAGMENT)

        if (signupWithPhoneFragment != null) {
            val childFm = signupWithPhoneFragment.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack();
            } else {
                supportFragmentManager.popBackStack()
            }
        } else if (signUpFragment != null || otpFragment != null)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    fun onClickFilterBack(view: View) {
        onBackPressed()
    }

    /**
     * when user click on filter category item
     */
    override fun onCategoryItemClick(categoryBean: CategoryBean) {
        isFollowingSelected = false

        deselectFollowing()
        setSubcategoryList(
            categoryBean.sub_category,
            categoryBean.startColor,
            categoryBean.endColor
        )
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        val intent = Intent(this, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
        TODO("Not yet implemented")
    }

    override fun onItemPostImageClick(position: Int, videoList: ArrayList<VideoBean>) {
        val intent = Intent(this@FilterActivity, AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoList)
        intent.putExtra("position", position)
        openActivity(intent)
    }

    override fun onSubCategoryItemClick(subCategoryId: Int) {
        Log.d("Subcategory : ", subCategoryId.toString())
        if (sessionManager.isGuestUser()) {

        } else {
            followingClick = false
            postList.clear()
            postList = ArrayList()
            val jsonObject = JsonObject()
            jsonObject.addProperty("sub_cat_id", subCategoryId)
            tvTranding.visibility = View.GONE
            filterViewModel.getTredingVideoList(jsonObject)
        }

    }

    private fun setSliderData() {
        membershipSliderArrayList = java.util.ArrayList()
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

    override fun onItemClick(userId: Long, position: Int) {
        val intent = Intent(this@FilterActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onDestroy() {
        super.onDestroy()
        filterViewModel.onDestroy()
    }

    fun onClickInvite(view: View) {
        openActivity(this@FilterActivity, InviteActivity())
    }

    fun onFollowingClick(view: View) {
        if (::categoryAdapter.isInitialized) {
            categoryAdapter.lastSelectedPos = -1
            categoryAdapter.categoryList.forEach { category ->
                category.is_selected = 0
            }
            categoryAdapter.notifyDataSetChanged()
        }

        followingClick = true
        postList.clear()
        postList = ArrayList()
        val jsonObject = JsonObject()
        jsonObject.addProperty("sub_cat_id", "")
        jsonObject.addProperty("sub_cat_id", "")
        jsonObject.addProperty("following_id", sessionManager.getUserId())

        tvTranding.visibility = View.GONE
        filterViewModel.getTredingVideoList(jsonObject)
        selectFollowing()

    }

    private fun selectFollowing() {
        Utils.filterrectangleCornerShapeGradient(
            tvFollowing, intArrayOf(
                ContextCompat.getColor(this@FilterActivity, R.color.color_text_red),
                ContextCompat.getColor(this@FilterActivity, R.color.color_text_red)
            )
        )
        tvFollowing.setTextColor(ContextCompat.getColor(this@FilterActivity, R.color.white))

    }

    private fun deselectFollowing() {
        Utils.filterrectangleShapeBorder(
            tvFollowing,
            ContextCompat.getColor(this@FilterActivity, R.color.color_text_red), true
        )

        tvFollowing.setTextColor(
            ContextCompat.getColor(
                this@FilterActivity,
                R.color.color_text_red
            )
        )

    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.e(TAG, "onPurchasesUpdated: debugMessage $billingResult")
        Log.e(TAG, "onPurchasesUpdated: responseCode ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.e(TAG, "purchase: \t $purchase")
                Log.e(TAG, "purchaseToken: \t ${purchase.purchaseToken}")
                Log.e(TAG, "purchaseToken: \t $purchase")

                // finish()
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.e(TAG, "onPurchasesUpdated User Cancelled")
            //finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Service Unavailable")
            //finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Billing Unavailable")
            //finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
            Log.e(TAG, "onPurchasesUpdated Item Unavailable")
            //finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            Log.e(TAG, "onPurchasesUpdated Developer Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR) {
            Log.e(TAG, "onPurchasesUpdated  Error")
            finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item already owned")
            //finish()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
            Log.e(TAG, "onPurchasesUpdated Item not owned")
            // finish()
        } else {
            Log.e(TAG, "onPurchasesUpdated: debugMessage ${billingResult.debugMessage}")
            // finish()
        }
    }
}
