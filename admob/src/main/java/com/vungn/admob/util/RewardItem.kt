package com.vungn.admob.util

import com.google.android.gms.ads.rewarded.RewardItem

class RewardItem(private val _rewardItem: RewardItem) {
    fun getAmount(): Int {
        return _rewardItem.amount
    }

    fun getType(): String {
        return _rewardItem.type
    }

    companion object {
        val DEFAULT = RewardItem.DEFAULT_REWARD
    }
}