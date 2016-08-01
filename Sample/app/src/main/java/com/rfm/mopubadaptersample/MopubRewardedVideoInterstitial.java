/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample;

import android.os.Bundle;
import android.util.Log;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.rfm.mopubadaptersample.sample.BaseActivity;

import java.util.Set;

public class MopubRewardedVideoInterstitial extends BaseActivity {
	
	private final String LOG_TAG = "MopubRewardedVideoInterstitial";
    private MoPubRewardedVideoListener rewardedVideoListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_interstitial);

        MoPub.initializeRewardedVideo(this);
        MoPub.onCreate(this);

        rewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {
                // Called when the adUnitId has loaded. At this point you should be able to call MoPub.showRewardedVideo(String) to show the video
                MoPub.showRewardedVideo(siteId);

                log("Interstitial loaded.");

                mNumberOfSuccess = mNumberOfSuccess + 1;
                updateCountersView();
            }

            @Override
            public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {
                // Called when a video fails to load for the given ad unit id. The provided error code will provide more insight into the reason for the failure to load.

                log("Interstitial failed to load: " + errorCode);

                mNumberOfFailures = mNumberOfFailures + 1;
                updateCountersView();
            }

            @Override
            public void onRewardedVideoStarted(String adUnitId) {
                // Called when a rewarded video starts playing.
            }

            @Override
            public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
                //  Called when there is an error during video playback.
            }

            @Override
            public void onRewardedVideoClosed(String adUnitId) {
                // Called when a rewarded video is closed. At this point your application should resume.
            }

            @Override
            public void onRewardedVideoCompleted(Set adUnitIds, MoPubReward reward) {
                // Called when a rewarded video is completed and the user should be rewarded.
                // You can query the reward object with boolean isSuccessful(), String getLabel(), and int getAmount().
                if(reward != null) {
                    log("Rewarded video completed with reward " + reward.getLabel() + ":" +reward.getAmount());
                } else {
                    log("Rewarded video completed without reward ");
                }
            }
        };

        MoPub.setRewardedVideoListener(rewardedVideoListener);


        setLoadAdAction();
    }

    @Override
    public void updateAdView() {

    }

    private void log(String message) {
        super.appendTextToConsole(message);
        Log.d(LOG_TAG, message);
    }

	@Override 
    public void onDestroy() { 
		super.onDestroy();

        MoPub.onDestroy(this);
    }

    @Override
    public void loadAd() {
        MoPub.loadRewardedVideo(siteId);

        mNumberOfRequests = mNumberOfRequests +1;
        updateCountersView();
    }

}
