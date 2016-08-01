/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String SAMPLE_ADS_TABLE = "sampleads";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEST_CASE_NAME = "testCaseName";
    public static final String COLUMN_SITE_ID = "siteId";
    public static final String COLUMN_AD_TYPE = "adType";
    public static final String COLUMN_REFRESH_COUNT = "refreshCount";
    public static final String COLUMN_REFRESH_INTERVAL = "refreshInterval";
    public static final String COLUMN_LOCATION_TYPE = "locationType";
    public static final String COLUMN_LOCATION_PRECISION = "locationPrecision";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_TARGETING_KEY_VALUE = "targetingKeyValue";
    public static final String COLUMN_AD_WIDTH = "adWidth";
    public static final String COLUMN_AD_HEIGHT = "adHeight";
    public static final String COLUMN_TEST_MODE = "testMode";
    public static final String COLUMN_AD_ID = "adId";
    public static final String COLUMN_IS_CUSTOM = "isCustom";

    public static final String COLUMN_RFM_SERVER = "rfmServer";
    public static final String COLUMN_APP_ID = "appId";
    public static final String COLUMN_PUB_ID = "pubId";

    private static final String DATABASE_NAME = "sampleadsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + SAMPLE_ADS_TABLE
            + " ("

            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TEST_CASE_NAME + " text not null, "
            + COLUMN_SITE_ID + " text not null, "
            + COLUMN_AD_TYPE + " text not null, "
            + COLUMN_REFRESH_COUNT + " integer not null, "
            + COLUMN_REFRESH_INTERVAL + " integer not null, "
            + COLUMN_LOCATION_TYPE + " text not null, "
            + COLUMN_LOCATION_PRECISION + " text not null, "
            + COLUMN_LAT + " text not null, "
            + COLUMN_LONG + " text not null, "
            + COLUMN_TARGETING_KEY_VALUE + " text not null, "
            + COLUMN_AD_WIDTH + " integer not null, "
            + COLUMN_AD_HEIGHT + " integer not null, "
            + COLUMN_TEST_MODE + " integer not null, "
            + COLUMN_AD_ID + " text not null, "
            + COLUMN_IS_CUSTOM + " integer not null, "

            + COLUMN_RFM_SERVER + " text not null, "
            + COLUMN_APP_ID + " text not null, "
            + COLUMN_PUB_ID + " text not null "

            + ");";

    private final Context mContext;

    public SQLiteHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final List<AdUnit> adUnitList = new ArrayList<AdUnit>();

        adUnitList.add(new AdUnit(-1, "Mopub Banner", "b195f8dd8ded45fe847ad89ed1d016da",
                AdUnit.AdType.MOPUB_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6","0.0", "0.0",
                "", 320, 50, true, "", false, "", "", "", 1));
        adUnitList.add(new AdUnit(-1, "Mopub Interstitial", "24534e1901884e398f1253216226017e",
                AdUnit.AdType.MOPUB_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", 320, 50, true, "", false, "", "", "", 2));

        adUnitList.add(new AdUnit(-1, "RFM/Mopub Adpt Banner", "40b636994ce84a788c99d6d75f87cbbb",
                AdUnit.AdType.MOPUB_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0", "0",
                "", 320, 50, true, "", false, "http://mrp.rubiconproject.com/", "6C62DFC0EF710133146B22000B3510F7", "111008", 3));
        adUnitList.add(new AdUnit(-1, "RFM/Mopub Adpt Interstitial", "2ed5b3a708f649209aeb7537ccb3e4bd",
                AdUnit.AdType.MOPUB_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", 320, 480, true, "", false, "http://mrp.rubiconproject.com/", "3C19B350EF73013312DE22000B2E019E", "111008", 4));

        adUnitList.add(new AdUnit(-1, "RFM/Mopub FastLane Banner", "0eecb7d91ac543ba83ef98b54c38168b",
                AdUnit.AdType.FASTLANE_MOPUB_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0", "0",
                "", 320, 50, true, "0", false, "http://mrp.rubiconproject.com/", "50144540EF720133146F22000B3510F7", "111008", 5));

        adUnitList.add(new AdUnit(-1, "RFM/Mopub FastLane Interstitial", "04f98af79bed42de97090d7203903e54",
                AdUnit.AdType.FASTLANE_MOPUB_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0", "0",
                "", 320, 480, true, "0", false, "http://mrp.rubiconproject.com/", "E24F8D60EF73013312E122000B2E019E", "111008", 6));

        adUnitList.add(new AdUnit(-1, "RFM/Mopub Rewarded Video Interstitial", "dab8cb19b71742d3a142a969697919b3",
                AdUnit.AdType.REWARDED_VIDEO_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", 320, 480, true, "0", false, "http://mrp.rubiconproject.com/", "CDBBF1A037F60134162922000B3510F7", "111315", 7));

        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.beginTransaction();

        for (final AdUnit adUnit : adUnitList) {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_TEST_CASE_NAME, adUnit.getTestCaseName());
            values.put(COLUMN_SITE_ID, adUnit.getSiteId());
            values.put(COLUMN_AD_TYPE, adUnit.getActivityClassName());
            values.put(COLUMN_REFRESH_COUNT, adUnit.getRefreshCount());
            values.put(COLUMN_REFRESH_INTERVAL, adUnit.getRefreshInterval());
            values.put(COLUMN_LOCATION_TYPE, adUnit.getLocationType().getLocType());
            values.put(COLUMN_LOCATION_PRECISION, adUnit.getLocationPrecision());
            values.put(COLUMN_LAT, adUnit.getLat());
            values.put(COLUMN_LONG, adUnit.getLong());
            values.put(COLUMN_TARGETING_KEY_VALUE, adUnit.getTargetingKeyValue());
            values.put(COLUMN_AD_WIDTH, adUnit.getAdWidth());
            values.put(COLUMN_AD_HEIGHT, adUnit.getAdHeight());
            values.put(COLUMN_TEST_MODE, adUnit.getTestMode());
            values.put(COLUMN_AD_ID, adUnit.getAdId());
            values.put(COLUMN_IS_CUSTOM, 0);

            values.put(COLUMN_RFM_SERVER, adUnit.getRfmServer());
            values.put(COLUMN_APP_ID, adUnit.getAppId());
            values.put(COLUMN_PUB_ID, adUnit.getPubId());

            sqLiteDatabase.insert(SAMPLE_ADS_TABLE, null, values);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    private void recreateDb(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + SAMPLE_ADS_TABLE);
        onCreate(database);
    }
}
