/*
 * Copyright (C) 2016 Rubicon Project. All rights reserved
 *
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with mopub SDK
 *  RFM SDK will be triggered via mopub Custom Events
 *
 */
package com.mopub.mobileads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.mopub.nativeads.CustomEventNative;
import com.mopub.nativeads.ImpressionTracker;
import com.mopub.nativeads.NativeClickHandler;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.StaticNativeAd;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.mopub.network.TrackingRequest;
import com.rfm.sdk.RFMNativeAd;
import com.rfm.sdk.RFMNativeAdRequest;
import com.rfm.sdk.RFMNativeAdResponse;
import com.rfm.sdk.RFMNativeAssets;

public class RFMMopubNativeAdapter extends CustomEventNative {
        private String mRFMServerName = "";
        private String mRFMPubId = "";
        private String mRFMAppId = "";
        private String mRFMAdId = "";
        private CustomEventNativeListener mCustomEventNativeListener;
        private Context mContext;
        private static final String RFM_SERVER_NAME = "rfm_server_name";
        private static final String RFM_PUB_ID = "rfm_pub_id";
        private static final String RFM_APP_ID = "rfm_app_id";
        private static final String RFM_AD_ID = "rfm_ad_id";
        private static final String RFM_NATIVE_SAMPLE = "native-sample";
        private static final String LOG_TAG = "RFMMopubNativeAdapter";
        public static final String SPONSORED_FOR_AD = "sponsoredText";
        private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();
        private static boolean ENABLE_DEBUG_LOG = true;

        public RFMMopubNativeAdapter() {
            localTargetingInfoHM.put("adp_version", "mp_adp_2.0.0");
        }

        // CustomEventNative implementation
        @Override
        protected void loadNativeAd(@NonNull final Context context,
                                    @NonNull final CustomEventNativeListener customEventNativeListener,
                                    @NonNull final Map<String, Object> localExtras,
                                    @NonNull final Map<String, String> serverExtras) {

            mCustomEventNativeListener = customEventNativeListener;
            mContext = context;

            String nativeSample;
            if (isExtrasValid(serverExtras)) {
                mRFMServerName = serverExtras.get(RFM_SERVER_NAME);
                mRFMPubId = serverExtras.get(RFM_PUB_ID);
                mRFMAppId = serverExtras.get(RFM_APP_ID);
                mRFMAdId = serverExtras.get(RFM_AD_ID);
                nativeSample = serverExtras.get(RFM_NATIVE_SAMPLE); // Server extra 'native-sample' is set only for demo ads
            } else {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
                if(ENABLE_DEBUG_LOG) {
                    Log.d(LOG_TAG, "Insufficient data, " + MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.toString());
                }
                return;
            }

            RFMNativeAd nativeAd = new RFMNativeAd(mContext);
            RFMNativeAdRequest nativeAdRequest = new RFMNativeAdRequest(mRFMServerName, mRFMPubId, mRFMAppId);
            if(mRFMAdId != null) {
                nativeAdRequest.setRFMTestAdId(mRFMAdId);
            }

            if(nativeSample != null) {
                // This is get sample native requests, Server extra 'native-sample' is set only for demo ads
                nativeAdRequest.setRFMAdMode(nativeSample);
            }

            HashMap<String, String> targetingParamsHM = getTargetingParams(serverExtras);
            nativeAdRequest.setTargetingParams(targetingParamsHM);

            final RFMStaticNativeAd rfmStaticNativeAd = new RFMStaticNativeAd(
                        context, nativeAd, nativeAdRequest, customEventNativeListener, new NativeClickHandler(context), new ImpressionTracker(context));
            rfmStaticNativeAd.loadAd();
        }

