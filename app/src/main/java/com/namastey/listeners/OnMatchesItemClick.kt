package com.namastey.listeners

import com.namastey.model.MatchesListBean

interface OnMatchesItemClick {
    fun onMatchesItemClick(position: Int, matchesListBean: MatchesListBean?)

}