package com.vungn.admob.util

import com.google.android.gms.ads.MediaContent

class MediaContent(val mediaContent: MediaContent?) {
    fun getAspectRatio(): Float? {
        return mediaContent?.aspectRatio
    }

    fun hasVideoContent(): Boolean {
        return mediaContent?.hasVideoContent() ?: false
    }

    fun getVideoController(): VideoController {
        return VideoController(mediaContent?.videoController)
    }
}