        private boolean isExtrasValid(Map<String, String> serverExtras) {
            return (serverExtras.containsKey(RFM_SERVER_NAME) && serverExtras.containsKey(RFM_PUB_ID) &&
                    serverExtras.containsKey(RFM_APP_ID));
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


        static class RFMStaticNativeAd extends StaticNativeAd {
            private final RFMNativeAd mNativeAd;
            private final RFMNativeAdRequest rfmNativeAdRequest;
            private final CustomEventNativeListener mCustomEventNativeListener;
            private final NativeClickHandler mNativeClickHandler;
            private final ImpressionTracker mImpressionTracker;
            private RFMNativeAdResponse adResponse;
            private String clickDestinationURL;
            private Context mContext;
            RFMStaticNativeAd(final Context context,
                              final RFMNativeAd nativeAd, final RFMNativeAdRequest nativeAdRequest,
                              final CustomEventNativeListener customEventNativeListener, final NativeClickHandler nativeClickHandler, final ImpressionTracker impressionTracker) {
                mContext = context;
                mNativeAd = nativeAd;
                rfmNativeAdRequest = nativeAdRequest;
                mCustomEventNativeListener = customEventNativeListener;
                mNativeClickHandler = nativeClickHandler;
                mImpressionTracker = impressionTracker;
            }

            void loadAd() {
                setNativeAdListener(mNativeAd);
                mNativeAd.requestRFMAd(rfmNativeAdRequest);
            }

            private void setNativeAdListener(RFMNativeAd nativeAd){
                nativeAd.setNativeAdListener(new RFMNativeAd.RFMNativeAdListener() {
                    @Override
                    public void onAdRequested(String requestUrl, boolean adRequestSuccess) {
                        log("Native Ad Requested "+requestUrl);
                    }

                    @Override
                    public void onAdReceived(RFMNativeAdResponse adResponse) {
                        log("Native Ad Received");
                        handleResponse(adResponse);
                    }

                    @Override
                    public void onAdFailed(String appId, String errorMessage) {
                        log("Native Ad Failed");
                        if (mCustomEventNativeListener != null) {
                            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.SERVER_ERROR_RESPONSE_CODE);
                        }
                    }
                });
            }

            void handleResponse(final RFMNativeAdResponse ad) {

                if(ad == null) {
                    if(mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.EMPTY_AD_RESPONSE);
                    }
                    return;
                }
                adResponse = ad;
                /*** Set Native Assets using Mopub Native API ***/

                // Set Title
                RFMNativeAssets.Title title = ad.getTitle();
                if(title != null) {
                    setTitle(title.getTitle());
                }

                // Set Text
                //setText();
                RFMNativeAssets.AssetData<String> desc = ad.getDataAsset(RFMNativeAssets.DATA_DESC);
                if(desc != null) {
                    setText(desc.getValue());
                }

                // Set Icon Image
                RFMNativeAssets.Image icon = ad.getImage(RFMNativeAssets.IMAGE_ICON);
                setIconImageUrl(icon == null ? null : icon.getUrl());


                // Set Main Image
                RFMNativeAssets.Image coverImage = ad.getImage(RFMNativeAssets.IMAGE_MAIN);
                setMainImageUrl(coverImage == null ? null : coverImage.getUrl());

                // Set CTA data asset
                RFMNativeAssets.AssetData<String> cta = ad.getDataAsset(RFMNativeAssets.DATA_CTA);
                setCallToAction(cta == null ? null : cta.getValue());

                // Set RATING data asset
                RFMNativeAssets.AssetData<String> rating = ad.getDataAsset(RFMNativeAssets.DATA_RATING);
                try {
                    setStarRating(rating == null ? null : Double.parseDouble(rating.getValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Set Sponsored data using addExtra`  (for any additional fields).
                RFMNativeAssets.AssetData<String> sponsoredData = ad.getDataAsset(RFMNativeAssets.DATA_SPONSORED);
                if(sponsoredData != null) {
                    addExtra(SPONSORED_FOR_AD, sponsoredData.getValue());
                }

                // Set Privacy information icon image
                RFMNativeAssets.Image adChoicesIconImage = ad.getAdChoiceIconImage();
                String privacyInformationIconImageUrl=null;
                if(adChoicesIconImage != null) {
                    privacyInformationIconImageUrl = adChoicesIconImage.getUrl();
                    setPrivacyInformationIconImageUrl(privacyInformationIconImageUrl);
                }

                // Set Privacy information url
                String adChoicesOptURL = ad.getAdChoicesOptURL();
                if (adChoicesOptURL !=null) {
                    setPrivacyInformationIconClickThroughUrl(adChoicesOptURL);
                }

                // Set clickthrough URL
                RFMNativeAssets.Link adLink = ad.getLink();
                if(adLink != null && adLink.getURL() != null) {
                    log("Link URL = "+adLink.getURL());
                    clickDestinationURL = adLink.getURL();
                    setClickDestinationUrl(clickDestinationURL);
                }

                // Set impression trackers
                try {
                    Set<String> impTrackers = ad.getImpressionTrackers();
                    if (impTrackers != null) {
                        for (String tracker : impTrackers) {
                            addImpressionTracker(tracker);
                        }
                    }
                } catch (Exception e) {
                    log("Failed to fetch impression trackers "+e.getMessage());
                }


//              setImpressionMinTimeViewed` for the minimum amount of time the view is required to be on the screen before recording an impression.

                if(mCustomEventNativeListener != null) {
                    mCustomEventNativeListener.onNativeAdLoaded(RFMStaticNativeAd.this);
                }
/*
 * Uncomment the below section if Images need to be cached **
 */
//                final List<String> imageUrls = new ArrayList<>();
//                final String mainImageUrl = getMainImageUrl();
//                if (mainImageUrl != null) {
//                    imageUrls.add(getMainImageUrl());
//                }
//                final String iconUrl = getIconImageUrl();
//                if (iconUrl != null) {
//                    imageUrls.add(getIconImageUrl());
//                }
//
//                if (privacyInformationIconImageUrl != null) {
//                    imageUrls.add(privacyInformationIconImageUrl);
//                }
//
//                preCacheImages(mContext, imageUrls, new NativeImageHelper.ImageListener() {
//                    @Override
//                    public void onImagesCached() {
//                        mCustomEventNativeListener.onNativeAdLoaded(RFMStaticNativeAd.this);
//                    }
//
//                    @Override
//                    public void onImagesFailedToCache(NativeErrorCode errorCode) {
//                        mCustomEventNativeListener.onNativeAdFailed(errorCode);
//                    }
//                });
            }


            // Lifecycle Handlers
            @Override
            public void prepare(@NonNull final View view) {
                mImpressionTracker.addView(view, this);
                mNativeClickHandler.setOnClickListener(view, this);
                log("RFM native ad getting prepared ");
            }

            @Override
            public void clear(@NonNull final View view) {
                mImpressionTracker.removeView(view);
                mNativeClickHandler.clearOnClickListener(view);
                log("RFM native ad getting cleared");
            }

            @Override
            public void destroy() {
                log("RFM native ad getting destroyed ");
                mImpressionTracker.destroy();
                try {
                    if (mNativeAd != null) {
                        mNativeAd.destroy();
                        mNativeAd.setNativeAdListener(null);
                    }
                } catch (Exception e) {
                    log( "Failed to clean up RFM Native Ad view "+e.getMessage());
                }
            }

            // Event Handlers
            @Override
            public void recordImpression(@NonNull final View view) {
                notifyAdImpressed();
            }

            @Override
            public void handleClick(@Nullable final View view) {
                notifyAdClicked();
                if(adResponse != null) {
                    RFMNativeAssets.Link adLink = adResponse.getLink();
                    Set<String> trackers = adLink.getClickTrackers();
                    if(trackers != null) {
                        for (String trackerStr: trackers) {
                            log("Firing click trackers = "+trackerStr);
                            TrackingRequest.makeTrackingHttpRequest(trackerStr, mContext);
                        }
                    }
                }
                if(clickDestinationURL != null) {
                    mNativeClickHandler.openClickDestinationUrl(clickDestinationURL, view);
                } else {
                    log("Click destination URL is not available");
                }
            }
            private void log(String message) {
                if (ENABLE_DEBUG_LOG)
                    Log.d(LOG_TAG, message);
            }
        }
 }