package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.namastey.activity.MatchesActivity
import com.namastey.adapter.MembershipDialogSliderAdapter
import com.namastey.adapter.NotificationAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentNotificationBinding
import com.namastey.listeners.FragmentRefreshListener
import com.namastey.model.ActivityListBean
import com.namastey.model.FollowRequestBean
import com.namastey.model.MembershipSlide
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.NotificationViewModel
import kotlinx.android.synthetic.main.dialog_membership.view.*
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationView,
    FragmentRefreshListener {

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

    private var isActivityList = 0
    private lateinit var dialog: AlertDialog
    private lateinit var tvAllActivityTitle: TextView
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
        (activity as MatchesActivity).setFragmentRefreshListener(this)
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
        /* isActivityList = 0
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
        setSelectedLayout()
        /*setSelectedTextColor(tvAllActivity)
        setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)*/
    }

    private fun initDialogViews(customLayout: View) {
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
    }

    private fun setDialogClickListeners()   {
        llAllActivity.setOnClickListener {
            isActivityList = 0
            hideDoneImageView(ivAllActivitySelected)
            setSelectedTextColor(tvAllActivity)
            setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.all_activity),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            tvAllActivityTitle.text = resources.getString(R.string.all_activity)
            tvAllActivityMain.text = resources.getString(R.string.all_activity)
            //dialog.dismiss()
        }
        llLikes.setOnClickListener {
            dialog.dismiss()
            showCustomDialog(1)

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
        llComments.setOnClickListener {
            hideDoneImageView(ivCommentsSelected)
            setSelectedTextColor(tvComments)
            setImageViewColor(ivComments, R.drawable.ic_comment)
            isActivityList = 2
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.comments),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            tvAllActivityTitle.text = resources.getString(R.string.comments)
            tvAllActivityMain.text = resources.getString(R.string.comments)
            // dialog.dismiss()
        }
        llFollowers.setOnClickListener {
            hideDoneImageView(ivFollowersSelected)
            setSelectedTextColor(tvFollowers)
            setImageViewColor(ivFollowers, R.drawable.ic_all_activity) // Todo: Change icon
            isActivityList = 3
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.followers),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            tvAllActivityTitle.text = resources.getString(R.string.followers)
            tvAllActivityMain.text = resources.getString(R.string.followers)
            //     dialog.dismiss()
        }
        llMentions.setOnClickListener {
            hideDoneImageView(ivMentionsSelected)
            setSelectedTextColor(tvMentions)
            setImageViewColor(ivMentions, R.drawable.ic_mention)
            isActivityList = 4
            notificationViewModel.getActivityList(isActivityList)
            sessionManager.setIntegerValue(isActivityList, Constants.KEY_ALL_ACTIVITY)
            sessionManager.setStringValue(
                resources.getString(R.string.mentions),
                Constants.KEY_ALL_ACTIVITY_TITLE
            )
            tvAllActivityTitle.text = resources.getString(R.string.mentions)
            tvAllActivityMain.text = resources.getString(R.string.mentions)
            // dialog.dismiss()
        }
    }

    private fun showCustomDialog(position: Int) {
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

    private fun setSliderData() {
        membershipSliderArrayList = ArrayList()
        membershipSliderArrayList.clear()
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._1_boost_each_month),
                getString(R.string.skip_the_line_to_get_more_matches),
                R.drawable.ic_cards_boots,
                R.drawable.dialog_offread_gradiant
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.out_of_likes),
                getString(R.string.dont_want_to_wait),
                R.drawable.ic_cards_outoflike,
                R.drawable.dialog_gradiant_two
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.swipe_around_the_world),
                getString(R.string.passport_to_anywher),
                R.drawable.ic_cards_passport,
                R.drawable.dialog_gradiant_three
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string._5_free_super_message),
                getString(R.string.your_3x_more_likes),
                R.drawable.ic_cards_super_message,
                R.drawable.dialog_gradiant_five
            )
        )
        membershipSliderArrayList.add(
            MembershipSlide(
                resources.getString(R.string.see_who_like_you),
                getString(R.string.month_with_them_intantly),
                R.drawable.ic_cards_super_like,
                R.drawable.dialog_gradiant_six
            )
        )
    }

    private fun manageVisibility(view: View) {
        val conTwel = view.findViewById<ConstraintLayout>(R.id.conTwel)
        val conSix = view.findViewById<ConstraintLayout>(R.id.conSix)
        val conOne = view.findViewById<ConstraintLayout>(R.id.conOne)

        conOne.setOnClickListener {
            view.tvOnemonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvOnemonthText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvOnemonthText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.viewBgOneColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            view.tvMostpopular.visibility = View.VISIBLE
            view.viewSelected.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvSixmonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvSixtext1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvSixText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwel.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwelText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwelText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }

        conSix.setOnClickListener {
            view.tvSixmonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvSixtext1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvSixText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvBgSixColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            view.tvMostpopularSix.visibility = View.VISIBLE
            view.viewSelectedSix.visibility = View.VISIBLE

            view.viewBgOneColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE
            view.viewBgTwelColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularTwel.visibility = View.INVISIBLE
            view.viewSelectedTwel.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvOnemonthText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvOnemonthText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwel.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwelText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvTwelText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }

        conTwel.setOnClickListener {
            view.tvTwel.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTwelText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.tvTwelText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorBlueLight
                )
            )
            view.viewBgTwelColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            view.tvMostpopularTwel.visibility = View.VISIBLE
            view.viewSelectedTwel.visibility = View.VISIBLE

            view.tvBgSixColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopularSix.visibility = View.INVISIBLE
            view.viewSelectedSix.visibility = View.INVISIBLE
            view.viewBgOneColor.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightPink
                )
            )
            view.tvMostpopular.visibility = View.INVISIBLE
            view.viewSelected.visibility = View.INVISIBLE

            view.tvOnemonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvOnemonthText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvOnemonthText1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvSixmonth.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvSixtext1.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
            view.tvSixText2.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorDarkGray
                )
            )
        }
    }

    private fun setSelectedApi() {
        if (sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) != 0) {
            val allActivity = sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY)
            notificationViewModel.getActivityList(allActivity)

            val allActivityTitle = sessionManager.getStringValue(Constants.KEY_ALL_ACTIVITY_TITLE)
            //tvAllActivityTitle.text = allActivityTitle
            tvAllActivityMain.text = allActivityTitle
        } else {
            notificationViewModel.getActivityList(0)
        }
    }

    private fun setSelectedLayout() {
        when {
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 0 -> {
                hideDoneImageView(ivAllActivitySelected)
                setSelectedTextColor(tvAllActivity)
                setImageViewColor(ivAllActivity, R.drawable.ic_all_activity)
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 1 -> {
                hideDoneImageView(ivLikesSelected)
                setSelectedTextColor(tvLikes)
                setImageViewColor(ivLikes, R.drawable.heart)
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 2 -> {
                hideDoneImageView(ivCommentsSelected)
                setSelectedTextColor(tvComments)
                setImageViewColor(ivComments, R.drawable.ic_comment)
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 3 -> {
                hideDoneImageView(ivFollowersSelected)
                setSelectedTextColor(tvFollowers)
                setImageViewColor(ivFollowers, R.drawable.ic_all_activity) // Todo: Change icon
            }
            sessionManager.getIntegerValue(Constants.KEY_ALL_ACTIVITY) == 4 -> {
                hideDoneImageView(ivMentionsSelected)
                setSelectedTextColor(tvMentions)
                setImageViewColor(ivMentions, R.drawable.ic_mention)
            }
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

    override fun onRefresh(activityListBean: ActivityListBean?) {
        if (activityListBean != null) {
            rvNotification.visibility = View.VISIBLE
            if (!this::notificationAdapter.isInitialized) {
                val notificationList = arrayListOf<ActivityListBean>()
                notificationList.add(activityListBean)
                notificationAdapter =
                    NotificationAdapter(requireActivity(), notificationList, isActivityList)
                rvNotification.layoutManager = LinearLayoutManager(context!!)
                rvNotification.adapter = notificationAdapter
            } else {
                //  notificationAdapter.updateNotification(activityListBean)
            }
        }
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
        if (activityList.size != 0) {
            rvNotification.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            notificationAdapter =
                NotificationAdapter(requireActivity(), activityList, isActivityList)
            rvNotification.adapter = notificationAdapter
            Log.e("NotificationFragment", "onSuccessActivityList: \t data: ${activityList.size}")
        } else {
            rvNotification.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        }
    }

    override fun onSuccess(msg: String) {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        //showMsg(msg)
        rvNotification.visibility = View.GONE
        tvNoData.visibility = View.VISIBLE
        tvNoData.text = msg
    }

    override fun onDestroy() {
        notificationViewModel.onDestroy()
        super.onDestroy()
    }
}