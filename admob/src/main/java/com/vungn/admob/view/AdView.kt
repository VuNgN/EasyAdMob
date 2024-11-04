package com.vungn.admob.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.gms.ads.BaseAdView
import com.google.android.gms.common.internal.Preconditions
import com.vungn.admob.util.LifecycleAwareAdView

class AdView : BaseAdView, LifecycleAwareAdView {
    constructor(context: Context) : super(context, 0) {
        Preconditions.checkNotNull(context, "Context cannot be null")
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr, 0
    )

    override fun onPause() {
        super.pause()
    }

    override fun onResume() {
        super.resume()
    }

    override fun onDestroy() {
        super.destroy()
    }
}