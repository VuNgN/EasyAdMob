package com.vungn.admob.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.vungn.admob.util.MediaContent

class MediaView : FrameLayout {
    private val _mediaView: MediaView

    val mediaView: MediaView
        get() = _mediaView


    constructor(context: Context) : super(context) {
        _mediaView = MediaView(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        _mediaView = MediaView(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        _mediaView = MediaView(context, attrs, defStyleAttr)
        init()
    }

    private fun init() {
        addView(_mediaView)
    }

    fun setMediaContent(mediaContent: MediaContent?) {
        _mediaView.mediaContent = mediaContent?.mediaContent
    }
}
