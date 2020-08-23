package com.hmdm.control;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private static final int[] bitrates = {128000, 256000, 512000, 768000, 1024000};
    private static final int[] frame_rates = {5, 10, 15, 20};

    private EditText editTextServerUrl;
    private CheckBox checkBoxTranslateAudio;
    private Spinner spinnerBitrate;
    private Spinner spinnerFrameRate;
    private EditText editTextDeviceName;

    private SettingsHelper settingsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsHelper = SettingsHelper.getInstance(this);

        editTextServerUrl = findViewById(R.id.server_url);
        checkBoxTranslateAudio = findViewById(R.id.translate_audio);
        spinnerBitrate = findViewById(R.id.bitrate);
        spinnerFrameRate = findViewById(R.id.frame_rate);
        editTextDeviceName = findViewById(R.id.device_name);

        loadSettings();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveSettings()) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (saveSettings()) {
            finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    private void loadSettings() {
        editTextServerUrl.setText(settingsHelper.getString(SettingsHelper.KEY_SERVER_URL));
        checkBoxTranslateAudio.setChecked(settingsHelper.getBoolean(SettingsHelper.KEY_TRANSLATE_AUDIO));
        int bitrate = getIndex(bitrates, settingsHelper.getInt(SettingsHelper.KEY_BITRATE));
        if (bitrate >= 0) {
            spinnerBitrate.setSelection(bitrate);
        }
        int frameRate = getIndex(frame_rates, settingsHelper.getInt(SettingsHelper.KEY_FRAME_RATE));
        if (frameRate >= 0) {
            spinnerFrameRate.setSelection(frameRate);
        }
        editTextDeviceName.setText(settingsHelper.getString(SettingsHelper.KEY_DEVICE_NAME));
    }

    private boolean saveSettings() {
        String serverUrl = editTextServerUrl.getText().toString();
        if (!serverUrl.startsWith("http://") && !serverUrl.startsWith("https://")) {
            Toast.makeText(this, R.string.enter_correct_url, Toast.LENGTH_LONG).show();
            return false;
        }
        String deviceName = editTextDeviceName.getText().toString();
        if (deviceName.trim().equals("")) {
            Toast.makeText(this, R.string.enter_device_name, Toast.LENGTH_LONG).show();
            return false;
        }

        if (areSettingsChanged()) {
            setResult(Const.RESULT_DIRTY);
        } else {
            setResult(RESULT_OK);
        }

        settingsHelper.setString(SettingsHelper.KEY_SERVER_URL, serverUrl);
        settingsHelper.setBoolean(SettingsHelper.KEY_TRANSLATE_AUDIO, checkBoxTranslateAudio.isChecked());
        settingsHelper.setInt(SettingsHelper.KEY_BITRATE, bitrates[spinnerBitrate.getSelectedItemPosition()]);
        settingsHelper.setInt(SettingsHelper.KEY_FRAME_RATE, frame_rates[spinnerFrameRate.getSelectedItemPosition()]);
        settingsHelper.setString(SettingsHelper.KEY_DEVICE_NAME, deviceName);
        return true;
    }

    private boolean areSettingsChanged() {
        if (!editTextServerUrl.getText().toString().equals(settingsHelper.getString(SettingsHelper.KEY_SERVER_URL))) {
            return true;
        }
        if (checkBoxTranslateAudio.isChecked() != settingsHelper.getBoolean(SettingsHelper.KEY_TRANSLATE_AUDIO)) {
            return true;
        }
        if (bitrates[spinnerBitrate.getSelectedItemPosition()] != settingsHelper.getInt(SettingsHelper.KEY_BITRATE)) {
            return true;
        }
        if (frame_rates[spinnerFrameRate.getSelectedItemPosition()] != settingsHelper.getInt(SettingsHelper.KEY_FRAME_RATE)) {
            return true;
        }
        if (!editTextDeviceName.getText().toString().equals(settingsHelper.getString(SettingsHelper.KEY_DEVICE_NAME))) {
            return true;
        }
        return false;
    }

    private int getIndex(int[] array, int value) {
        for (int n = 0; n < array.length; n++) {
            if (array[n] == value) {
                return n;
            }
        }
        return -1;
    }
}