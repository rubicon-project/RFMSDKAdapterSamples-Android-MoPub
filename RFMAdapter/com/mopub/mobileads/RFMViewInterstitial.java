/*
 * Copyright (C) 2016 Rubicon Project. All rights reserved
 *
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with mopub SDK
 *  RFM SDK will be triggered via mopub Custom Events
 *
 */
package com.mopub.mobileads;

import com.rfm.sdk.RFMAdView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class RFMViewInterstitial extends Activity {
	
	private MoPubInterstitial mMoPubInterstitial;
	public static Activity mMopubRFMViewInterstitial;
	private int CONTAINDER_ID = 100;
	private FrameLayout mFrameLayout;
	private RFMAdView mRFMAdView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(CONTAINDER_ID);
        RelativeLayout.LayoutParams lpForRelativeLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        
        mFrameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lpForFrameLayout = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
        		ViewGroup.LayoutParams.FILL_PARENT,
        		Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        mFrameLayout.setLayoutParams(lpForFrameLayout);
        
        mRFMAdView = RFMMopubInterstitialAdapter.mRFMInterstitialAdView;

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (mRFMAdView.getParent() != null)
                ((ViewGroup) mRFMAdView.getParent()).removeView(mRFMAdView);
        }

        if (mFrameLayout.getChildCount() == 0)
        	mFrameLayout.addView(mRFMAdView);
  
        relativeLayout.addView(mFrameLayout);
 
        setContentView(relativeLayout, lpForRelativeLayout);
        
        mMopubRFMViewInterstitial = this;
 
    }
    
    public static void dismissActivity() {
    	mMopubRFMViewInterstitial.finish();
    }
    
	@Override 
    public void onDestroy() { 
        super.onDestroy();
        if (mFrameLayout != null && mRFMAdView != null)
        	mFrameLayout.removeView(mRFMAdView);
        
        RFMMopubInterstitialAdapter.mCustomEventInterstitialListener.onInterstitialDismissed();
        if (mMoPubInterstitial != null) {
        	mMoPubInterstitial.setInterstitialAdListener(null);
            mMoPubInterstitial.destroy();
            mMoPubInterstitial = null;
        }
    } 
	
}
