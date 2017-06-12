/*
 * Copyright (C) 2016 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with mopub SDK
 *  RFM SDK will be triggered via mopub Custom Events
 * 
 */
package com.mopub.mobileads;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mopub.common.util.Views;
import com.mopub.mobileads.CustomEventBanner;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;

public class RFMMopubBannerAdapter extends CustomEventBanner {
	
	private static final String LOG_TAG = "RFMMopubBannerAdapter";

	private boolean ENABLE_DEBUG_LOG = true;

	private CustomEventBannerListener mCustomEventBannerListener;
	private Context mContext;
	
	private RFMAdView mRFMBannerAdView;
	private RFMAdRequest mRFMAdRequest;

	private static final String RFM_SERVER_NAME = "rfm_server_name";
	private static final String RFM_PUB_ID = "rfm_pub_id";
	private static final String RFM_APP_ID = "rfm_app_id";
	private static final String RFM_AD_ID = "rfm_ad_id";
	private static final String RFM_AD_HEIGHT = "rfm_ad_height";
	private static final String RFM_AD_WIDTH = "rfm_ad_width";
	
	private String mRFMServerName = "";
	private String mRFMPubId = "";
	private String mRFMAppId = "";
	private String mRFMAdId = "";
	
	private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();
	
	public RFMMopubBannerAdapter() {
		localTargetingInfoHM.put("adp_version", "mp_adp_3.0.1");
	}

	@Override
    protected void loadBanner( 
            Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {

		mCustomEventBannerListener = customEventBannerListener;
		mContext = context;

		if (mContext == null) {
			mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
	        return; 
	    }
		
		if (isExtrasValid(serverExtras)) {
			mRFMServerName = serverExtras.get(RFM_SERVER_NAME);
			mRFMPubId = serverExtras.get(RFM_PUB_ID);
			mRFMAppId = serverExtras.get(RFM_APP_ID);
			mRFMAdId = serverExtras.get(RFM_AD_ID);
		} else {
			mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
			log(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.toString());
			return;
		}
		
        if(mRFMBannerAdView == null){
        	mRFMBannerAdView = new RFMAdView(mContext);
        }

        if(mRFMAdRequest == null) 
        	mRFMAdRequest = new RFMAdRequest();

        if (mRFMAdId != null && !mRFMAdId.equals("")) {
        	mRFMAdRequest.setRFMTestAdId(mRFMAdId);
        }

//        mRFMAdRequest.setRFMAdMode("test");

        mRFMAdRequest.setRFMParams(mRFMServerName, mRFMPubId, mRFMAppId);
        
        if (serverExtras.containsKey(RFM_AD_WIDTH) && serverExtras.containsKey(RFM_AD_HEIGHT)) {
        	int adWidth = Integer.parseInt(serverExtras.get(RFM_AD_WIDTH));
        	int adHeight = Integer.parseInt(serverExtras.get(RFM_AD_HEIGHT));
        	mRFMAdRequest.setAdDimensionParams(adWidth, adHeight);
        }
        
        HashMap<String, String> targetingParamsHM = getTargetingParams(serverExtras);
        mRFMAdRequest.setTargetingParams(targetingParamsHM);
        
        setRFMAdViewListener();

        boolean success = mRFMBannerAdView.requestRFMAd(mRFMAdRequest);
        if(!success) {
        	log("ad request denied");
        	mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        } else {
        	log("ad request accepted, waiting for ad");
        }
	}

	private void setRFMAdViewListener() {
		if(mRFMBannerAdView == null){
			return;
		}
		
		//Optional listener for RFMAd status
 	   	mRFMBannerAdView.setRFMAdViewListener(new RFMAdViewListener() {
 			@Override
 			public void didDisplayAd(RFMAdView arg0) {
 				log("RFM Ad: displayed ");
 			}

 			@Override
 			public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
 				log("RFM Ad: Could not be displayed ");
 			}

 			@Override
 			public void onAdFailed(RFMAdView arg0) {
 				log("RFM Ad: Failed");
 				mRFMBannerAdView.setVisibility(View.GONE);
 				mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
 			}

 			@Override
 			public void onAdReceived(RFMAdView arg0) {
 				log("RFM Ad: Received");
				mRFMBannerAdView.setVisibility(View.VISIBLE);
 				mCustomEventBannerListener.onBannerLoaded(mRFMBannerAdView);
 			}

 			@Override
 			public void onAdRequested(String requestUrl, boolean adRequestSuccess) {
 				log("RFM Ad: Requesting Url:" + requestUrl);	
 			}

 			@Override
 			public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
 				log("RFM Ad: resized to width " + arg1 + ", height = " + arg2);
 			}

 			@Override
 			public  void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
				switch(event){
					case FULL_SCREEN_AD_DISPLAYED:
						log("RFM Ad: Full screen ad displayed");
						break;
					case FULL_SCREEN_AD_DISMISSED:
						log("RFM Ad: Full screen ad dismissed");
						break;
					case AD_CLICKED:
						log("RFM Ad: Banner Ad clicked");
						mCustomEventBannerListener.onBannerClicked();
						break;
					default:
						break;
				}
 			}

		});
	}
	
	// mopub CustomEventBanner callback
	@Override
	protected void onInvalidate() {
		log("CustomEventBanner: onInvalidate");
		Views.removeFromParent(mRFMBannerAdView);
		try {
			if (mRFMBannerAdView != null) {
				mRFMBannerAdView.setRFMAdViewListener(null);
				mRFMBannerAdView.rfmAdViewDestroy();
			}
		} catch (Exception e) {
			log("CustomeEventBanner: Failed to clean custom banner with error,  "+e.toString());
		}
	}

	private boolean isExtrasValid(Map<String, String> serverExtras) {
		if (serverExtras.containsKey(RFM_SERVER_NAME) && serverExtras.containsKey(RFM_PUB_ID) && 
				serverExtras.containsKey(RFM_APP_ID)) {
			return true;
		}
		return false;
	}

	private HashMap<String, String> getTargetingParams(Map<String, String> map) {
		HashMap<String, String> targetingHM = new HashMap<String, String>();
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
	
    private void log(String message) {
    	if (ENABLE_DEBUG_LOG)
    		Log.d(LOG_TAG, message);
    }

}