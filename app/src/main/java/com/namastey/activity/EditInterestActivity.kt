package com.namastey.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityEditInterestBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileSelectCategoryView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.SelectCategoryViewModel
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit_interest.*
import kotlinx.android.synthetic.main.view_profile_tag.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EditInterestActivity : BaseActivity<ActivityEditInterestBinding>(),
    ProfileSelectCategoryView, OnCategoryItemClick {

    private val TAG: String = EditInterestActivity::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private lateinit var activityEditInterestBinding: ActivityEditInterestBinding
    private var selectedCategoryList: ArrayList<CategoryBean> = ArrayList()
    private var selectCategoryId: ArrayList<Int> = ArrayList()
    private var subCategoryIdList: ArrayList<Int> = ArrayList()
    private var socialAccountList: ArrayList<SocialAccountBean> = ArrayList()
    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()

    var profileTagCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

    }

    override fun onDestroy() {
        selectCategoryViewModel.onDestroy()
        super.onDestroy()
    }


    private fun setupViewModel() {
        selectCategoryViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SelectCategoryViewModel::class.java)

        selectCategoryViewModel.setViewInterface(this)

        activityEditInterestBinding = bindViewData()
        activityEditInterestBinding.viewModel = selectCategoryViewModel

        initData()
    }

    private fun initData() {

        /*selectedCategoryList = sessionManager.getCategoryList()
        if (selectedCategoryList.size == 0)
            selectCategoryId = sessionManager.getChooseInterestList()*/

        selectCategoryViewModel.getCategoryList(sessionManager.getUserId())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        this.categoryBeanList = categoryBeanList


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            categoryBeanList.groupBy { it.sub_category }.forEach { (name, list) ->
                for (i in name.indices)
                    if (name[i].is_selected == 1)
                        selectedCategoryList.add(name[i])

            }
        }


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
                        this@EditInterestActivity,
                        R.color.color_text
                    )
                )

                Utils.rectangleShapeGradient(
                    view, intArrayOf(
                        ContextCompat.getColor(
                            this@EditInterestActivity,
                            android.R.color.transparent
                        ),
                        ContextCompat.getColor(
                            this@EditInterestActivity,
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
                            this@EditInterestActivity,
                            R.color.color_text
                        )
                    )

                    if (subCategoryBean.is_selected == 1) {
                        subCategoryIdList.add(subCategoryBean.id)
                        ++profileTagCount
                        tvSubCategory.setBackgroundResource(R.drawable.rounded_pink_solid_all_corner)

                    }
                    tvSubCategory.setOnClickListener {
                        if (tvSubCategory.background.constantState == ContextCompat.getDrawable(
                                this,
                                R.drawable.rounded_white_solid_white_corner
                            )?.constantState
                        ) {
                            subCategoryIdList.add(subCategoryBean.id)
                            ++profileTagCount
                            tvSubCategory.setBackgroundResource(R.drawable.rounded_pink_solid_all_corner)

                        } else {
                            subCategoryIdList.remove(subCategoryBean.id)
                            --profileTagCount
                            tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)
                        }
                        if (selectedCategoryList.any { it.id == subCategoryBean.id }) {
                            selectedCategoryList.removeIf { bean -> bean.id == subCategoryBean.id }
                        } else {
                            selectedCategoryList.add(subCategoryBean)
                        }

                    }
                }

                llEditInterestTag.addView(view)
                view.chipProfileTag.visibility = View.VISIBLE
            }
        }
    }

    override fun onSuccess(msg: String) {
        onBackPressed()
    }

    override fun getViewModel() = selectCategoryViewModel

    override fun getLayoutId() = R.layout.activity_edit_interest

    override fun getBindingVariable() = BR.viewModel

    @SuppressLint("NewApi")
    override fun onCategoryItemClick(categoryBean: CategoryBean) {
        if (selectedCategoryList.any { it.id == categoryBean.id }) {
            selectedCategoryList.removeIf { bean -> bean.id == categoryBean.id }
        } else {
            selectedCategoryList.add(categoryBean)
        }
    }

    override fun onSubCategoryItemClick(position: Int) {

    }

    fun onEditBackClick(view: View) {
        onBackPressed()
    }

    fun onEditSaveClick(view: View) {
        if (selectedCategoryList.size == Constants.MIN_CHOOSE_INTEREST) {
            editProfileApiCall()
        } else {
            object : CustomAlertDialog(
                this@EditInterestActivity,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }

    private fun editProfileApiCall() {

        val subCategoryId = ArrayList<Int>()
        val jsonArrayInterest = JsonArray()
        for (i in selectedCategoryList.indices)
            jsonArrayInterest.add(selectedCategoryList[i].id)
        val jsonObject = JsonObject()
        jsonObject.add(
            Constants.TAGS,
            jsonArrayInterest
        )


        selectCategoryViewModel.editProfile(jsonObject)
    }

    override fun onBackPressed() {
        finishActivity()
    }

}