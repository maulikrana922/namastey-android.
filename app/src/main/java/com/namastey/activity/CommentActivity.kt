package com.namastey.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CommentAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityCommentBinding
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.OnMentionUserItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.listeners.OnSocialTextViewClick
import com.namastey.model.*
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_comment.edtComment
import kotlinx.android.synthetic.main.activity_comment.ivCloseComment
import kotlinx.android.synthetic.main.activity_comment.ivCommentAdd
import kotlinx.android.synthetic.main.activity_comment.lvMentionList
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import java.util.ArrayList
import javax.inject.Inject

class CommentActivity : BaseActivity<ActivityCommentBinding>(), DashboardView,
    OnSelectUserItemClick, OnSocialTextViewClick, OnMentionUserItemClick {

    private val TAG = "CommentActivity"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityCommentBinding: ActivityCommentBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var mentionArrayAdapter: ArrayAdapter<Mention>

    private var colorDrawableBackground = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private var commentCount = -1
    private var isUpdateComment = false

    lateinit var dbHelper: DBHelper
    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_comment

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        dashboardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        activityCommentBinding = bindViewData()
        activityCommentBinding.viewModel = dashboardViewModel

        initData()
    }

    private fun initData() {
        mentionArrayAdapter = MentionArrayAdapter(this)

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white)!!

        var id = intent.getLongExtra ("postId",0L)
        var position = intent.getIntExtra("position",0)
        dashboardViewModel.getCommentList(id)

        ivCommentAdd.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            } else {
                if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    if (edtComment.text.toString().isNotBlank()) {
                        dashboardViewModel.addComment(
                            id,
                           edtComment.text.toString()
                        )
                    }
                } else {
                    completeSignUpDialog()
                }
            }
        }

       edtComment.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            }
        }

        addCommentsTextChangeListener()

        ivCloseComment.setOnClickListener {
            if (isUpdateComment) {
                isUpdateComment = false
                val dashboardBean = feedList[position]
                dashboardBean.comments = commentCount

            }
            finishActivity()
        }
    }

    private fun addCommentsTextChangeListener() {
        mentionArrayAdapter.clear()
        dashboardViewModel.getMentionList("")
        var strMention = ""
       edtComment.showSoftInputOnFocus
       edtComment.mentionColor =
            ContextCompat.getColor(this, R.color.colorBlack)
       edtComment.mentionAdapter = mentionArrayAdapter
       edtComment.setMentionTextChangedListener { view, text ->
            Log.e("mention", text.toString())
            mentionArrayAdapter.notifyDataSetChanged()
            strMention = text.toString()


           Log.d("!!!!!!!!!!!!!!!!!!",strMention)
            if (text.length == 1) {
               lvMentionList.visibility = View.GONE
            } else
                lvMentionList.visibility = View.VISIBLE

        }

       lvMentionList.adapter = mentionArrayAdapter

       lvMentionList.setOnItemClickListener { _, _, i, l ->
            val strName =edtComment.text.toString().replace(
                strMention, "${
                    mentionArrayAdapter.getItem(
                        i
                    ).toString()
                }"
            )
           edtComment.setText(strName)
           edtComment.setSelection(edtComment.text!!.length)
           lvMentionList.visibility = View.GONE

        }
    }

    override fun onSuccess(msg: String) {
//        tvTotalComment.text =
//            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

        isUpdateComment = true
        commentCount = commentAdapter.itemCount
    }
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessFeedFinal(dashboardList: ArrayList<DashboardBean>, total: Int) {
        TODO("Not yet implemented")
    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)
        rvPostComments.scrollToPosition(commentAdapter.itemCount - 1)

//        tvTotalComment.text =
//            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
        commentCount = commentAdapter.itemCount
        isUpdateComment = true
    }

    override fun onSuccessGetComment(data: ArrayList<CommentBean>) {
        rvMentionLists.visibility = View.GONE
        commentAdapter = CommentAdapter(data, this@CommentActivity, this, this)
        rvPostComments.adapter = commentAdapter

//        val params: ViewGroup.LayoutParams =
//            rvPostComments.layoutParams
//        params.height = 1130
//        rvPostComments.layoutParams = params
//      if (data.size > 6) {
//
//        } else {
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
//            rvPostComments.layoutParams = params
//        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (data[viewHolder.adapterPosition].user_id != sessionManager.getUserId()) return 0
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    swipeDirection: Int
                ) {

                    if (sessionManager.getUserId() == data[viewHolder.adapterPosition].user_id) {
                        dashboardViewModel.deleteComment(data[viewHolder.adapterPosition].id)
                        data.removeAt(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRangeChanged(
                            viewHolder.adapterPosition,
                            commentAdapter.itemCount
                        )
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvPostComments)
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
        TODO("Not yet implemented")
    }

    override fun onSuccessFollow(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessReport(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessBlockUser(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessSavePost(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessMention(mentionList: ArrayList<MentionListBean>) {
        if (mentionList.size > 0) {
            rvMentionLists.addItemDecoration(
                DividerItemDecoration(
                    this@CommentActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
          rvMentionLists.visibility = View.GONE

            for (i in mentionList.indices) {
                mentionArrayAdapter.addAll(
                    if (mentionList[i].profile_url != "") Mention(
                        mentionList[i].username,
                        "",
                        mentionList[i].profile_url
                    )
                    else Mention(mentionList[i].username)
                )
            }

        } else {
          rvMentionLists.visibility = View.GONE
        }
    }


    override fun onFailedMaxLike(msg: String, error: Int) {
        TODO("Not yet implemented")
    }

    override fun onSuccessPostShare(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessPurchaseStatus(purchaseBean: PurchaseBean) {
        TODO("Not yet implemented")
    }

    override fun onSuccessBoostUse(boostBean: BoostBean) {
        TODO("Not yet implemented")
    }

    override fun onSuccessStartChat(msg: String) {
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onSelectItemClick(
        userId: Long,
        position: Int,
        userProfileType: String
    ) {
        Log.e(
            "DashboardActivity",
            "onSelectItemClick: \t userProfileType: $userProfileType"
        )
        if (userProfileType == "1" && sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@CommentActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, userId)
            openActivity(intent)
        }
    }


    override fun onClickSocialText(userName: String) {
        TODO("Not yet implemented")
    }

    override fun onMentionItemClick(userId: Long, position: Int, username: String) {
        edtComment.setText(username)
        rvMentionLists.visibility = View.GONE
    }

}