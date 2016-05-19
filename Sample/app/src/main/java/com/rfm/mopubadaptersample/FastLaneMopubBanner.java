/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.MoPubView.BannerAdListener;
import com.rfm.mopubadaptersample.sample.BaseActivity;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMFastLane;

import java.util.Map;

public class FastLaneMopubBanner extends BaseActivity implements BannerAdListener {
	
	private final String LOG_TAG = "MopubBanner";
	private Context mContext;
	private MoPubView mMoPubView;

    private RFMFastLane rfmFastLane;
    private RFMAdRequest rfmAdRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_banner);
        mContext = getApplicationContext();
        
        mMoPubView = (MoPubView) findViewById(R.id.banner_mopubview);

        updateAdView();

        mMoPubView.setBannerAdListener(this);
        // Set location awareness and precision globally for your app:
        MoPub.setLocationAwareness(locationAwareness);
        MoPub.setLocationPrecision(locPrecision);

        setLoadAdAction();


        rfmFastLane = new RFMFastLane(this);
        rfmAdRequest = new RFMAdRequest();

    }

    @Override
    public void updateAdView() {
        if (mAdWidth != 0)
            mMoPubView.getLayoutParams().width = mAdWidth * displayDesity;
        if (mAdHeight != 0) {
            if (mAdHeight == -1) {
                mMoPubView.getLayoutParams().height = displayHeight;
                // will help to redraw view after setting it to device screen size
                //loadAd();
            } else {
                mMoPubView.getLayoutParams().height = mAdHeight * displayDesity;
            }
        }
        mMoPubView.setTesting(adTestMode);
    }

    private void createRFMFastLaneRequest() {
        if(rfmAdId != null && !rfmAdId.trim().equalsIgnoreCase("0")) {
            rfmAdRequest.setRFMTestAdId(rfmAdId);
        }
        rfmAdRequest.setRFMParams(rfmServer, rfmPubId, rfmAppId);
        rfmAdRequest.setAdDimensionParams(mAdWidth, mAdHeight);
    }

    private void loadMoPubView(final String adUnitId) {

        createRFMFastLaneRequest();

        rfmFastLane.preFetchAd(rfmAdRequest, new RFMFastLane.RFMFastLaneAdListener() {
            @Override
            public void onAdReceived(Map<String, String> serverExtras) {
                String keywords = "";
                if (serverExtras != null && serverExtras.size() > 0) {
                    Object firstKey = serverExtras.keySet().toArray()[0];
                    Object valueForFirstKey = serverExtras.get(firstKey);
                    keywords = firstKey + ":" + valueForFirstKey;
                }

                appendTextToConsole("FASTLANE onAdReceived " +
                        (serverExtras != null ? serverExtras.size() : "serverExtras is null!"));

                requestMopubAd(adUnitId, keywords);
            }

            @Override
            public void onAdFailed(String errorMessage) {
                appendTextToConsole("FASTLANE onAdFailed " +
                        (errorMessage != null ? errorMessage : ""));

                requestMopubAd(adUnitId, null);
            }
        });

    }

    private void requestMopubAd(final String adUnitId, final String keywords) {
        mMoPubView.setAdUnitId(adUnitId);
        mMoPubView.setKeywords(keywords);
        mMoPubView.loadAd();
    }

    @Override
    public void onBannerLoaded(MoPubView banner) {
        logToast(mContext, "Banner loaded.");

        mNumberOfSuccess = mNumberOfSuccess + 1;
        updateCountersView();
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        final String errorMessage = (errorCode != null) ? errorCode.toString() : "";
        logToast(mContext, "Banner failed to load: " + errorMessage);

        mNumberOfFailures = mNumberOfFailures + 1;
        updateCountersView();
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        logToast(mContext, "Banner clicked.");
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
        logToast(mContext, "Banner expanded.");
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
        logToast(mContext, "Banner collapsed.");
    }
	

    private void logToast(Context context, String message) {
        super.appendTextToConsole(message);
        Log.d(LOG_TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadAd() {
        String bannerAdUnitId = siteId;
        loadMoPubView(bannerAdUnitId);

        mNumberOfRequests = mNumberOfRequests + 1;
        updateCountersView();
    }

    @Override 
    public void onDestroy() { 
        super.onDestroy(); 
        if (mMoPubView != null) {
            mMoPubView.destroy();
            mMoPubView = null;
        }
    }

}
