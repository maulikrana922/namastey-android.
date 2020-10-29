package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnBlockUserClick
import com.namastey.model.BlockUserListBean
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_block_user.view.*

class BlockUserAdapter(
    var blockList: ArrayList<BlockUserListBean>,
    var activity: Activity,
    var onBlockUserClick: OnBlockUserClick,
    var userId: Long
) : androidx.recyclerview.widget.RecyclerView.Adapter<BlockUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_block_user, parent, false
        )
    )

    override fun getItemCount() = blockList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val blockUserListBean = blockList[position]
            tvBlockedUserName.text = blockUserListBean.username
            //  tvFollowingJob.text = dashboardBean.job

            GlideLib.loadImageUrlRoundCorner(activity, ivBlockUser, blockUserListBean.user_image)

            if (userId == blockUserListBean.user_id) {
                tvUnblockLabel.visibility = View.GONE
            } else {
                tvUnblockLabel.visibility = View.VISIBLE
            }


            viewBlockUserMain.setOnClickListener {
                onBlockUserClick.onUserItemClick(blockUserListBean.user_id)
            }
            tvUnblockLabel.setOnClickListener {

                val isBlock = 0

                object : CustomCommonAlertDialog(
                    activity,
                    blockUserListBean.username,
                    resources.getString(R.string.msg_unblock_user),
                    blockUserListBean.user_image,
                    activity.resources.getString(R.string.yes),
                    resources.getString(R.string.cancel)
                ) {
                    override fun onBtnClick(id: Int) {
                        when (id) {
                            btnAlertOk.id -> {
                                onBlockUserClick.onUnblockUserClick(
                                    blockUserListBean.user_id,
                                    isBlock,
                                    position
                                )
                            }
                        }
                    }
                }.show()
            }
        }

    }
}