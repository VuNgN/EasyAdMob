package com.vungn.admob.util

import com.google.android.gms.ads.VideoController

class VideoController(val videoController: VideoController?) {
    fun setVideoLifecycleCallbacks(listener: VideoLifecycleCallbacks) {
        val gmsListener = object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
                listener.onVideoEnd()
            }

            override fun onVideoMute(p0: Boolean) {
                listener.onVideoMute(p0)
            }

            override fun onVideoPause() {
                listener.onVideoPause()
            }

            override fun onVideoPlay() {
                listener.onVideoPlay()
            }

            override fun onVideoStart() {
                listener.onVideoStart()
            }
        }
        videoController?.videoLifecycleCallbacks = gmsListener
    }

    abstract class VideoLifecycleCallbacks {
        open fun onVideoEnd() {
        }

        fun onVideoMute(var1: Boolean) {
        }

        open fun onVideoPause() {
        }

        fun onVideoPlay() {
        }

        open fun onVideoStart() {
        }
    }
}