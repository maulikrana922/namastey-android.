package com.namastey.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileInterestBinding
import com.namastey.fragment.AddLinksFragment
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileInterestViewModel
import kotlinx.android.synthetic.main.activity_profile_interest.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import javax.inject.Inject

class ProfileInterestActivity : BaseActivity<ActivityProfileInterestBinding>(),
    ProfileInterestView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var activityProfileInterestBinding: ActivityProfileInterestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        activityProfileInterestBinding = bindViewData()
        activityProfileInterestBinding.viewModel = profileInterestViewModel

        initData()
    }

    private fun initData() {

//        Log.d("TAG", sessionManager.getCategoryList().toString())
        generateProfileTagUI()
    }

    /**
     * Create dynamic layout as per category selected
     */
    private fun generateProfileTagUI() {
        var profileTagCount = 0
        if (sessionManager.getCategoryList().size > 0) {

            for (categoryBean in sessionManager.getCategoryList()) {
                var layoutInflater = LayoutInflater.from(this@ProfileInterestActivity)
                var view = layoutInflater.inflate(R.layout.view_profile_tag, llProfileTag, false)
                view.tvCategory.text = categoryBean.name.toString()

                for (subCategoryBean in categoryBean.sub_category) {
                    val tvCategory = TextView(this)
                    tvCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvCategory.text = subCategoryBean.name.toString()
                    tvCategory.setPadding(40, 20, 40, 20)
                    tvCategory.setTextColor(Color.WHITE)
                    tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)

                    view.chipProfileTag.addView(tvCategory)

                    tvCategory.setOnClickListener {

                        if (tvCategory.background.constantState == ContextCompat.getDrawable(
                                this@ProfileInterestActivity,
                                R.drawable.rounded_gray_solid
                            )?.constantState
                        ) {
                            ++profileTagCount
                            Utils.rectangleShapeGradient(
                                tvCategory, resources.getColor(R.color.gradient_six_start),
                                resources.getColor(R.color.gradient_six_end)
                            )
                        } else {
                            --profileTagCount
                            tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)
                        }
                        tvCountProfileTag.text = profileTagCount.toString()
                    }
                }

                llProfileTag.addView(view)

                view.tvCategory.setOnClickListener {
                    //                    if (categoryBean.sub_category.size > 0) {
                    if (view.chipProfileTag.visibility == View.VISIBLE) {
                        view.chipProfileTag.visibility = View.GONE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down_gray,
                            0
                        )
                    } else {
                        view.chipProfileTag.visibility = View.VISIBLE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_up_gray,
                            0
                        )
                    }
//                    }
                }
            }
        }
    }

//    private fun generateProfileTagUI() {
//        var profileTagCount = 0
//        if (sessionManager.getCategoryList().size > 0) {
//
//            for (categoryBean in sessionManager.getCategoryList()) {
//                val llCategoryItem = LinearLayout(this)
//                llCategoryItem.orientation = LinearLayout.VERTICAL
//                var layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                layoutParams.topMargin = 25
//                llCategoryItem.layoutParams = layoutParams
//
//                llCategoryItem.setBackgroundResource(R.drawable.rounded_pink_solid)
//                llCategoryItem.setPadding(40, 40, 40, 40)
//
//                val tvCategoryTitle = TextView(this)
//                tvCategoryTitle.setTextColor(Color.GRAY)
//                tvCategoryTitle.layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                tvCategoryTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_arrow_down_gray,
//                    0
//                )
//                tvCategoryTitle.text = categoryBean.name.toString()
//
//                llCategoryItem.addView(tvCategoryTitle)
//
//                val chipGroup = ChipGroup(this)
//                val chipLayoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//
//                chipLayoutParams.topMargin = 25
//                chipGroup.layoutParams = chipLayoutParams
//                chipGroup.chipSpacingVertical = 30
//                for (subCategoryBean in categoryBean.sub_category) {
//                    val tvCategory = TextView(this)
//                    tvCategory.layoutParams = LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
//
//                    tvCategory.text = subCategoryBean.name.toString()
//                    tvCategory.setPadding(40, 20, 40, 20)
//                    tvCategory.setTextColor(Color.WHITE)
//                    tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)
//
//                    chipGroup.addView(tvCategory)
//
//                    tvCategory.setOnClickListener {
//
//                        if (tvCategory.background.constantState == ContextCompat.getDrawable(
//                                this@ProfileInterestActivity,
//                                R.drawable.rounded_gray_solid
//                            )?.constantState
//                        ) {
//                            ++profileTagCount
//                            Utils.rectangleShapeGradient(
//                                tvCategory, resources.getColor(R.color.gradient_six_start),
//                                resources.getColor(R.color.gradient_six_end)
//                            )
//                        } else {
//                            --profileTagCount
//                            tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)
//                        }
//                        tvCountProfileTag.text = profileTagCount.toString()
//                    }
//                }
////                if (categoryBean.sub_category.size > 0)
//                llCategoryItem.addView(chipGroup)
//
//                chipGroup.visibility = View.GONE
//
//                llProfileTag.addView(llCategoryItem)
//
//                tvCategoryTitle.setOnClickListener {
//                    //                    if (categoryBean.sub_category.size > 0) {
//                    if (chipGroup.visibility == View.VISIBLE) {
//                        chipGroup.visibility = View.GONE
//                        tvCategoryTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_arrow_down_gray,
//                            0
//                        )
//                    } else {
//                        chipGroup.visibility = View.VISIBLE
//                        tvCategoryTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_arrow_up_gray,
//                            0
//                        )
//                    }
////                    }
//                }
//            }
//        }
//    }

    override fun getViewModel() = profileInterestViewModel

    override fun getLayoutId() = R.layout.activity_profile_interest

    override fun getBindingVariable() = BR.viewModel

    fun onClickInterestBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    /**
     * click on plus button open add links fragment
     */
    fun onClickAddLinks(view: View) {
        addFragment(AddLinksFragment.getInstance(), Constants.ADD_LINKS_FRAGMENT)
    }

    /**
     * click on next button open create album screen
     */
    fun onClickNextInterest(view: View) {
        openActivity(this@ProfileInterestActivity, CreateAlbumActivity())
    }

}
