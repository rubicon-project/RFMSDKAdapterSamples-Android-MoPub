package com.rfm.mopubadaptersample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;
import com.rfm.mopubadaptersample.sample.BaseActivity;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMFastLane;

import java.util.Map;

public class FastLaneMopubInterstitial extends BaseActivity implements InterstitialAdListener {
	
	private final String LOG_TAG = "FastLaneMopubInterstitial";
	private Context mContext;
	private MoPubInterstitial mMoPubInterstitial;

    private RFMFastLane rfmFastLane;
    private RFMAdRequest rfmAdRequest;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_interstitial);
        mContext = getApplicationContext();

        // Set location awareness and precision globally for your app:
        MoPub.setLocationAwareness(locationAwareness);
        MoPub.setLocationPrecision(locPrecision);

        updateAdView();
        setLoadAdAction();

        rfmFastLane = new RFMFastLane(this);
        rfmAdRequest = new RFMAdRequest();
    }

    @Override
    public void updateAdView() {

    }

    private void createRFMFastLaneRequest() {
        if(rfmAdId != null && !rfmAdId.trim().equalsIgnoreCase("0")) {
            rfmAdRequest.setRFMTestAdId(rfmAdId);
        }
        rfmAdRequest.setRFMParams(rfmServer, rfmPubId, rfmAppId);
        rfmAdRequest.setRFMAdAsInterstitial(true);
    }

    private void loadMoPubView() {

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

                requestMopubAd(keywords);
            }

            @Override
            public void onAdFailed(String errorMessage) {
                appendTextToConsole("FASTLANE onAdFailed " +
                        (errorMessage != null ? errorMessage : ""));

                requestMopubAd(null);
            }
        });

    }

    private void requestMopubAd(final String keywords) {
        String interstitialAdUnitId = siteId;
        mMoPubInterstitial = new MoPubInterstitial(FastLaneMopubInterstitial.this, interstitialAdUnitId);
        mMoPubInterstitial.setInterstitialAdListener(FastLaneMopubInterstitial.this);
        mMoPubInterstitial.setTesting(true);
        mMoPubInterstitial.setKeywords(keywords);
        mMoPubInterstitial.load();
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
        loadMoPubView();

        mNumberOfRequests = mNumberOfRequests +1;
        updateCountersView();
    }

}
