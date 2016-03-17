/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rfm.mopubadaptersample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleSettings extends AppCompatActivity {

    private final String LOG_TAG = "SampleSettings";
    Toolbar toolbar;
    private Context mContext;
    private EditText locPrecisionEditText;
    private Spinner locationTypeSpinner;
    private long adUnitId;
    private TextInputLayout locPrecisionEditTextContainer;

    private HashMap<String, String> updatedUnitHashMap = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_settings);
        mContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Settings");


        locPrecisionEditTextContainer  = (TextInputLayout) findViewById(R.id.location_precision_editext_container);
        locPrecisionEditText = (EditText) findViewById(R.id.location_precision_editext);
        locationTypeSpinner = (Spinner) findViewById(R.id.location_type_spinner);

        Bundle extras = getIntent().getExtras();
        adUnitId = extras.getLong(AdUnit.ID);

        initSampleSettings();

    }

    protected void initSampleSettings() {
        AdUnitDataSource adUnitDataSource = AdUnitDataSource.getInstance(mContext);
        AdUnit adUnit;

        adUnit = adUnitDataSource.getRowById(adUnitId);

        final List<String> locationTypeStrings = new ArrayList<String>();
        locationTypeStrings.add(AdUnit.LocationType.NORMAL.getLocType());
        locationTypeStrings.add(AdUnit.LocationType.TRUNCATED.getLocType());
        locationTypeStrings.add(AdUnit.LocationType.DISABLED.getLocType());

        locationTypeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, locationTypeStrings));

        if (adUnit.getLocationType()!=null) {
            locationTypeSpinner.setSelection(adUnit.getLocationType().ordinal());

            if (adUnit.getLocationType() == AdUnit.LocationType.TRUNCATED) {
                locPrecisionEditTextContainer.setVisibility(View.VISIBLE);
                locPrecisionEditText.setText(adUnit.getLat());
            }
        }

        locationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == AdUnit.LocationType.TRUNCATED.ordinal()) {
                    locPrecisionEditTextContainer.setVisibility(View.VISIBLE);
                } else {
                    locPrecisionEditTextContainer.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String valuesAreValid() {
        if (locationTypeSpinner.getSelectedItemPosition() == AdUnit.LocationType.TRUNCATED.ordinal()) {
            if (locPrecisionEditText.getText().toString().isEmpty())
                return "Enter a valid location precision value!";
            else {
                return "";
            }
        }
        return "";
    }

    private void saveTestCaseSettings() {
        updatedUnitHashMap.clear();

        updateLocType();

        AdUnitDataSource adUnitDS = AdUnitDataSource.getInstance(getBaseContext());

        // sample settings table is configured to work at ad unit level if required
        // but for current implementation we are using it at sample app level
        // we will updated all rows with same value
        adUnitDS.updateSampleAdUnit(-1, updatedUnitHashMap);

        mContext.sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        finish();
    }

    private void updateLocType() {
        final AdUnit.LocationType[] locTypes = AdUnit.LocationType.values();
        AdUnit.LocationType locationType = locTypes[locationTypeSpinner.getSelectedItemPosition()];

        updatedUnitHashMap.put(AdUnit.LOCATION_TYPE, locationType.getLocType());
        if (locationType == AdUnit.LocationType.TRUNCATED) {
            updatedUnitHashMap.put(AdUnit.LAT, locPrecisionEditText.getText().toString());
        } else {
            updatedUnitHashMap.put(AdUnit.LAT, "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_sample_settings:
                String msg = valuesAreValid();
                if (msg.equals(""))
                    saveTestCaseSettings();
                else
                    Utils.snackbar(SampleSettings.this, msg, false);
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
