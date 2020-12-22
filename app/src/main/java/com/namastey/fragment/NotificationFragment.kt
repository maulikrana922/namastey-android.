package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AlbumVideoActivity
import com.namastey.activity.MatchesActivity
import com.namastey.activity.ProfileViewActivity
import com.namastey.adapter.MembershipDialogSliderAdapter
import com.namastey.adapter.NotificationAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentNotificationBinding
import com.namastey.listeners.FragmentRefreshListener
import com.namastey.listeners.OnNotificationClick
import com.namastey.model.*
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.NotificationViewModel
import kotlinx.android.synthetic.main.dialog_membership.view.*
import kotlinx.android.synthetic.main.dialog_notification_all_activity.view.*
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationView,
    FragmentRefreshListener, OnNotificationClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentAddFriendBinding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var layoutView: View
    private lateinit var notificationAdapter: NotificationAdapter
    private var activityList: ArrayList<ActivityListBean> = ArrayList()
    private lateinit var membershipSliderArrayList: ArrayList<MembershipSlide>
    private var videoBeanList: ArrayList<VideoBean> = ArrayList()
    private var membershipViewList = ArrayList<MembershipPriceBean>()
    private var position = -1
    private var isActivityList = 0
    private lateinit var dialog: AlertDialog

    /*private lateinit var tvAllActivityTitle: TextView
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
    private lateinit var ivFollowers: ImageView*/

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
        notificationViewModel.getMembershipPriceList()
        val builder = AlertDialog.Builder(requireContext())
        val customLayout: View =
            layoutInflater.inflate(R.layout.dialog_notification_all_activity, null)
        builder.setView(customLayout)
        dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        /*isActivityList = 0
        notificationViewModel.getActivityList(isActivityList)*/

        setSelectedApi()

        setSliderData()
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
            populateAllActivityDialog(customLayout)
        }
    }

    private fun populateAllActivityDialog(customLayout: View) {
//        val builder = AlertDialog.Builder(requireContext())
//        val customLayout: View = layoutInflater.inflate(R.layout.dialog_notification_all_activity, null)
//        builder.setView(customLayout)
//
//        dialog = builder.create()
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        //initDialogViews(customLayout)
        setDialogClickListeners(customLayout)
        setSelectedLayout(customLayout)
        /*setSelectedTextColor(tvAllActivity)
        setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)*/
    }

    /*private fun initDialogViews(customLayout: View) {
        tvAllActivityTitle = customLayout.findViewById(R.id.tvAllActivityTitle)
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
    }*/

    private fun setDialogClickListeners(customLayout: View) {
        customLayout.llAllActivity.setOnClickListener {
            isActivityList = 0
            hideDoneImageView(customLayout.ivAllActivitySelected, customLayout)
            setSelectedTextColor(customLayout.tvAllActivity, customLayout)
            setImageViewColor(customLayout.ivAllActivity, R.drawable.ic_all_activity, customLayout)
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.all_activity),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            customLayout.tvAllActivityTitle.text = resources.getString(R.string.all_activity)
            tvAllActivityMain.text = resources.getString(R.string.all_activity)
            //dialog.dismiss()
        }
        customLayout.llLikes.setOnClickListener {
            dialog.dismiss()
            showMembershipDialog(4)

            //Todo: Display dialog based on condition
            /* hideDoneImageView(ivLikesSelected)
             setSelectedTextColor(tvLikes)
             setImageViewColor(ivLikes, R.drawable.heart)
             isActivityList = 1
             notificationViewModel.getActivityList(isActivityList)
             sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
             sessionManager.setStringValue(
                 resources.getString(R.string.likes),
                 Constants.KEY_ALL_ACTIVITY_TITLE
             )
             tvAllActivityTitle.text = resources.getString(R.string.likes)
             tvAllActivityMain.text = resources.getString(R.string.likes)*/
        }
        customLayout.llComments.setOnClickListener {
            hideDoneImageView(customLayout.ivCommentsSelected, customLayout)
            setSelectedTextColor(customLayout.tvComments, customLayout)
            setImageViewColor(customLayout.ivComments, R.drawable.ic_comment, customLayout)
            isActivityList = 2
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.comments),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            customLayout.tvAllActivityTitle.text = resources.getString(R.string.comments)
            tvAllActivityMain.text = resources.getString(R.string.comments)
            // dialog.dismiss()
        }
        customLayout.llFollowers.setOnClickListener {
            hideDoneImageView(customLayout.ivFollowersSelected, customLayout)
            setSelectedTextColor(customLayout.tvFollowers, customLayout)
            setImageViewColor(
                customLayout.ivFollowers,
                R.drawable.ic_all_activity,
                customLayout
            ) // Todo: Change icon
            isActivityList = 3
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.followers),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            customLayout.tvAllActivityTitle.text = resources.getString(R.string.followers)
            tvAllActivityMain.text = resources.getString(R.string.followers)
            //     dialog.dismiss()
        }
        customLayout.llMentions.setOnClickListener {
            hideDoneImageView(customLayout.ivMentionsSelected, customLayout)
            setSelectedTextColor(customLayout.tvMentions, customLayout)
            setImageViewColor(customLayout.ivMentions, R.drawable.ic_mention, customLayout)
            isActivityList = 4
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.mentions),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            customLayout.tvAllActivityTitle.text = resources.getString(R.string.mentions)
            tvAllActivityMain.text = resources.getString(R.string.mentions)
            // dialog.dismiss()
        }
    }

    private fun setSelectedApi() {
        Log.e(
            "NotificationFragment",
            "KEY_ALL_ACTIVITY: \t ${sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY)}"
        )
        if (sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) != 0) {
            val allActivity = sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY)
            notificationViewModel.getActivityList(allActivity)
            Log.e("NotificationFragment", "allActivity: \t $allActivity")

            val allActivityTitle = sessionManager.getStringValue(Constants.KEY_ALL_ACTIVITY_TITLE)
            //tvAllActivityTitle.text = allActivityTitle
            tvAllActivityMain.text = allActivityTitle
        } else {
            notificationViewModel.getActivityList(0)
        }
    }

    private fun showMembershipDialog(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        //  val viewGroup: ViewGroup = layoutView.findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_membership, null, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        /*Show dialog slider*/
        val viewpager = dialogView.findViewById<ViewPager>(R.id.viewpagerMembership)
        val tabview = dialogView.findViewById<TabLayout>(R.id.tablayout)
        manageVisibility(dialogView)
        viewpager.adapter =
            MembershipDialogSliderAdapter(requireActivity(), membershipSliderArrayList)
        tabview.setupWithViewPager(viewpager, true)
        viewpager.currentItem = position
        dialogView.tvNothanks.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setSelectedLayout(customLayout: View) {
        when {
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 0 -> {
                hideDoneImageView(customLayout.ivAllActivitySelected, customLayout)
                setSelectedTextColor(customLayout.tvAllActivity, customLayout)
                setImageViewColor(
                    customLayout.ivAllActivity,
                    R.drawable.ic_all_activity,
                    customLayout
                )
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 1 -> {
                hideDoneImageView(customLayout.ivLikesSelected, customLayout)
                setSelectedTextColor(customLayout.tvLikes, customLayout)
                setImageViewColor(customLayout.ivLikes, R.drawable.heart, customLayout)
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 2 -> {
                hideDoneImageView(customLayout.ivCommentsSelected, customLayout)
                setSelectedTextColor(customLayout.tvComments, customLayout)
                setImageViewColor(customLayout.ivComments, R.drawable.ic_comment, customLayout)
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 3 -> {
                hideDoneImageView(customLayout.ivFollowersSelected, customLayout)
                setSelectedTextColor(customLayout.tvFollowers, customLayout)
                setImageViewColor(
                    customLayout.ivFollowers,
                    R.drawable.ic_all_activity,
                    customLayout
                ) // Todo: Change icon
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 4 -> {
                hideDoneImageView(customLayout.ivMentionsSelected, customLayout)
                setSelectedTextColor(customLayout.tvMentions, customLayout)
                setImageViewColor(customLayout.ivMentions, R.drawable.ic_mention, customLayout)
            }
        }
    }

    private fun setSelectedTextColor(textView: TextView, customLayout: View) {
        customLayout.tvAllActivity.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )
        customLayout.tvLikes.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )
        customLayout.tvComments.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )
        customLayout.tvMentions.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )
        customLayout.tvFollowers.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
    }

    private fun hideDoneImageView(imageViewDone: ImageView, customLayout: View) {
        customLayout.ivAllActivitySelected.visibility = View.GONE
        customLayout.ivLikesSelected.visibility = View.GONE
        customLayout.ivCommentsSelected.visibility = View.GONE
        customLayout.ivMentionsSelected.visibility = View.GONE
        customLayout.ivFollowersSelected.visibility = View.GONE

        imageViewDone.visibility = View.VISIBLE
    }

    private fun setImageViewColor(imageView: ImageView, drawable: Int, customLayout: View) {
        /* ivAllActivity.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivLikes.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivComments.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivMentions.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
         ivFollowers.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

         imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);*/

        customLayout.ivAllActivity.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_all_activity_black
            )
        )
        customLayout.ivLikes.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_heart_black
            )
        )
        customLayout.ivComments.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_comment_black
            )
        )
        customLayout.ivMentions.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_mention_black
            )
        )
        customLayout.ivFollowers.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_all_activity_black
            )
        ) // Todo: Change icon

        imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), drawable))

        /* val unwrappedDrawable: Drawable? =
             AppCompatResources.getDrawable(context!!, drawable)
         val wrappedDrawable: Drawable = DrawableCompat.wrap(unwrappedDrawable!!)
         DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext'(), R.color.colorRed))*/
    }

    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes),
                getString(R.string.do_not_want_to_wait_slider),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.swipe_around_the_world),
                getString(R.string.passport_to_anywhere),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._5_free_super_message),
                getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.see_who_like_you),
                getString(R.string.month_with_them_instantly),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        )
    }

    private fun manageVisibility(view: View) {
        val constHigh = view.findViewById<ConstraintLayout>(R.id.constHigh)
        val constMedium = view.findViewById<ConstraintLayout>(R.id.constMedium)
        val constLow = view.findViewById<ConstraintLayout>(R.id.constLow)

        for (data in membershipViewList) {
            val membershipType = data.membership_type
            val price = data.price
            val discount = data.discount_pr

            Log.e("MembershipActivity", "numberOfBoost: \t $membershipType")
            Log.e("MembershipActivity", "price: \t $price")
            Log.e("MembershipActivity", "discount: \t $discount")

            if (membershipType == 0) {
                view.tvTextLowEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
            }

            if (membershipType == 1) {
                view.tvTextMediumEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus(price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus(" ")
                        .plus(discount)
                        .plus(resources.getString(R.string.percentage))

            }

            if (membershipType == 2) {
                view.tvTextHighEachBoost.text =
                    resources.getString(R.string.dollars)
                        .plus( price)
                        .plus(resources.getString(R.string.per_month))
                        .plus("\n")
                        .plus(resources.getString(R.string.save))
                        .plus( " ")
                        .plus( discount )
                        .plus(resources.getString(R.string.percentage))
            }
        }

        constLow.setOnClickListener {
            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            //  view.tvOfferLow.visibility = View.VISIBLE
            view.viewSelectedLow.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }

        constMedium.setOnClickListener {
            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            view.tvOfferMedium.visibility = View.VISIBLE
            view.viewSelectedMedium.visibility = View.VISIBLE

            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferHigh.visibility = View.INVISIBLE
            view.viewSelectedHigh.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }

        constHigh.setOnClickListener {
            view.tvTextHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextBoostHigh.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTextHighEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.viewBgHigh.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            view.tvOfferHigh.visibility = View.VISIBLE
            view.viewSelectedHigh.visibility = View.VISIBLE

            view.viewBgMedium.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferMedium.visibility = View.INVISIBLE
            view.viewSelectedMedium.visibility = View.INVISIBLE
            view.viewBgLow.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvOfferLow.visibility = View.INVISIBLE
            view.viewSelectedLow.visibility = View.INVISIBLE

            view.tvTextLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostLow.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextLowEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextBoostMedium.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTextMediumEachBoost.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }
    }

    override fun onRefresh(activityListBean: ActivityListBean?) {
        if (activityListBean != null) {
            rvNotification.visibility = View.VISIBLE
            if (!this::notificationAdapter.isInitialized) {
                val notificationList = arrayListOf<ActivityListBean>()
                notificationList.add(activityListBean)
                notificationAdapter =
                    NotificationAdapter(requireActivity(), notificationList, isActivityList, this)
                rvNotification.layoutManager = LinearLayoutManager(context!!)
                rvNotification.adapter = notificationAdapter
            } else {
                //  notificationAdapter.updateNotification(activityListBean)
            }
        }
    }

    override fun onNotificationClick(userId: Long, position: Int) {
        Log.e("NotificationFragment", "user_id: \t $userId")
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onClickFollowRequest(
        position: Int,
        userId: Long,
        isFollow: Int
    ) {
        this.position = position
        Log.e("NotificationFragment", "isFollow: \t $isFollow")
        notificationViewModel.followUser(userId, 1)
    }

    override fun onPostVideoClick(position: Int, postId: Long) {
        this.position = position
        videoBeanList.clear()
        notificationViewModel.getPostVideoDetails(postId)
    }

    override fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>) {
        if (data.size >= 3) {
            if (data[0].profile_url != null && data[0].profile_url.isNotEmpty()) {
                // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data.get(0).profile_url)
                Log.e("NotificationFragment", "profile: \t data: ${data[0].profile_url}")
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestFirst,
                    data[0].profile_url
                )
            }
            if (data[1].profile_url != null && data[1].profile_url.isNotEmpty()) {
                Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestSecond,
                    data[1].profile_url
                )
            }

            if (data[2].profile_url != null && data[2].profile_url.isNotEmpty()) {
                Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestThird,
                    data[2].profile_url
                )
            }
        } else if (data.size == 2) {
            if (data[0].profile_url != null && data[0].profile_url.isNotEmpty()) {
                // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data[0].profile_url)
                Log.e("NotificationFragment", "profile: \t data: ${data[0].profile_url}")
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestFirst,
                    data[0].profile_url
                )
            }

            if (data[1].profile_url != null && data[1].profile_url.isNotEmpty()) {
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestSecond,
                    data[1].profile_url
                )
            }
        } else if (data.size == 1) {
            if (data[0].profile_url != null && data[0].profile_url.isNotEmpty()) {
                // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data[0].profile_url)
                Log.e("NotificationFragment", "profile: \t data: ${data[0].profile_url}")
                GlideLib.loadImage(
                    requireContext(),
                    ivFollowRequestFirst,
                    data[0].profile_url
                )
            }
        }

        /*if (data[0].profile_url != null && data[0].profile_url.isNotEmpty()) {
            // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data[0].profile_url)
            Log.e("NotificationFragment", "profile: \t data: ${data[0].profile_url}")
            GlideLib.loadImage(
                requireContext(),
                ivFollowRequestFirst,
                data[0].profile_url
            )
        }

        if (data[1].profile_url != null && data[1].profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
            GlideLib.loadImage(
                requireContext(),
                ivFollowRequestSecond,
                data[1].profile_url
            )
        }

        if (data[2].profile_url != null && data[2].profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
            GlideLib.loadImage(
                requireContext(),
                ivFollowRequestThird,
                data[2].profile_url
            )
        }*/

    }

    override fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>) {
        this.activityList = activityList
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        if (activityList.size != 0) {
            rvNotification.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            notificationAdapter =
                NotificationAdapter(requireActivity(), activityList, isActivityList, this)
            rvNotification.adapter = notificationAdapter
            Log.e(
                "NotificationFragment",
                "onSuccessActivityList: \t data: ${activityList.size}"
            )
        } else {
            rvNotification.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        }
    }

    override fun onSuccessFollow(profileBean: ProfileBean) {
        Log.e("NotificationFragment", "profileBean: \t ${profileBean.is_follow}")
        val activityListBean = activityList[position]
        activityListBean.is_follow = profileBean.is_follow

        Log.e("NotificationFragment", "videoListDetail: ${profileBean.is_follow}")
        activityList[position] = activityListBean
        notificationAdapter.notifyItemChanged(position)

        //notificationAdapter.notifyDataSetChanged()
    }

    override fun onSuccessPostVideoDetailResponse(videoBean: VideoBean) {
        Log.e("NotificationAdapter", "videoBean: ${videoBean.id}")
        videoBeanList.add(videoBean)
        val intent = Intent(requireActivity(), AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoBeanList)
        intent.putExtra("position", position)
        openActivity(intent)

    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
        this.membershipViewList = membershipView
    }

    override fun onSuccess(msg: String) {

        //showMsg(msg)
        rvNotification.visibility = View.GONE
        tvNoData.visibility = View.VISIBLE
        tvNoData.text = msg

        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        notificationViewModel.onDestroy()
        super.onDestroy()
    }
}