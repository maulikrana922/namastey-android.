package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.EditProfileActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEditProfileBinding
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.view_profile_basic_info.*
import kotlinx.android.synthetic.main.view_profile_select_interest.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import javax.inject.Inject

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), ProfileBasicView,
    OnInteractionWithFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
    private lateinit var layoutView: View
    private lateinit var profileBasicViewModel: ProfileBasicViewModel

    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.fragment_edit_profile

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            EditProfileFragment().apply {

            }
    }

    private fun setupViewModel() {
        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        profileBasicViewModel.setViewInterface(this)

        fragmentEditProfileBinding = getViewBinding()
        fragmentEditProfileBinding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {

        (activity as EditProfileActivity).setListenerOfInteractionWithFragment(this)

        rangeProfileAge.setMaxStartValue(45f)
        rangeProfileAge.apply()
        rangeProfileAge.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            tvProfileAgeValue.text = "$minValue and $maxValue"
        })

        generateProfileTagUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
//        setupViewModel()

        initData()

    }
    private fun generateProfileTagUI() {
        var profileTagCount = 0
        if (sessionManager.getCategoryList().size > 0) {
            for (categoryBean in sessionManager.getCategoryList()) {
                var layoutInflater = LayoutInflater.from(requireActivity())
                var view = layoutInflater.inflate(R.layout.view_profile_tag, llProfileTag, false)
                view.tvCategory.text = categoryBean.name.toString()

                for (subCategoryBean in categoryBean.sub_category) {
                    val tvCategory = TextView(requireActivity())
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
                                requireActivity(),
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
                }
            }
        }
    }


    override fun onClickOfFragmentView(view: View) {
        onClick(view)
    }

    private fun onClick(view: View) {
        when (view) {
            tvProfileSelectCategory -> {
                (activity as EditProfileActivity).addFragment(
                    SelectCategoryFragment.getInstance(
                    ),
                    Constants.SELECT_CATEGORY_FRAGMENT
                )
            }
            tvProfileInterestIn -> {
                (activity as EditProfileActivity).addFragment(
                    InterestInFragment.getInstance(
                    ),
                    Constants.INTEREST_IN_FRAGMENT
                )
            }
            tvProfileEducation -> {
                (activity as EditProfileActivity).addFragment(
                    EducationFragment.getInstance(
                    ),
                    Constants.EDUCATION_FRAGMENT
                )
            }
            tvProfileJobs -> {
                (activity as EditProfileActivity).addFragment(
                    JobFragment.getInstance(
                    ),
                    Constants.JOB_FRAGMENT
                )
            }
            // click on add link button open new fragment social link
            ivAddLink -> {
                (activity as EditProfileActivity).addFragment(
                    AddLinksFragment.getInstance(true,ArrayList<SocialAccountBean>()),
                    Constants.ADD_LINKS_FRAGMENT
                )
            }
        }

    }

//    override fun onDestroy() {
//        profileBasicViewModel.onDestroy()
//        super.onDestroy()
//    }
}