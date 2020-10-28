package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MatchedProfileAdapter
import com.namastey.adapter.MessagesAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentMatchesProfileBinding
import com.namastey.model.MatchesListBean
import com.namastey.uiView.MatchesProfileView
import com.namastey.viewModel.MatchesProfileViewModel
import kotlinx.android.synthetic.main.row_matches_profile_first.*
import kotlinx.android.synthetic.main.view_matches_horizontal_list.*
import kotlinx.android.synthetic.main.view_matches_messages_list.*
import javax.inject.Inject

class MatchesProfileFragment : BaseFragment<FragmentMatchesProfileBinding>(), MatchesProfileView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddFriendBinding: FragmentMatchesProfileBinding
    private lateinit var matchesProfileViewModel: MatchesProfileViewModel
    private lateinit var layoutView: View
    private lateinit var matchedProfileAdapter: MatchedProfileAdapter
    private lateinit var messagesAdapter: MessagesAdapter


    override fun getViewModel() = matchesProfileViewModel

    override fun getLayoutId() = R.layout.fragment_matches_profile

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            MatchesProfileFragment().apply {
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

        initUI()
    }

    private fun setupViewModel() {
        matchesProfileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesProfileViewModel::class.java)
        matchesProfileViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = matchesProfileViewModel
    }

    private fun initUI() {
        matchesProfileViewModel.getMatchesList()

        messagesAdapter = MessagesAdapter(requireActivity())
        rvMessagesList.adapter = messagesAdapter

        /* tvMatches.setOnClickListener {
             val intent = Intent(requireContext(), MatchesScreenActivity::class.java)
             openActivity(intent)
         }*/
    }


    override fun onSuccessMatchesList(data: ArrayList<MatchesListBean>) {
//        Log.e("MatchesProfile", "onSuccessMatchesList: \t $data")

        tvLikesCount.text = data.size.toString()
        matchedProfileAdapter = MatchedProfileAdapter(data, requireActivity())
        rvMatchesList.adapter = matchedProfileAdapter
    }

    override fun onDestroy() {
        matchesProfileViewModel.onDestroy()
        super.onDestroy()
    }


}