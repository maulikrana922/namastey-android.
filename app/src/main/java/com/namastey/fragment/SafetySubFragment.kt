package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSafetySubBinding
import com.namastey.model.SafetyBean
import com.namastey.uiView.SafetySubView
import com.namastey.viewModel.SafetySubViewModel
import kotlinx.android.synthetic.main.fragment_safety_sub.*
import javax.inject.Inject

class SafetySubFragment : BaseFragment<FragmentSafetySubBinding>(), SafetySubView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentSafetySubBinding: FragmentSafetySubBinding
    private lateinit var safetyViewModel: SafetySubViewModel
    private lateinit var layoutView: View

    private var fromSafetyValue = 0

    override fun getViewModel() = safetyViewModel

    override fun getLayoutId() = R.layout.fragment_safety_sub

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(fromSafetyValue: Int) =
            SafetySubFragment().apply {
                this.fromSafetyValue = fromSafetyValue
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
    }

    private fun setupViewModel() {
        safetyViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SafetySubViewModel::class.java)
        safetyViewModel.setViewInterface(this)
        fragmentSafetySubBinding = getViewBinding()
        fragmentSafetySubBinding.viewModel = safetyViewModel

        initData()
    }

    private fun initData() {
        Log.e("SafetySebFragment", "fromSafetyValue: $fromSafetyValue")
        setSelected()
    }

    private fun setSelected() {
        tvEveryone.setOnClickListener {
            showSelectedImage(ivEveryoneDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.seeYourFollowers(0)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers

            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video

            }
        }

        tvFriend.setOnClickListener {
            showSelectedImage(ivFriendDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.seeYourFollowers(1)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers

            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video

            }
        }

        tvNoOne.setOnClickListener {
            showSelectedImage(ivNoOneDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.seeYourFollowers(2)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers

            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video

            }
        }
    }

    private fun setSelectedTemp() {
        tvEveryone.setOnClickListener {
            tvEveryone.isSelected = !tvEveryone.isSelected
            tvFriend.isSelected = false
            tvNoOne.isSelected = false

            ivEveryoneDone.visibility = View.VISIBLE
            ivFriendDone.visibility = View.GONE
            ivNoOneDone.visibility = View.GONE
        }

        tvFriend.setOnClickListener {
            tvEveryone.isSelected = false
            tvFriend.isSelected = !tvFriend.isSelected
            tvNoOne.isSelected = false

            ivEveryoneDone.visibility = View.GONE
            ivFriendDone.visibility = View.VISIBLE
            ivNoOneDone.visibility = View.GONE
        }

        tvNoOne.setOnClickListener {
            tvEveryone.isSelected = false
            tvFriend.isSelected = false
            tvNoOne.isSelected = !tvNoOne.isSelected

            ivEveryoneDone.visibility = View.GONE
            ivFriendDone.visibility = View.GONE
            ivNoOneDone.visibility = View.VISIBLE
        }
    }

    private fun showSelectedImage(imageView: ImageView) {
        ivEveryoneDone.visibility = View.GONE
        ivFriendDone.visibility = View.GONE
        ivNoOneDone.visibility = View.GONE

        imageView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (fromSafetyValue == 1) {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_send_you_direct_msg))
            tvSafetySubMessage.text = getString(R.string.safety_sub_who_can_send_you_direct_msg)
        } else if (fromSafetyValue == 2) {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_see_your_followers))
            tvSafetySubMessage.visibility = View.GONE
        } else if (fromSafetyValue == 3) {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_comments_on_your_video))
            tvSafetySubMessage.text =
                getString(R.string.safety_sub_who_can_comments_on_your_video_msg)
        }
    }

    override fun onDestroy() {
        safetyViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSuccessResponse(safetyBean: SafetyBean) {
        Log.e("SafetySubFragment", "onSuccessResponse  safetyBean: \t ${safetyBean.is_followers}")
    }

}