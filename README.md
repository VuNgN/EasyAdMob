# EasyAdMob

## Overview

EasyAdMob is a comprehensive library designed to simplify the integration and management of Google
AdMob ads in your Android applications. The library provides functionalities for handling rewarded
ads, native ads, and managing user privacy personalization states.

## Table of Contents

- [Overview](#overview)
- [Table of Contents](#table-of-contents)
- [Installation](#installation)
- [Usage](#usage)
    - [Check Google Ad Consent](#check-google-ad-consent)
    - [Add Ad Keys](#add-ad-keys)
    - [Initialize Ad](#initialize-ad)
    - [Load Ad](#load-ad)
- [Support and Contributions](#support-and-contributions)
- [License](#license)

## Installation

[![](https://jitpack.io/v/VuNgN/EasyAdMob.svg)](https://jitpack.io/#VuNgN/EasyAdMob)

### Gradle Kotlin DSL

1. Add Jitpack to your project-level `settings.gradle.kts`:
    ```kotlin
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories { 
            ...
            maven { url = uri("https://jitpack.io") }
       }
   }
    ```

2. Add the library dependency to your module-level `build.gradle.kts`:
    ```kotlin
    dependencies {
        implementation("com.github.VuNgN:EasyAdMob:latest-version")
    }
    ```

### Gradle

1. Add Jitpack to your project-level `settings.gradle`:
    ```groovy
    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
    ```

2. Add the library dependency to your module-level `build.gradle`:
    ```groovy
    dependencies {
        implementation 'com.github.VuNgN:EasyAdMob:latest-version'
    }
    ```

### Maven

1. Add Jitpack to your `pom.xml`:
    ```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```

2. Add the library dependency:
    ```xml
    <dependency>
        <groupId>com.github.VuNgN</groupId>
        <artifactId>EasyAdMob</artifactId>
        <version>latest-version</version>
    </dependency>
    ```

## Usage

### Add your AdMob app ID

Add your AdMob app ID, as identified in the AdMob web interface, to your app's `AndroidManifest.xml`
file.
To do so, add a `<meta-data>` tag with `android:name="com.google.android.gms.ads.APPLICATION_ID"`.
You can find your app ID in the AdMob web interface. For `android:value`, insert your own AdMob app
ID, surrounded by quotation marks.

```xml

<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <application>
        <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" />
    </application>
</manifest>
```

DON'T forget to request `android.permission.INTERNET` permission.

### Check Google Ad Consent

Before loading ads, ensure that you have checked the user's consent for personalized ads.

```kotlin
val testDeviceIds = listOf("5051A79634A2CE2BE8EAC9A44E4AD2E7")
GoogleAdsConsentManager.getInstance(this).gatherConsent(activity = this,
    timeout = 1500,
    debug = true, // for testing
    testDeviceIds = testDeviceIds, // for testing
    listener = object : GoogleAdsConsentManager.GatherConsentListener {
        override fun onCanShowAds() {
            // do something if the app can request ads.
        }

        override fun onDisableAds() {
            // do something if ads are disabled.
        }
    })
```

If `debug` is `true` and `testDeviceIds` has your device id, your app will ask for consent everytime
you launch it.

### Add Ad Keys

You can load ad unit configurations from a JSON file placed in the raw directory or add them
directly in your code.

#### Loading from JSON

`adkey` JSON file example:

```json
{
  "APP_BANNER_AD_KEY": "ca-app-pub-3940256099942544/6300978111",
  "APP_INTERSTITIAL_AD_KEY": "ca-app-pub-3940256099942544/1033173712",
  "APP_REWARDED_AD_KEY": "ca-app-pub-3940256099942544/5224354917",
  "APP_NATIVE_AD_KEY": "ca-app-pub-3940256099942544/2247696110",
  "APP_OPEN_AD_KEY": "ca-app-pub-3940256099942544/9257395921"
}
```

In your code:

```kotlin
AdMobeConfig.loadKeys(R.raw.adkey, resources)
```

#### Adding Directly

```kotlin
AdMobeConfig.loadKeys(
    bannerKey = "ca-app-pub-3940256099942544/6300978111",
    interstitialKey = "ca-app-pub-3940256099942544/1033173712",
    nativeKey = "ca-app-pub-3940256099942544/2247696110",
    rewardedKey = "ca-app-pub-3940256099942544/5224354917",
    openKey = "ca-app-pub-3940256099942544/9257395921",
)
```

### Initialize Ad

Initialize the ad manager in your activity or application class.

```kotlin
val backgroundScope = CoroutineScope(Dispatchers.IO)
backgroundScope.launch {
    AdMobeConfig.initAds()
}
```

### Load Ad

Load the ad using the ad manager.

#### App open

Init `AppOpenAdManager`:

```kotlin
var adManager: AppOpenAdManager? = null

adManager = AppOpenAdManager.Builder(this).setTimeout(3000).addListener(
    object : AppOpenAdManager.OpenAdListener {
        override fun onStateChange(manager: AppOpenAdManager, state: AppOpenAdManager.State) {
            when (state) {
                AppOpenAdManager.State.LOADING -> {}
                AppOpenAdManager.State.NOT_LOADED -> {}
                AppOpenAdManager.State.LOADED -> {}
                AppOpenAdManager.State.SHOWING -> {}
                AppOpenAdManager.State.CLOSED -> {}
                else -> {}
            }
        }
    }
).build()
```

Load ad:

```kotlin
adManager?.loadAd()
```

Show ad:

```kotlin
adManager?.showAd()
```

#### Banner

```kotlin
val adViewContainer = findViewById<FrameLayout>(R.id.bannerAd)
val adManager = AppBannerAdManager(this)
adManager?.loadAd(
    adViewContainer = adViewContainer,
    lifecycle = lifecycle,
    isCollapse = true,
    listener = object : AppBannerAdManager.BannerAdLoadListener() {
        override fun onAdLoaded() {
            // Code to be executed when an ad finishes loading.
        }
    }
)
```

#### Interstitial

Init `AppInterstitialAdManager`:

```kotlin
 val adManager = AppInterstitialAdManager.Builder(this).setTimeout(3000).addListener(
    object : AppInterstitialAdManager.InterstitialAdListener {
        override fun onStateChange(state: AppInterstitialAdManager.State) {
            when (state) {
                AppInterstitialAdManager.State.LOADING -> {}
                AppInterstitialAdManager.State.NOT_LOADED -> {}
                AppInterstitialAdManager.State.LOADED -> {}
                AppInterstitialAdManager.State.CLOSED -> {}
                else -> {}
            }
        }
    }
).build()
```

Load ad:

```kotlin
adManager.loadAd()
```

Show ad:

```kotlin
adManager.showAd()
```

#### Rewarded

Init `AppRewardedAdManager`:

```kotlin
adManager = AppRewardedAdManager.Builder(this).setTimeout(3000)
    .addListener(object : AppRewardedAdManager.AppRewardedAdListener {
        override fun onStateChange(state: AppRewardedAdManager.State) {
            when (state) {
                AppRewardedAdManager.State.CLOSED -> {}
                else -> {}
            }
        }

        override fun onAdClicked() {}

        override fun onUserEarnedReward(rewardItem: RewardItem) {
            // Code to be executed when the user earns a reward.
        }
    }).build()
```

Load ad:

```kotlin
adManager.loadAd()
```

Show ad:

```kotlin
adManager.showAd()
```

#### Native

```kotlin
val nativeAdManager = AppNativeAdManager(this)
nativeAdManager.loadAd(
    videoMuted = false,
    listener = object : AppNativeAdManager.NativeAdLoadListener() {
        override fun onAdLoaded(currentNativeAd: NativeAd) {
            val frameLayout = findViewById<FrameLayout>(R.id.fl_adplaceholder)
            val adView: NativeAdView =
                layoutInflater.inflate(
                    R.layout.ad_unified,
                    frameLayout,
                    false
                ) as NativeAdView
            populateNativeAdView(currentNativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }

        override fun onAdFailedToLoad() {
            // Code to be executed when an ad fails to load.
        }
    })
```

`ad_unified.xml` example:

```xml
<com.vungn.admob.view.NativeAdView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="#FFFFFF"
        android:minHeight="50dp"
        android:orientation="vertical">

        <TextView style="@style/AppTheme.AdAttribution" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingLeft="20dp"
            android:paddingTop="3dp"
            android:paddingRight="20dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <ImageView
                    android:id="@+id/ad_app_icon"
                    android:layout_width="40dp"
                    android:layout_height="40dp"
                    android:adjustViewBounds="true"
                    android:paddingEnd="5dp"
                    android:paddingRight="5dp"
                    android:paddingBottom="5dp" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:id="@+id/ad_headline"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:textColor="#0000FF"
                        android:textSize="16sp"
                        android:textStyle="bold" />

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content">

                        <TextView
                            android:id="@+id/ad_advertiser"
                            android:layout_width="wrap_content"
                            android:layout_height="match_parent"
                            android:gravity="bottom"
                            android:textSize="14sp"
                            android:textStyle="bold" />

                        <RatingBar
                            android:id="@+id/ad_stars"
                            style="?android:attr/ratingBarStyleSmall"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:isIndicator="true"
                            android:numStars="5"
                            android:stepSize="0.5" />
                    </LinearLayout>

                </LinearLayout>
            </LinearLayout>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

                <TextView
                    android:id="@+id/ad_body"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="20dp"
                    android:layout_marginRight="20dp"
                    android:textSize="12sp" />

                <com.vungn.admob.view.MediaView
                    android:id="@+id/ad_media"
                    android:layout_width="250dp"
                    android:layout_height="175dp"
                    android:layout_gravity="center_horizontal"
                    android:layout_marginTop="5dp" />

                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="end"
                    android:orientation="horizontal"
                    android:paddingTop="10dp"
                    android:paddingBottom="10dp">

                    <TextView
                        android:id="@+id/ad_price"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:paddingStart="5dp"
                        android:paddingLeft="5dp"
                        android:paddingEnd="5dp"
                        android:paddingRight="5dp"
                        android:textSize="12sp" />

                    <TextView
                        android:id="@+id/ad_store"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:paddingStart="5dp"
                        android:paddingLeft="5dp"
                        android:paddingEnd="5dp"
                        android:paddingRight="5dp"
                        android:textSize="12sp" />

                    <Button
                        android:id="@+id/ad_call_to_action"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:gravity="center"
                        android:textSize="12sp" />
                </LinearLayout>
            </LinearLayout>
        </LinearLayout>
    </LinearLayout>
</com.vungn.admob.view.NativeAdView>

```

`populateNativeAdView()` example:

```kotlin
 /**
 * Populates a [NativeAdView] object with data from a given [NativeAd].
 *
 * @param nativeAd the object containing the ad's assets
 * @param adView   the view to be populated
 */
private fun populateNativeAdView(
    nativeAd: NativeAd,
    adView: NativeAdView
) {
    // Set the media view.
    adView.setMediaView(adView.findViewById<View>(R.id.ad_media) as MediaView)

    // Set other ad assets.
    adView.setHeadlineView(adView.findViewById<View>(R.id.ad_headline))
    adView.setBodyView(adView.findViewById<View>(R.id.ad_body))
    adView.setCallToActionView(adView.findViewById<View>(R.id.ad_call_to_action))
    adView.setIconView(adView.findViewById<View>(R.id.ad_app_icon))
    adView.setPriceView(adView.findViewById<View>(R.id.ad_price))
    adView.setStarRatingView(adView.findViewById<View>(R.id.ad_stars))
    adView.setStoreView(adView.findViewById<View>(R.id.ad_store))
    adView.setAdvertiserView(adView.findViewById<View>(R.id.ad_advertiser))

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    (adView.getHeadlineView() as TextView).text = nativeAd.getHeadline()
    adView.getMediaView()?.setMediaContent(nativeAd.getMediaContent())

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.getBody() == null) {
        adView.getBodyView()?.visibility = View.INVISIBLE
    } else {
        adView.getBodyView()?.visibility = View.VISIBLE
        (adView.getBodyView() as TextView).text = nativeAd.getBody()
    }

    if (nativeAd.getCallToAction() == null) {
        adView.getCallToActionView()?.visibility = View.INVISIBLE
    } else {
        adView.getCallToActionView()?.visibility = View.VISIBLE
        (adView.getCallToActionView() as Button).text = nativeAd.getCallToAction()
    }

    if (nativeAd.getIcon() == null) {
        adView.getIconView()?.visibility = View.GONE
    } else {
        (adView.getIconView() as ImageView).setImageDrawable(
            nativeAd.getIcon()?.getDrawable()
        )
        adView.getIconView()?.visibility = View.VISIBLE
    }

    if (nativeAd.getPrice() == null) {
        adView.getPriceView()?.visibility = View.INVISIBLE
    } else {
        adView.getPriceView()?.visibility = View.VISIBLE
        (adView.getPriceView() as TextView).text = nativeAd.getPrice()
    }

    if (nativeAd.getStore() == null) {
        adView.getStoreView()?.visibility = View.INVISIBLE
    } else {
        adView.getStoreView()?.visibility = View.VISIBLE
        (adView.getStoreView() as TextView).text = nativeAd.getStore()
    }

    if (nativeAd.getStarRating() == null) {
        adView.getStarRatingView()?.visibility = View.INVISIBLE
    } else {
        (adView.getStarRatingView() as RatingBar).rating =
            nativeAd.getStarRating()?.toFloat() ?: 0f
        adView.getStarRatingView()?.visibility = View.VISIBLE
    }

    if (nativeAd.getAdvertiser() == null) {
        adView.getAdvertiserView()?.visibility = View.INVISIBLE
    } else {
        (adView.getAdvertiserView() as TextView).text = nativeAd.getAdvertiser()
        adView.getAdvertiserView()?.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc: VideoController? = nativeAd.getMediaContent()?.getVideoController()

    // Updates the UI to say whether or not this ad has a video asset.
    if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent()
            ?.hasVideoContent() == true
    ) {
        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.

        vc?.setVideoLifecycleCallbacks(object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
                // Publishers should allow native ads to complete video playback before
                // refreshing or replacing them with another ad in the same UI location.
                videoStatus.text = "Video status: Video playback has ended."
                super.onVideoEnd()
            }
        })
    } else {
        videoStatus.text = "Video status: Ad does not contain a video asset."
    }
}
```

## Support and Contributions
Support it by joining [stargazers](https://github.com/VuNgN/EasyAdMob/stargazers) for this repository. ⭐</br>
And [follow](https://github.com/VuNgN) me for my next creations! 🤩

## License
```text
Copyright 2024 VuNgN (Nguyễn Ngọc Vũ)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
