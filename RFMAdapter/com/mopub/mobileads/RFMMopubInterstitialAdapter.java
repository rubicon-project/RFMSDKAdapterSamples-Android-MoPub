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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.rfm.sdk.AdState;

import com.mopub.common.util.Views;
import com.mopub.mobileads.CustomEventInterstitial;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMInterstitialAdViewListener;

public class RFMMopubInterstitialAdapter extends CustomEventInterstitial {
	
	private static final String LOG_TAG = "RFMMopubInterstitialAdapter";
	public static CustomEventInterstitialListener mCustomEventInterstitialListener;
	private Context mContext;
	
	private boolean ENABLE_DEBUG_LOG = false;
	
	public static RFMAdView mRFMInterstitialAdView;
	private RFMAdRequest mRFMAdRequest;

	private static final String RFM_SERVER_NAME = "rfm_server_name";
	private static final String RFM_PUB_ID = "rfm_pub_id";
	private static final String RFM_APP_ID = "rfm_app_id";
	private static final String RFM_AD_ID = "rfm_ad_id";
	
	private String mRFMServerName = "";
	private String mRFMPubId = "";
	private String mRFMAppId = "";
	private String mRFMAdId = "";
	
	private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();
	
	RFMMopubInterstitialAdapter() {
		localTargetingInfoHM.put("adp_version", "mp_adp_1.2.0");
	}
	
	@Override
	protected void loadInterstitial(
			Context context,
			CustomEventInterstitialListener customEventInterstitialListener,
			Map<String, Object> localExtras,
			Map<String, String> serverExtras) {

		mCustomEventInterstitialListener = customEventInterstitialListener;
		mContext = context;
		
		if (mContext == null) {
			mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
	        return; 
	    }


		if (isExtrasValid(serverExtras)) {
			mRFMServerName = serverExtras.get(RFM_SERVER_NAME);
			mRFMPubId = serverExtras.get(RFM_PUB_ID);
			mRFMAppId = serverExtras.get(RFM_APP_ID);
			mRFMAdId = serverExtras.get(RFM_AD_ID);
		} else {
			mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
			log(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.toString());
			return;
		}
		
        if(mRFMInterstitialAdView == null){
        	mRFMInterstitialAdView = new RFMAdView(mContext);
        }


        if(mRFMAdRequest == null) 
        	mRFMAdRequest = new RFMAdRequest();
   		
        //mRFMAdRequest.setRFMAdMode("test");
        
        if (mRFMAdId != null && !mRFMAdId.equals("")) {
        	mRFMAdRequest.setRFMTestAdId(mRFMAdId);
        }
        
        mRFMAdRequest.setRFMParams(mRFMServerName, mRFMPubId, mRFMAppId);
        
        HashMap<String, String> targetingParamsHM = getTargetingParams(serverExtras);
        mRFMAdRequest.setTargetingParams(targetingParamsHM);
        mRFMAdRequest.setRFMAdAsInterstitial(true);
        mRFMInterstitialAdView.setScrollContainer(true);
        mRFMInterstitialAdView.enableHWAcceleration(true);
        
        setRFMAdViewListener();
		
		boolean success = mRFMInterstitialAdView.requestRFMAd(mRFMAdRequest);
        if(!success) {
        	log("ad request denied");
        	mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        } else {
        	log("ad request accepted, waiting for ad");
        }
        
	}

	private void setRFMAdViewListener() {
		if(mRFMInterstitialAdView == null){
			return;
		}
		
	    //Optional listener for RFMAd status
 	   	mRFMInterstitialAdView.setRFMAdViewListener(new RFMInterstitialAdViewListener() {
 		   @Override
 		   public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
 			   log("RFM Ad: Requesting Url:" + requestUrl);
 		   }
		    
 		   @Override
 		   public void onAdReceived(RFMAdView adView){
 			   log("RFM Ad: Received");
 			   mCustomEventInterstitialListener.onInterstitialLoaded();
 		   }
 		   
 		   @Override
 		   public void onAdFailed(RFMAdView adView){
 			   log("RFM Ad: Failed");
 			   mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
 		   }
			
 		   @Override
 		   public void onInterstitialAdWillDismiss(RFMAdView adView) {
 			   log("RFM Ad: Interstitial will dismiss");
 		   }
			
 		   @Override
 		   public void onInterstitialAdDismissed(RFMAdView adView) {
 			   log("RFM Ad: Interstitial ad dismissed");
 			   RFMViewInterstitial.dismissActivity();
 		   }

 		   @Override
 		   public void onAdStateChangeEvent(RFMAdView arg0, RFMAdViewEvent event) {
 			   switch(event){
 			  		case FULL_SCREEN_AD_DISPLAYED:
 			  			log("RFM Ad: Full screen ad displayed");
 			  			mCustomEventInterstitialListener.onInterstitialClicked();
 			  			break;
 			  		case FULL_SCREEN_AD_DISMISSED:
 			  			log("RFM Ad: Full screen ad dismissed");
 			  			break;
 			  		default:
 			  			break;  
 			   }
 		   }

 		   @Override
 		   public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
 			   log("RFM Ad: Resized");
 		   }

 		   @Override
 		   public void didDisplayAd(RFMAdView arg0) {
 			   log("RFM Ad: Ad displayed");
 			   mCustomEventInterstitialListener.onInterstitialLoaded();
 		   }

		   @Override
		   public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
			   log("RFM Ad: Failed to display Ad");
		   }

		});
	}
	
	private void showFullScreenRFMInterstitial() {
		Intent i = new Intent(mContext.getApplicationContext(), RFMViewInterstitial.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		((Activity) mContext).startActivity(i);
	}
	
	@Override
	protected void showInterstitial() {
        if(mRFMInterstitialAdView.isAdAvailableToDisplay() ||
                mRFMInterstitialAdView.getAdStateRO().getCurrentState() == AdState.AdViewState.INTERSTITIAL_DISP) {
        	showFullScreenRFMInterstitial();
        	mCustomEventInterstitialListener.onInterstitialShown();
        }
	}
	
	// mopub CustomEventBanner callback
	@Override
	protected void onInvalidate() {
		log("CustomEventInterstitial: onInvalidate");
		Views.removeFromParent(mRFMInterstitialAdView);
		try {
			if (mRFMInterstitialAdView != null) {
				mRFMInterstitialAdView.setRFMAdViewListener(null);
				mRFMInterstitialAdView.rfmAdViewDestroy();
				mRFMInterstitialAdView = null;
			}
		} catch (Exception e) {
			log("CustomEventInterstitial: Failed to clean custom interstitial with error,  "+e.toString());
		}
		mCustomEventInterstitialListener = null;
	}
	
	@SuppressWarnings("deprecation")
	public static int getFillParentLP(){
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.FROYO){
			return ViewGroup.LayoutParams.MATCH_PARENT;
		}else{
			return ViewGroup.LayoutParams.FILL_PARENT;		
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