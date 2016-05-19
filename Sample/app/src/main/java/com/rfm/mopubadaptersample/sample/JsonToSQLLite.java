/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample.sample;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.rfm.mopubadaptersample.sample.AdUnit.AdType;

public class JsonToSQLLite {

    private static final String LINE_FEED = "\r\n";

    public static String readFileToString(String filename) {

        String everything = "";
        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(LINE_FEED);
                    line = br.readLine();
                }
                everything = sb.toString();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return everything;
    }

    public static boolean saveToDB(Context context, String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("testcases");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);

                final String testCaseName = _jsonObject.getString(AdUnit.TEST_CASE_NAME);
                final String siteId = _jsonObject.getString(AdUnit.SITE_ID);

                final AdType adType = AdUnit.AdType.fromActivityClassName(_jsonObject.getString(AdUnit.AD_TYPE));

                final AdUnit.LocationType locationType = AdUnit.LocationType.fromLocationName(_jsonObject.getString(AdUnit.LOCATION_TYPE));

                final String locationPrecision = _jsonObject.getString(AdUnit.LOCATION_PRECISION);

                final String rfmServer = _jsonObject.getString(AdUnit.RFM_SERVER);
                final String rfmAdId = _jsonObject.getString(AdUnit.AD_ID);
                final String rmfPubId = _jsonObject.getString(AdUnit.PUB_ID);
                final String rfmAppId = _jsonObject.getString(AdUnit.APP_ID);

                String adWidthStr;
                if (_jsonObject.has(AdUnit.AD_WIDTH)) {
                    adWidthStr = _jsonObject.getString(AdUnit.AD_WIDTH);
                } else {
                    adWidthStr = "320";
                }
                int adWidth = 320;
                if (adWidthStr != null && !adWidthStr.isEmpty()) {
                    adWidth = Integer.parseInt(adWidthStr);
                }

                String adHeightStr;
                if (_jsonObject.has(AdUnit.AD_HEIGHT)) {
                    adHeightStr = _jsonObject.getString(AdUnit.AD_HEIGHT);
                } else {
                    adHeightStr = "50";
                }
                int adHeight = 50;
                if (adHeightStr != null && !adHeightStr.isEmpty()) {
                    adHeight = Integer.parseInt(adHeightStr);
                }

                final AdUnit adUnit = new AdUnit(-1, testCaseName, siteId, adType,
                        1, 0, locationType, locationPrecision, "0.0", "0.0",
                        "", adWidth, adHeight, true, rfmAdId, true, rfmServer, rfmAppId, rmfPubId, 0);

                AdUnitDataSource adUnitDataSource = AdUnitDataSource.getInstance(context);
                adUnitDataSource.createAdUnit(adUnit);

            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
