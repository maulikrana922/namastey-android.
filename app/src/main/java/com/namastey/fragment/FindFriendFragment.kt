package com.namastey.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.FollowingFollowersActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFindFriendBinding
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.model.DashboardBean
import com.namastey.uiView.FindFriendView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.FindFriendViewModel
import kotlinx.android.synthetic.main.fragment_find_friend.*
import javax.inject.Inject

class FindFriendFragment : BaseFragment<FragmentFindFriendBinding>(), FindFriendView,
    OnInteractionWithFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentFindFriendBinding: FragmentFindFriendBinding
    private lateinit var findFriendViewModel: FindFriendViewModel
    private lateinit var layoutView: View
    override fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>) {
    }

    override fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>) {
    }


    override fun getViewModel() = findFriendViewModel

    override fun getLayoutId() = R.layout.fragment_find_friend

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            FindFriendFragment().apply {

            }
    }

    private fun setupViewModel() {
        findFriendViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FindFriendViewModel::class.java)
        findFriendViewModel.setViewInterface(this)

        fragmentFindFriendBinding = getViewBinding()
        fragmentFindFriendBinding.viewModel = findFriendViewModel

    }

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

    private fun initUI() {
        (activity as FollowingFollowersActivity).setListenerOfInteractionWithFragment(this)

    }

    override fun onClickOfFragmentView(view: View) {
        when (view) {
            ivFindClose -> {
                fragmentManager!!.popBackStack()
            }
            ivInviteFriend, tvInviteFriend -> {
                (requireActivity() as FollowingFollowersActivity).addFragment(
                    AddFriendFragment.getInstance(
                    ),
                    Constants.ADD_FRIEND_FRAGMENT
                )
            }
            ivInviteSMS, tvInviteSMS -> {
                val sendIntent = Intent(Intent.ACTION_VIEW)
                sendIntent.data = Uri.parse("sms:")
                sendIntent.putExtra(
                    "sms_body",
                    String.format(
                        getString(R.string.invite_message),
                        sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                    )
                )
                startActivity(sendIntent)
            }

            tvInviteOther, ivInviteOther -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(
                        getString(R.string.invite_message),
                        sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                    )
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }

            ivInviteWhatsapp, tvInviteWhatsapp -> {
                try {
                    val pm: PackageManager = context!!.packageManager
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)

                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    sendIntent.type = "text/plain"
                    sendIntent.setPackage("com.whatsapp")

                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        String.format(
                            getString(R.string.invite_message),
                            sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                        )
                    )
                    startActivity(sendIntent)
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.whatsapp_not_install_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }
}