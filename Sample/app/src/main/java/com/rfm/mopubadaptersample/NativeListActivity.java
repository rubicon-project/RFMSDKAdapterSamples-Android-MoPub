package com.rfm.mopubadaptersample;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.rfm.mopubadaptersample.sample.BaseActivity;

import java.util.EnumSet;

public class NativeListActivity extends BaseActivity {
    private MoPubAdAdapter mAdAdapter;
    private RequestParameters mRequestParameters;
    public String adUnit = "6b8e00b9ac584364931aa22b2a17f05c";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_list_view);
        final ListView listView = (ListView) findViewById(R.id.native_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        for (int i = 0; i < 100; ++i) {
            adapter.add("Rubicon Native Ad test  " + i);
        }

        // Create an ad adapter that gets its positioning information from the MoPub Ad Server.
        // This adapter will be used in place of the original adapter for the ListView.
        mAdAdapter = new MoPubAdAdapter(this, adapter, new MoPubNativeAdPositioning.MoPubServerPositioning());

        // Set up a renderer that knows how to put ad data in your custom native view.
        final MoPubStaticNativeAdRenderer staticAdRender = new MoPubStaticNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_layout_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mainImageId(R.id.native_main_image)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .addExtra("sponsoredText", R.id.native_sponsored)
                        .build());

        // Native Video Ads not supported by Rubicon custom native adapter
//        final MoPubVideoNativeAdRenderer videoAdRenderer = new MoPubVideoNativeAdRenderer(
//                new MediaViewBinder.Builder(R.layout.video_ad_list_item)
//                        .titleId(R.id.native_title)
//                        .textId(R.id.native_text)
//                        .mediaLayoutId(R.id.native_media_layout)
//                        .iconImageId(R.id.native_icon_image)
//                        .callToActionId(R.id.native_cta)
//                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
//                        .build());


        // Register the renderers with the MoPubAdAdapter and then set the adapter on the ListView.
 //       mAdAdapter.registerAdRenderer(videoAdRenderer);
        mAdAdapter.registerAdRenderer(staticAdRender);
        listView.setAdapter(mAdAdapter);

        mAdAdapter.loadAds(adUnit, mRequestParameters);
        setLoadAdAction();
    }

    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak.
        mAdAdapter.destroy();
        super.onDestroy();
    }

    @Override
    public void loadAd() {
        loadMopubNativeAd();
    }

    @Override
    public void updateAdView() {

    }

    public void loadMopubNativeAd() {
        // If your app already has location access, include it here.
        final Location location = null;

        // Setting desired assets on your request helps native ad networks and bidders
        // provide higher-quality ads.
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

        mRequestParameters = new RequestParameters.Builder()
                .location(location)
                .desiredAssets(desiredAssets)
                .build();

        mAdAdapter.loadAds(adUnit, mRequestParameters);
    }
}

