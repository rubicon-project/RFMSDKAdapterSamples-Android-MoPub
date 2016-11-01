package com.rfm.mopubadaptersample.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rfm.mopubadaptersample.R;

import java.util.HashMap;

public class TestCaseSettings extends AppCompatActivity {

    private final String LOG_TAG = "TestCaseSettings";

    private Toolbar toolbar;
    private Context mContext;

    private CheckBox adWidthCheckbox;
    private EditText adWidthEditText;
    private CheckBox adHeightCheckbox;
    private EditText adHeightEditText;
    private EditText rfmAdIDEditText;
    private long adUnitId;
    private String adWidth = "";
    private String adHeight = "";
    private SwitchCompat testModeSwitch;
    private HashMap<String, String> updatedUnitHashMap = new HashMap<String, String>();
    final static String DISABLE_SIZE_CHANGE = "DISABLE_SIZE_CHANGE";
    private boolean mDisableSizeChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case_settings);
        mContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Test Case Settings");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adUnitId = extras.getLong(AdUnit.ID);
            mDisableSizeChange = extras.getBoolean(DISABLE_SIZE_CHANGE);
        }

        adWidthEditText = (EditText) findViewById(R.id.ad_width_edittext);
        adWidthCheckbox = (CheckBox) findViewById(R.id.ad_width_checkbox);
        adHeightEditText = (EditText) findViewById(R.id.ad_height_edittext);
        adHeightCheckbox = (CheckBox) findViewById(R.id.ad_height_checkbox);
        testModeSwitch = (SwitchCompat) findViewById(R.id.test_mode_switch);
        rfmAdIDEditText = (EditText) findViewById(R.id.rfm_ad_id_editext);
        LinearLayout sizeContainer = (LinearLayout) findViewById(R.id.size_container);

        if (mDisableSizeChange)
            sizeContainer.setVisibility(View.GONE);

        adWidthCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    adWidthEditText.setVisibility(View.INVISIBLE);
                } else {
                    adWidthEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        adHeightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    adHeightEditText.setVisibility(View.INVISIBLE);
                } else {
                    adHeightEditText.setVisibility(View.VISIBLE);
                }
            }
        });


        initializeActivity();
    }

    private void initializeActivity() {

        AdUnitDataSource mAdUnitDataSource = AdUnitDataSource.getInstance(TestCaseSettings.this);
        AdUnit _adUnit = mAdUnitDataSource.getRowById(adUnitId);
        long mAdWidth = _adUnit.getAdWidth();
        if (mAdWidth == -1) {
            adWidthCheckbox.setChecked(true);
            adWidthEditText.setVisibility(View.INVISIBLE);
        } else {
            adWidthEditText.setText(String.valueOf(mAdWidth));
        }
        long mAdHeight = _adUnit.getAdHeight();
        if (mAdHeight == -1) {
            adHeightCheckbox.setChecked(true);
            adHeightEditText.setVisibility(View.INVISIBLE);
        } else {
            adHeightEditText.setText(String.valueOf(mAdHeight));
        }

        boolean testMode = _adUnit.getTestMode();
        testModeSwitch.setChecked(testMode);

        String rfmAdId = _adUnit.getAdId();
        if (rfmAdId != null) {
            rfmAdIDEditText.setText(rfmAdId);
        } else {
            rfmAdIDEditText.setText("");
        }

    }

    private void saveTestCaseSettings() {
        updatedUnitHashMap.clear();

        updateAdSize();
        updateTestMode();
        updateRFMAdId();

        AdUnitDataSource adUnitDS = AdUnitDataSource.getInstance(getBaseContext());
        adUnitDS.updateSampleAdUnit(adUnitId, updatedUnitHashMap);

        mContext.sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        finish();
    }

    private void updateAdSize() {
        updatedUnitHashMap.put(AdUnit.AD_WIDTH, adWidth);
        updatedUnitHashMap.put(AdUnit.AD_HEIGHT, adHeight);
    }

    private void updateTestMode() {
        if (testModeSwitch.isChecked()) {
            updatedUnitHashMap.put(AdUnit.TEST_MODE, "1");
        } else {
            updatedUnitHashMap.put(AdUnit.TEST_MODE, "0");
        }
    }

    private void updateRFMAdId() {
        String rfmAdID = rfmAdIDEditText.getText().toString().trim();
        updatedUnitHashMap.put(AdUnit.AD_ID, rfmAdID);
    }

    private String valuesAreValid() {
        if (adWidthCheckbox.isChecked()) {
            adWidth = "-1";
        } else {
            adWidth = adWidthEditText.getText().toString().trim();
            if (adWidth.isEmpty()) {
                return "Enter a valid Width value";
            }
        }
        if (adHeightCheckbox.isChecked()) {
            adHeight = "-1";
        } else {
            adHeight = adHeightEditText.getText().toString().trim();
            if (adHeight.isEmpty()) {
                return "Enter a valid Height value";
            }
        }
        return "";
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
                    Utils.snackbar(TestCaseSettings.this, msg, false);
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
