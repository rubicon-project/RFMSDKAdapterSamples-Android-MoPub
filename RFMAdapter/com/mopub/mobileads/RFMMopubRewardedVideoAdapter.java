/*
 * Copyright (C) 2016 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with mopub SDK
 *  RFM SDK will be triggered via mopub Custom Events
 * 
 */
package com.mopub.mobileads;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMRewardedVideo;

import java.util.HashMap;
import java.util.Map;

public class RFMMopubRewardedVideoAdapter extends CustomEventRewardedVideo {

	private final String LOG_TAG = "RFMMopubRewardedVideo";
	private static final String RFM_AD_NETWORK_CONSTANT = "com.rfm.sdk";

	private boolean ENABLE_DEBUG_LOG = false;

	private final String RFM_SERVER_NAME = "rfm_server_name";
	private final String RFM_PUB_ID = "rfm_pub_id";
	private final String RFM_APP_ID = "rfm_app_id";
	private final String RFM_AD_ID = "rfm_ad_id";

	private String mRFMServerName = "";
	private String mRFMPubId = "";
	private String mRFMAppId = "";
	private String mRFMAdId = "";

	private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();

	public RFMMopubRewardedVideoAdapter() {
		super();
		localTargetingInfoHM.put("adp_version", "mp_adp_1.3.0");
	}

	private RFMRewardedVideoListener rfmListener = new RFMRewardedVideoListener();

	private class RFMRewardedVideoListener implements RFMRewardedVideo.LoadRewardedVideoListener, CustomEventRewardedVideoListener {
		@Override
		public void onRewardedVideoAdLoadSuccess(String appId) {
			log("RFM Ad: Received");
			MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
					RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT);
		}

		@Override
		public void onRewardedVideoAdLoadFailure(String appId, String errorMessage) {
			log("RFM Ad: Failed " + errorMessage);
			MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
					RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT, MoPubErrorCode.NETWORK_NO_FILL);
		}
	}

	@Nullable
	@Override
	protected CustomEventRewardedVideoListener getVideoListenerForSdk() {
		return rfmListener;
	}

	@Nullable
	@Override
	protected LifecycleListener getLifecycleListener() {
		return null;
	}

	@NonNull
	@Override
	protected String getAdNetworkId() {
		return RFM_AD_NETWORK_CONSTANT;
	}

	@Override
	protected void onInvalidate() {
		RFMRewardedVideo.destroy(mRFMAppId);
		RFMRewardedVideo.destroyAll();
	}

	@Override
	protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
		return true;
	}

	@Override
	protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {

		if (isExtrasValid(serverExtras)) {
			mRFMServerName = serverExtras.get(RFM_SERVER_NAME);
			mRFMPubId = serverExtras.get(RFM_PUB_ID);
			mRFMAppId = serverExtras.get(RFM_APP_ID);
			mRFMAdId =  serverExtras.get(RFM_AD_ID);
		} else {
			log(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.toString());
			return;
		}

		// create RewardedVideo ad request
		RFMAdRequest request = createInterstitialRequest();
		HashMap<String, String> targetingParamsHM = getTargetingParams(serverExtras);
		request.setTargetingParams(targetingParamsHM);

		// Request RewardedVideo Ad
		boolean success = RFMRewardedVideo.requestAd(activity, request, rfmListener);

		if (success) {
			log("ad request accepted, waiting for ad");
		} else {
			log("ad request denied");
		}
	}

	private RFMAdRequest createInterstitialRequest() {
		RFMAdRequest mAdRequest = new RFMAdRequest();
		mAdRequest.setRFMParams(mRFMServerName, mRFMPubId, mRFMAppId);
		mAdRequest.setRFMAdAsInterstitial(true);
		mAdRequest.fetchVideoAds(true);

		if (!"0".equalsIgnoreCase(mRFMAdId)) {
			mAdRequest.setRFMTestAdId(mRFMAdId);
		}

		return mAdRequest;
	}

	@Override
	protected boolean hasVideoAvailable() {
		return true;
	}

	@Override
	protected void showVideo() {
		// show rewarded video ad
		RFMRewardedVideo.show(mRFMAppId, new RFMRewardedVideo.ShowRewardedVideoListener() {
			@Override
			public void onRewardedVideoAdStarted(String appId) {
				log("RFM Ad: Video Started");
				MoPubRewardedVideoManager.onRewardedVideoStarted(RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT);
			}

			@Override
			public void onRewardedVideoAdPlaybackError(String appId, String errorMessage) {
				log("RFM Ad: Playback error " + errorMessage);
				MoPubRewardedVideoManager.onRewardedVideoPlaybackError(RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT, MoPubErrorCode.VIDEO_PLAYBACK_ERROR);
			}

			@Override
			public void onRewardedVideoAdCompleted(String appId, String reward) {
				log("RFM Ad: Ad displayed");
				log("\nreward : " + reward);
				MoPubRewardedVideoManager.onRewardedVideoCompleted(RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT, MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.NO_REWARD_AMOUNT));
			}

			@Override
			public void onRewardedVideoAdClosed(String appId) {
				log("RFM Ad: Ad Closed");
				MoPubRewardedVideoManager.onRewardedVideoClosed(RFMMopubRewardedVideoAdapter.class, RFM_AD_NETWORK_CONSTANT);
			}
		});

	}

	private boolean isExtrasValid(Map<String, String> serverExtras) {
		if (serverExtras.containsKey(RFM_SERVER_NAME) && serverExtras.containsKey(RFM_PUB_ID) &&
				serverExtras.containsKey(RFM_APP_ID)) {
			return true;
		}
		return false;
	}

	private void log(String message) {
		if (ENABLE_DEBUG_LOG)
			Log.d(LOG_TAG, message);
	}

	private HashMap<String, String> getTargetingParams(Map<String, String> map) {
		HashMap<String, String> targetingHM = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equals(RFM_SERVER_NAME) || key.equals(RFM_PUB_ID) || key.equals(RFM_APP_ID) || key.equals(RFM_AD_ID)) {
				// create targetingHM without these keys
			} else {
				targetingHM.put(key, value);
			}
		}
		targetingHM.putAll(localTargetingInfoHM);
		return targetingHM;
	}
}