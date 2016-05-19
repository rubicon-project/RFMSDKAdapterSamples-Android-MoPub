/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rfm.mopubadaptersample.sample.AdUnit.AdType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_AD_HEIGHT;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_AD_WIDTH;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_APP_ID;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_ID;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_AD_TYPE;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_AD_ID;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_IS_CUSTOM;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_LAT;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_LOCATION_PRECISION;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_LONG;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_PUB_ID;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_REFRESH_COUNT;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_REFRESH_INTERVAL;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_RFM_SERVER;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_SITE_ID;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_TARGETING_KEY_VALUE;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_TEST_CASE_NAME;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_TEST_MODE;
import static com.rfm.mopubadaptersample.sample.SQLiteHelper.COLUMN_LOCATION_TYPE;

import static com.rfm.mopubadaptersample.sample.SQLiteHelper.SAMPLE_ADS_TABLE;

public class AdUnitDataSource {

    String LOG_TAG = "AdUnitDataSource";
    private static AdUnitDataSource adUnitDataSourceInstance = null;
    private SQLiteHelper mDatabaseHelper;
    private String[] mAllColumns = {
            COLUMN_ID,
            COLUMN_TEST_CASE_NAME,
            COLUMN_SITE_ID,
            COLUMN_AD_TYPE,
            COLUMN_REFRESH_COUNT,
            COLUMN_REFRESH_INTERVAL,
            COLUMN_LOCATION_TYPE,
            COLUMN_LOCATION_PRECISION,
            COLUMN_LAT,
            COLUMN_LONG,
            COLUMN_TARGETING_KEY_VALUE,
            COLUMN_AD_WIDTH,
            COLUMN_AD_HEIGHT,
            COLUMN_TEST_MODE,
            COLUMN_AD_ID,
            COLUMN_IS_CUSTOM,

            COLUMN_RFM_SERVER,
            COLUMN_APP_ID,
            COLUMN_PUB_ID,
    };

    private AdUnitDataSource(final Context context) {
        mDatabaseHelper = new SQLiteHelper(context);
    }

    public static AdUnitDataSource getInstance(Context context) {
        if (adUnitDataSourceInstance == null) {
            synchronized (AdUnitDataSource.class) {
                if (adUnitDataSourceInstance == null) {
                    adUnitDataSourceInstance = new AdUnitDataSource(context);
                }
            }
        }

        return adUnitDataSourceInstance;
    }


    AdUnit createAdUnit(final AdUnit adUnit) {
        try {
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
            values.put(COLUMN_IS_CUSTOM, adUnit.getAdIsCustom());

            values.put(COLUMN_RFM_SERVER, adUnit.getRfmServer());
            values.put(COLUMN_APP_ID, adUnit.getAppId());
            values.put(COLUMN_PUB_ID, adUnit.getPubId());


            final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
            final long insertId = database.insert(SAMPLE_ADS_TABLE, null, values);
            final Cursor cursor = database.query(SAMPLE_ADS_TABLE, mAllColumns,
                    COLUMN_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            final AdUnit newAdUnit = convertCursorToAdUnit(cursor);
            cursor.close();
            database.close();
            return newAdUnit;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    AdUnit getRowById(long id) {
        AdUnit selectedAdUnit = null;
        String sqlQuery = "select * from " + SAMPLE_ADS_TABLE + " where _id=" + id;
        final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = database.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                selectedAdUnit = convertCursorToAdUnit(cursor);
            cursor.close();
        }
        database.close();
        return selectedAdUnit;
    }

    void deleteSampleAdUnit(final AdUnit adUnit) {
        final long id = adUnit.getId();
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        database.delete(SAMPLE_ADS_TABLE, COLUMN_ID + " = " + id, null);
        Log.d(LOG_TAG, "AdUnit deleted with id: " + id);
        database.close();
    }

    void updateSampleAdUnit(final long adUnitId, HashMap<String, String> newValuesHashMap) {
        ContentValues newValues = new ContentValues();

        for (Map.Entry<String, String> entry : newValuesHashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals(COLUMN_REFRESH_COUNT) || key.equals(COLUMN_REFRESH_INTERVAL) ||
                    key.equals(COLUMN_AD_WIDTH) || key.equals(COLUMN_AD_HEIGHT))
                newValues.put(key, Integer.parseInt(value));
            else
                newValues.put(key, value);
        }

        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        if (adUnitId != -1) {
            database.update(SAMPLE_ADS_TABLE, newValues, COLUMN_ID + "=" + adUnitId, null);
        } else {
            // update all rows with same value
            database.update(SAMPLE_ADS_TABLE, newValues, null, null);
        }

    }

    List<AdUnit> getAllAdUnits() {
        final List<AdUnit> allAdUnits = new ArrayList<AdUnit>();
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = database.query(SAMPLE_ADS_TABLE,
                mAllColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final AdUnit adUnit = convertCursorToAdUnit(cursor);
            allAdUnits.add(adUnit);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        return allAdUnits;
    }

    private AdUnit convertCursorToAdUnit(final Cursor cursor) {
        final long id = cursor.getLong(0);
        final String testCaseName = cursor.getString(1);
        final String siteId = cursor.getString(2);
        final AdType adType = AdType.fromActivityClassName(cursor.getString(3));
        final int refreshCount = cursor.getInt(4);
        final int refreshInterval = cursor.getInt(5);
        final String locationType = cursor.getString(6);
        final String locationPrecision = cursor.getString(7);
        final String lat = cursor.getString(8);
        final String lng = cursor.getString(9);
        final String targetingKeyValue = cursor.getString(10);
        final int adWidth = cursor.getInt(11);
        final int adHeight = cursor.getInt(12);
        final int testMode = cursor.getInt(13);
        final String adId = cursor.getString(14);
        final int isCustom = cursor.getInt(15);

        final String rfmServer = cursor.getString(16);
        final String appId = cursor.getString(17);
        final String pubId = cursor.getString(18);

        if (adType == null) {
            return null;
        }

        return new AdUnit(id, testCaseName, siteId, adType, refreshCount, refreshInterval,
                AdUnit.LocationType.fromLocationName(locationType), locationPrecision, lat, lng, targetingKeyValue, adWidth, adHeight,
                testMode == 1, adId, isCustom == 1, rfmServer, appId, pubId, 0);
    }

    void cleanUp() {
        if(mDatabaseHelper != null) {
            mDatabaseHelper.close();
            mDatabaseHelper = null;
            adUnitDataSourceInstance = null;
        }
    }
}
