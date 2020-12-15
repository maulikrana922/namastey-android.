package com.namastey.uiView

import com.namastey.model.LikedUserCountBean
import com.namastey.model.MatchesListBean

interface MatchesProfileView: BaseView {
    fun onSuccessMatchesList(data: ArrayList<MatchesListBean>)
    fun onSuccessMessageList(chatMessageList: ArrayList<MatchesListBean>)
    fun onSuccessLikeUserCount(likedUserCountBean: LikedUserCountBean)

}