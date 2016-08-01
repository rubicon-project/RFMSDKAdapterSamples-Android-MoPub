/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample;

import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;
import com.rfm.mopubadaptersample.sample.BaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MopubInterstitial extends BaseActivity implements InterstitialAdListener {

    private final String LOG_TAG = "MopubInterstitial";
    private Context mContext;
    private MoPubInterstitial mMoPubInterstitial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_interstitial);
        mContext = getApplicationContext();


        String interstitialAdUnitId = siteId;
        mMoPubInterstitial = new MoPubInterstitial(MopubInterstitial.this, interstitialAdUnitId);
        mMoPubInterstitial.setInterstitialAdListener(MopubInterstitial.this);
        mMoPubInterstitial.setKeywords(null);
        // Set location awareness and precision globally for your app:
        MoPub.setLocationAwareness(locationAwareness);
        MoPub.setLocationPrecision(locPrecision);

        updateAdView();
        setLoadAdAction();
    }

    @Override
    public void updateAdView() {
        mMoPubInterstitial.setTesting(adTestMode);
    }

    // InterstitialAdListener implementation
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        logToast(mContext, "Interstitial loaded.");
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.show();

            mNumberOfSuccess = mNumberOfSuccess + 1;
            updateCountersView();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        final String errorMessage = (errorCode != null) ? errorCode.toString() : "";
        logToast(mContext, "Interstitial failed to load: " + errorMessage);

        mNumberOfFailures = mNumberOfFailures + 1;
        updateCountersView();
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        logToast(mContext, "Interstitial shown.");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        logToast(mContext, "Interstitial clicked.");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        logToast(mContext, "Interstitial dismissed.");
    }


    private void logToast(Context context, String message) {
        super.appendTextToConsole(message);
        Log.d(LOG_TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.setInterstitialAdListener(null);
            mMoPubInterstitial.destroy();
            mMoPubInterstitial = null;
        }
    }

    @Override
    public void loadAd() {
        mMoPubInterstitial.load();

        mNumberOfRequests = mNumberOfRequests +1;
        updateCountersView();
    }

}
