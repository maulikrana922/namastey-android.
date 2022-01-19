package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.InterestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityInterestBinding
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.InterestBean
import com.namastey.model.InterestSubCategoryBean
import com.namastey.roomDB.entity.User
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.*
import com.namastey.viewModel.ChooseInterestViewModel
import kotlinx.android.synthetic.main.activity_edit_interest.*
import kotlinx.android.synthetic.main.activity_interest.*
import kotlinx.android.synthetic.main.activity_interest.llEditInterestTag
import kotlinx.android.synthetic.main.row_sub_category.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import java.util.*
import javax.inject.Inject

class InterestActivity : BaseActivity<ActivityInterestBinding>(), ChooseInterestView,
    OnImageItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var chooseInterestViewModel: ChooseInterestViewModel

    private lateinit var activityInterestBinding: ActivityInterestBinding
    private lateinit var interestAdapter: InterestAdapter
    private var selectInterestIdList: ArrayList<Int> = ArrayList()
    private var androidId = ""
    private var selectCategoryId: ArrayList<Int> = ArrayList()
    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private var subCategoryIdList: ArrayList<Int> = ArrayList()
    var profileTagCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

        initData()
    }

    private fun initData() {
        // chooseInterestViewModel.getChooseInterestList()
        // chooseInterestViewModel.getAllSubcategoryList()
        chooseInterestViewModel.getCategoryList(sessionManager.getUserId())
        androidId =
            Settings.Secure.getString(
                this@InterestActivity.contentResolver,
                Settings.Secure.ANDROID_ID
            )
    }

    /**
     * click on any item then display top select count and store id list
     */
    override fun onImageItemClick(interestBeanInterest: InterestSubCategoryBean) {

        if (selectInterestIdList.contains(interestBeanInterest.id)) {
            selectCategoryId.remove(interestBeanInterest.category_id)
            selectInterestIdList.remove(interestBeanInterest.id)
        } else {
            selectCategoryId.add(interestBeanInterest.category_id)
            selectInterestIdList.add(interestBeanInterest.id)
        }

        sessionManager.setChooseInterestList(selectCategoryId)
    }

    override fun onClose() {
    }

    override fun onNext() {

        if (selectInterestIdList.size >= Constants.MIN_CHOOSE_INTEREST) {

            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.GENDER, sessionManager.getUserGender())


            val jsonArrayInterest = JsonArray()
            for (selectInterest in selectInterestIdList) {
                jsonArrayInterest.add(JsonPrimitive(selectInterest))
            }
            // jsonObject.add(Constants.INTEREST, jsonArrayInterest)
            jsonObject.add(Constants.TAGS, jsonArrayInterest)

            jsonObject.addProperty(Constants.DEVICE_ID, androidId)    // Need to change
            jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
            if (!sessionManager.isGuestUser()) {
                jsonObject.addProperty(Constants.USER_UNIQUEID, sessionManager.getUserUniqueId())
            } else {
                sessionManager.setGuestUser(true)
            }
//            chooseInterestViewModel.updateOrCreateUser(jsonObject)

        } else {
            object : CustomAlertDialog(
                this@InterestActivity,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }

    }

    override fun onSuccessCreateOrUpdate(user: User) {
        if (user.token.isNotEmpty())
            sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setuserUniqueId(user.user_uniqueId)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@InterestActivity, DashboardActivity::class.java))
            this@InterestActivity.finish()
        }, 1000)
    }

    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        this.categoryBeanList = categoryBeanList

        if (categoryBeanList.size > 0) {
            for (categoryBeanPosition in categoryBeanList.indices) {
                var categoryBean = categoryBeanList[categoryBeanPosition]
                val layoutInflater = LayoutInflater.from(this)
                val view =
                    layoutInflater.inflate(R.layout.view_profile_tag, llEditInterestTag, false)

                (view.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 0)
                view.tvCategory.text = categoryBean.name

                view.tvCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                view.tvCategory.setTextColor(
                    ContextCompat.getColor(
                        this@InterestActivity,
                        R.color.color_text
                    )
                )

                Utils.rectangleShapeGradient(
                    view, intArrayOf(
                        ContextCompat.getColor(
                            this@InterestActivity,
                            android.R.color.transparent
                        ),
                        ContextCompat.getColor(
                            this@InterestActivity,
                            android.R.color.transparent
                        )
                    )
                )

                for (subCategoryBean in categoryBean.sub_category) {
                    val tvSubCategory = TextView(this)
                    tvSubCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvSubCategory.text = subCategoryBean.name
                    tvSubCategory.setPadding(40, 20, 40, 20)
                    tvSubCategory.setTextColor(Color.BLACK)
                    tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)

                    view.chipProfileTag.addView(tvSubCategory)
                    tvSubCategory.setTextColor(
                        ContextCompat.getColor(
                            this@InterestActivity,
                            R.color.color_text
                        )
                    )

                    if (subCategoryBean.is_selected == 1) {
                        subCategoryIdList.add(subCategoryBean.id)
                        ++profileTagCount
                        //tvSubCategory.setBackgroundResource(R.drawable.rounded_pink_solid_all_corner)
                        Utils.roundShapeGradient(
                            tvSubCategory, intArrayOf(
                                Color.parseColor(categoryBean.startColor),
                                Color.parseColor(categoryBean.endColor)
                            )
                        )
                    }
                    tvSubCategory.setOnClickListener {

                        if (selectInterestIdList.contains(subCategoryBean.id)) {
                            selectCategoryId.remove(categoryBean.id)
                            selectInterestIdList.remove(subCategoryBean.id)
                            setItemBackGround(tvSubCategory,subCategoryBean,categoryBean)
                        } else {
                            if (selectInterestIdList.size < Constants.MIN_CHOOSE_INTEREST) {
                                selectCategoryId.add(categoryBean.id)
                                selectInterestIdList.add(subCategoryBean.id)
                                setItemBackGround(tvSubCategory,subCategoryBean,categoryBean)
                            } else {
                                object : CustomAlertDialog(
                                    this@InterestActivity,
                                    resources.getString(R.string.msg_min_choose_interest),
                                    getString(R.string.ok),
                                    ""
                                ) {
                                    override fun onBtnClick(id: Int) {
                                        dismiss()
                                    }
                                }.show()
                            }
                        }

                        tvCount.text = selectInterestIdList.size.toString().plus("/9")

                    }

                }

                llEditInterestTag.addView(view)
                view.chipProfileTag.visibility = View.VISIBLE


            }
        }

    }

    fun setItemBackGround(tvSubCategory: TextView,subCategoryBean:CategoryBean,categoryBean:CategoryBean){

        if (tvSubCategory.background.constantState == ContextCompat.getDrawable(
                this,
                R.drawable.rounded_white_solid_white_corner
            )?.constantState
        ) {
            subCategoryIdList.add(subCategoryBean.id)
            ++profileTagCount
            //tvSubCategory.setBackgroundResource(R.drawable.rounded_pink_solid_all_corner)
            Utils.roundShapeGradient(
                tvSubCategory, intArrayOf(
                    Color.parseColor(categoryBean.startColor),
                    Color.parseColor(categoryBean.endColor)
                )
            )
        } else {
            subCategoryIdList.remove(subCategoryBean.id)
            --profileTagCount
            tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)
        }
    }

    override fun onSuccess(interestList: ArrayList<InterestBean>) {
        Log.e("ChooseInterestFragment", "interestList")
    }

    override fun onSuccessAllCategoryList(interestList: ArrayList<InterestSubCategoryBean>) {
        rvChooseInterest.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, this@InterestActivity, this, true)
        rvChooseInterest.adapter = interestAdapter
    }

    override fun getViewModel() = chooseInterestViewModel


    override fun getLayoutId() = R.layout.activity_interest

    override fun getBindingVariable() = BR.viewModel

    private fun setupViewModel() {
        chooseInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ChooseInterestViewModel::class.java)
        chooseInterestViewModel.setViewInterface(this)
        activityInterestBinding = bindViewData()
        activityInterestBinding.viewModel = chooseInterestViewModel
    }

    fun onClickContinue(view: View) {
        if (selectInterestIdList.size == Constants.MIN_CHOOSE_INTEREST) {
            Log.e("InterestList", selectInterestIdList.size.toString())
            sessionManager.setChooseInterestList(selectInterestIdList)

//            val jsonArrayInterest = JsonArray()
//            for (selectInterest in selectInterestIdList) {
//                jsonArrayInterest.add(JsonPrimitive(selectInterest))
//            }
//            jsonObject.add(Constants.TAGS, jsonArrayInterest)

            openActivity(this@InterestActivity, AboutActivity())

        } else {
            object : CustomAlertDialog(
                this@InterestActivity,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        chooseInterestViewModel.onDestroy()
        super.onDestroy()
    }
}