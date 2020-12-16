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
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SafetySubViewModel
import kotlinx.android.synthetic.main.fragment_safety_sub.*
import javax.inject.Inject

class SafetySubFragment : BaseFragment<FragmentSafetySubBinding>(), SafetySubView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
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

        setFromSessionManager()
    }

    private fun setSelected() {
        tvEveryone.setOnClickListener {
            showSelectedImage(ivEveryoneDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.whoCanSendYouDirectMessage(0)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers
                safetyViewModel.seeYourFollowers(0)
            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video
                safetyViewModel.whoCanCommentYourVideo(0)
            }
        }

        tvFriend.setOnClickListener {
            showSelectedImage(ivFriendDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.whoCanSendYouDirectMessage(1)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers
                safetyViewModel.seeYourFollowers(1)
            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video
                safetyViewModel.whoCanCommentYourVideo(1)
            }
        }

        tvNoOne.setOnClickListener {
            showSelectedImage(ivNoOneDone)
            if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
                safetyViewModel.whoCanSendYouDirectMessage(2)
            } else if (fromSafetyValue == 2) {      //who_can_see_your_followers
                safetyViewModel.seeYourFollowers(2)
            } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video
                safetyViewModel.whoCanCommentYourVideo(2)
            }
        }
    }

    private fun setFromSessionManager() {
        if (fromSafetyValue == 1) {             //For who_can_send_you_direct_msg
            setCanSendYouMessage()
        } else if (fromSafetyValue == 2) {      //who_can_see_your_followers
            setYourFollower()
        } else if (fromSafetyValue == 3) {      //who_can_comments_on_your_video
            setCanCommentVideos()
        }
    }

    private fun setYourFollower() {
        if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 0) {
            showSelectedImage(ivEveryoneDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 1) {
            showSelectedImage(ivFriendDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 2) {
            showSelectedImage(ivNoOneDone)
        } else {
            showSelectedImage(ivEveryoneDone)
        }
    }

    private fun setCanCommentVideos() {
        if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 0) {
            showSelectedImage(ivEveryoneDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 1) {
            showSelectedImage(ivFriendDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 2) {
            showSelectedImage(ivNoOneDone)
        } else {
            showSelectedImage(ivEveryoneDone)
        }
    }

    private fun setCanSendYouMessage() {
        if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 0) {
            showSelectedImage(ivEveryoneDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 1) {
            showSelectedImage(ivFriendDone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 2) {
            showSelectedImage(ivNoOneDone)
        } else {
            showSelectedImage(ivEveryoneDone)
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
            tvSafetySubMessage.visibility = View.GONE
//            tvSafetySubMessage.text =
//                getString(R.string.safety_sub_who_can_comments_on_your_video_msg)
        }
    }

    override fun onDestroy() {
        safetyViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSuccessYourFollowerResponse(safetyBean: SafetyBean) {
        Log.e("SafetySubFragment", "onSuccessResponse  safetyBean: \t ${safetyBean.is_followers}")
        sessionManager.setIntegerValue(safetyBean.is_followers, Constants.KEY_IS_YOUR_FOLLOWERS)

        /* if (safetyBean.is_followers == 0) {
             sessionManager.setStringValue(
                 getString(R.string.everyone),
                 Constants.KEY_IS_YOUR_FOLLOWERS
             )
         } else if (safetyBean.is_followers == 1) {
             sessionManager.setStringValue(
                 getString(R.string.friends),
                 Constants.KEY_IS_YOUR_FOLLOWERS
             )
         } else if (safetyBean.is_followers == 2) {
             sessionManager.setStringValue(
                 getString(R.string.no_one),
                 Constants.KEY_IS_YOUR_FOLLOWERS
             )
         }*/
    }

    override fun onSuccessWhoCanCommentYourVideoResponse(safetyBean: SafetyBean) {
        Log.e(
            "SafetySubFragment",
            "onSuccessResponse  safetyBean: \t ${safetyBean.who_can_comment}"
        )

        sessionManager.setIntegerValue(
            safetyBean.who_can_comment,
            Constants.KEY_CAN_COMMENT_YOUR_VIDEO
        )

        /*  if (safetyBean.who_can_comment == 0) {
              sessionManager.setStringValue(
                  getString(R.string.everyone),
                  Constants.KEY_CAN_COMMENT_YOUR_VIDEO
              )
          } else if (safetyBean.who_can_comment == 1) {
              sessionManager.setStringValue(
                  getString(R.string.friends),
                  Constants.KEY_CAN_COMMENT_YOUR_VIDEO
              )
          } else if (safetyBean.who_can_comment == 2) {
              sessionManager.setStringValue(
                  getString(R.string.no_one),
                  Constants.KEY_CAN_COMMENT_YOUR_VIDEO
              )
          }*/
    }

    override fun onSuccessWhoCanSendYouDirectMessageResponse(safetyBean: SafetyBean) {
        Log.e(
            "SafetySubFragment",
            "onSuccessResponse  safetyBean: \t ${safetyBean.who_can_send_message}"
        )

        sessionManager.setIntegerValue(
            safetyBean.who_can_send_message,
            Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE
        )

        /* if (safetyBean.who_can_send_message == 0) {
             sessionManager.setStringValue(
                 getString(R.string.everyone),
                 Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE
             )
         } else if (safetyBean.who_can_send_message == 1) {
             sessionManager.setStringValue(
                 getString(R.string.friends),
                 Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE
             )
         } else if (safetyBean.who_can_send_message == 2) {
             sessionManager.setStringValue(
                 getString(R.string.no_one),
                 Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE
             )
         }*/
    }

}