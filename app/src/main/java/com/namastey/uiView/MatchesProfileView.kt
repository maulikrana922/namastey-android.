package com.namastey.uiView

import com.namastey.model.MatchesListBean

interface MatchesProfileView: BaseView {
    fun onSuccessMatchesList(data: ArrayList<MatchesListBean>)
}