package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AddFriendUserAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddFriendBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.FindFriendView
import com.namastey.utils.Utils
import com.namastey.viewModel.FindFriendViewModel
import kotlinx.android.synthetic.main.fragment_add_friend.*
import javax.inject.Inject

class AddFriendFragment : BaseFragment<FragmentAddFriendBinding>(), FindFriendView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAddFriendBinding: FragmentAddFriendBinding
    private lateinit var findFriendViewModel: FindFriendViewModel
    private lateinit var layoutView: View
    private lateinit var addFriendUserAdapter: AddFriendUserAdapter
    private var userList = ArrayList<User>()

    override fun getViewModel() = findFriendViewModel

    override fun getLayoutId() = R.layout.fragment_add_friend

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            AddFriendFragment().apply {

            }
    }

    private fun setupViewModel() {
        findFriendViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FindFriendViewModel::class.java)
        findFriendViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = findFriendViewModel

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

        ivAddFriendClose.setOnClickListener(this)
        tvFindMultiple.setOnClickListener(this)

        setUserList(false)
    }

    // Temp for ui
    private fun setUserList(isDisplayCkb: Boolean) {

        userList.clear()
        for (number in 0..10) {
            var user = User()
            user.name = "Ankit Dev"
            userList.add(user)
        }
        rvAddFriendSuggested.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        addFriendUserAdapter = AddFriendUserAdapter(userList, requireActivity(),isDisplayCkb)
        rvAddFriendSuggested.adapter = addFriendUserAdapter

    }

    override fun onClick(v: View?) {
        when (v) {
            ivAddFriendClose -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            tvFindMultiple -> {
                if (tvFindMultiple.text.equals(resources.getString(R.string.multiple))) {
                    tvFindMultiple.text = resources.getString(R.string.done)
                    setUserList(true)   // For UI display entire list update
                } else {
                    Utils.hideKeyboard(requireActivity())
                    fragmentManager!!.popBackStack()
                }
            }
        }
    }
}