package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.MatchesActivity
import com.namastey.adapter.NotificationAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentNotificationBinding
import com.namastey.model.ActivityListBean
import com.namastey.model.FollowRequestBean
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import com.namastey.viewModel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddFriendBinding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var layoutView: View
    private lateinit var notificationAdapter: NotificationAdapter
    private var activityList: ArrayList<ActivityListBean> = ArrayList()

    private var isActivityList = 0
    private lateinit var dialog: AlertDialog
    private lateinit var llAllActivity: LinearLayout
    private lateinit var llLikes: LinearLayout
    private lateinit var llComments: LinearLayout
    private lateinit var llMentions: LinearLayout
    private lateinit var llFollowers: LinearLayout
    private lateinit var ivAllActivitySelected: ImageView
    private lateinit var ivLikesSelected: ImageView
    private lateinit var ivCommentsSelected: ImageView
    private lateinit var ivMentionsSelected: ImageView
    private lateinit var ivFollowersSelected: ImageView
    private lateinit var tvAllActivity: TextView
    private lateinit var tvLikes: TextView
    private lateinit var tvComments: TextView
    private lateinit var tvMentions: TextView
    private lateinit var tvFollowers: TextView
    private lateinit var ivAllActivity: ImageView
    private lateinit var ivLikes: ImageView
    private lateinit var ivComments: ImageView
    private lateinit var ivMentions: ImageView
    private lateinit var ivFollowers: ImageView

    override fun getViewModel() = notificationViewModel

    override fun getLayoutId() = R.layout.fragment_notification

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

        initUI()
    }

    private fun setupViewModel() {
        notificationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
        notificationViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = notificationViewModel
    }

    private fun initUI() {

        notificationViewModel.getFollowRequestList()
        isActivityList = 0
        notificationViewModel.getActivityList(isActivityList)

        /* notificationAdapter = NotificationAdapter(requireActivity())
         rvNotification.adapter = notificationAdapter*/

        tvFollowRequest.setOnClickListener {
            val followRequestFragment = FollowRequestFragment.getInstance()
            followRequestFragment.setTargetFragment(this, Constants.REQUEST_CODE)
            (activity as MatchesActivity).addFragment(
                followRequestFragment,
                Constants.FOLLOW_REQUEST_FRAGMENT
            )
        }

        tvAllActivityMain.setOnClickListener {
            populateAllActivityDialog()
        }
    }

    private fun populateAllActivityDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val customLayout: View =
            layoutInflater.inflate(R.layout.dialog_notification_all_activity, null)
        builder.setView(customLayout)

        /* initDialogViews(customLayout)
         setDialogClickListeners()*/

        // val dialog: AlertDialog = builder.create()
        dialog = builder.create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        initDialogViews(customLayout)
        setDialogClickListeners()
        setSelectedTextColor(tvAllActivity)
        setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)

    }

    private fun initDialogViews(customLayout: View) {
        llAllActivity = customLayout.findViewById(R.id.llAllActivity)
        llLikes = customLayout.findViewById(R.id.llLikes)
        llComments = customLayout.findViewById(R.id.llComments)
        llMentions = customLayout.findViewById(R.id.llMentions)
        llFollowers = customLayout.findViewById(R.id.llFollowers)

        ivAllActivitySelected = customLayout.findViewById(R.id.ivAllActivitySelected)
        ivLikesSelected = customLayout.findViewById(R.id.ivLikesSelected)
        ivCommentsSelected = customLayout.findViewById(R.id.ivCommentsSelected)
        ivMentionsSelected = customLayout.findViewById(R.id.ivMentionsSelected)
        ivFollowersSelected = customLayout.findViewById(R.id.ivFollowersSelected)

        tvAllActivity = customLayout.findViewById(R.id.tvAllActivity)
        tvLikes = customLayout.findViewById(R.id.tvLikes)
        tvComments = customLayout.findViewById(R.id.tvComments)
        tvMentions = customLayout.findViewById(R.id.tvMentions)
        tvFollowers = customLayout.findViewById(R.id.tvFollowers)

        ivAllActivity = customLayout.findViewById(R.id.ivAllActivity)
        ivLikes = customLayout.findViewById(R.id.ivLikes)
        ivComments = customLayout.findViewById(R.id.ivComments)
        ivMentions = customLayout.findViewById(R.id.ivMentions)
        ivFollowers = customLayout.findViewById(R.id.ivFollowers)
    }

    private fun setDialogClickListeners() {
        llAllActivity.setOnClickListener {
            hideDoneImageView(ivAllActivitySelected)
            setSelectedTextColor(tvAllActivity)
            setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)
            isActivityList = 0
            notificationViewModel.getActivityList(isActivityList)
            //dialog.dismiss()
        }
        llLikes.setOnClickListener {
            hideDoneImageView(ivLikesSelected)
            setSelectedTextColor(tvLikes)
            setImageViewColor(ivLikes, R.drawable.heart)
            isActivityList = 1
            notificationViewModel.getActivityList(isActivityList)
            // dialog.dismiss()
        }
        llComments.setOnClickListener {
            hideDoneImageView(ivCommentsSelected)
            setSelectedTextColor(tvComments)
            setImageViewColor(ivComments, R.drawable.ic_comment)
            isActivityList = 2
            notificationViewModel.getActivityList(isActivityList)
            // dialog.dismiss()
        }
        llMentions.setOnClickListener {
            hideDoneImageView(ivMentionsSelected)
            setSelectedTextColor(tvMentions)
            setImageViewColor(ivMentions, R.drawable.ic_mention)
            isActivityList = 4
            notificationViewModel.getActivityList(isActivityList)
            // dialog.dismiss()
        }
        llFollowers.setOnClickListener {
            hideDoneImageView(ivFollowersSelected)
            setSelectedTextColor(tvFollowers)
            setImageViewColor(ivFollowers, R.drawable.ic_all_activity) // Todo: Change icon
            isActivityList = 3
            notificationViewModel.getActivityList(isActivityList)
            //     dialog.dismiss()
        }
    }

    private fun setSelectedTextColor(textView: TextView) {
        tvAllActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        tvLikes.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        tvComments.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        tvMentions.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        tvFollowers.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
    }

    private fun hideDoneImageView(imageViewDone: ImageView) {
        ivAllActivitySelected.visibility = View.GONE
        ivLikesSelected.visibility = View.GONE
        ivCommentsSelected.visibility = View.GONE
        ivMentionsSelected.visibility = View.GONE
        ivFollowersSelected.visibility = View.GONE

        imageViewDone.visibility = View.VISIBLE
    }

    private fun setImageViewColor(imageView: ImageView, drawable: Int) {
        /* ivAllActivity.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivLikes.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivComments.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivMentions.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivFollowers.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);


         imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);*/

        ivAllActivity.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_all_activity_black
            )
        )
        ivLikes.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_heart_black
            )
        )
        ivComments.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_comment_black
            )
        )
        ivMentions.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_mention_black
            )
        )
        ivFollowers.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_all_activity_black
            )
        ) // Todo: Change icon

        imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), drawable))

        /* val unwrappedDrawable: Drawable? =
             AppCompatResources.getDrawable(context!!, drawable)
         val wrappedDrawable: Drawable = DrawableCompat.wrap(unwrappedDrawable!!)
         DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), R.color.colorRed))*/
    }

    override fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>) {
        Log.e("NotificationFragment", "onSuccessFollowRequest: \t data: ${data.size}")
        if (data.get(0).profile_url != null && data.get(0).profile_url.isNotEmpty()) {
            // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data.get(0).profile_url)
            Log.e("NotificationFragment", "profile: \t data: ${data.get(0).profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(0).profile_url)
                .into(ivFollowRequestFirst)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestFirst)
        }

        if (data.get(1).profile_url != null && data.get(1).profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(1).profile_url)
                .into(ivFollowRequestSecond)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestSecond)
        }

        if (data.get(2).profile_url != null && data.get(2).profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data.get(2).profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(2).profile_url)
                .into(ivFollowRequestThird)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestThird)
        }

    }

    override fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>) {
        this.activityList = activityList
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        notificationAdapter = NotificationAdapter(requireActivity(), activityList, isActivityList)
        rvNotification.adapter = notificationAdapter
        Log.e("NotificationFragment", "onSuccessActivityList: \t data: ${activityList.size}")
    }

    override fun onDestroy() {
        notificationViewModel.onDestroy()
        super.onDestroy()
    }
}