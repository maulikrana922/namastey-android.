package com.namastey.listeners

import com.namastey.model.MatchesListBean

interface OnMatchesItemClick {
    fun onMatchesItemClick(value: Long, position: Int, matchesListBean: MatchesListBean?)

}