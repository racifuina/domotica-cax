package com.abdesigns.domotica_cax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class HomeScreenView extends AppCompatActivity implements BleCallback {

    Boolean isLightOn = true;
    Button aButton, bButton, cButton, humButton, tempButton;
    AppCompatImageButton lightButton;
    BluetoothConnectionManager bluetoothConnectionManager;
    SharedPreferences pref;
    ProgressDialog btProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aButton = (Button) findViewById(R.id.aButton);
        bButton = (Button) findViewById(R.id.bButton);
        cButton = (Button) findViewById(R.id.cButton);
        humButton = (Button) findViewById(R.id.humButton);
        tempButton = (Button) findViewById(R.id.tempButton);
        lightButton = (AppCompatImageButton) findViewById(R.id.lightButton);
        btProgressDialog = new ProgressDialog(this);

        pref = getSharedPreferences(getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
        bluetoothConnectionManager = BluetoothConnectionManager.getInstance();
        bluetoothConnectionManager.mBleCallback = this;

        if (bluetoothConnectionManager.isConnected) {
            aButton.setEnabled(true);
            bButton.setEnabled(true);
            cButton.setEnabled(true);
            humButton.setEnabled(true);
            tempButton.setEnabled(true);
            lightButton.setEnabled(true);
        } else {
            aButton.setEnabled(false);
            bButton.setEnabled(false);
            cButton.setEnabled(false);
            humButton.setEnabled(false);
            tempButton.setEnabled(false);
            lightButton.setEnabled(false);
        }

        if (isLightOn) {
            lightButton.setImageResource(R.drawable.light_on);
        } else {
            lightButton.setImageResource(R.drawable.light_off);
        }

        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn) {
                    lightButton.setImageResource(R.drawable.light_off);
                    bluetoothConnectionManager.issueCommand("0");
                } else {
                    lightButton.setImageResource(R.drawable.light_on);
                    bluetoothConnectionManager.issueCommand("1");
                }
                isLightOn = !isLightOn;
            }
        });

        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnectionManager.issueCommand("A");
            }
        });

        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnectionManager.issueCommand("B");
            }
        });

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnectionManager.issueCommand("C");
            }
        });

        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnectionManager.issueCommand("D");
            }
        });

        humButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent goToAbout = new Intent(this, DeviceSelectionView.class);
            startActivity(goToAbout);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent goToAbout = new Intent(this, AboutView.class);
            startActivity(goToAbout);
        }
        return super.onOptionsItemSelected(item);
    }

    //BleCallback methods.

    @Override
    public void onDeviceConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (btProgressDialog.isShowing()) {
                    btProgressDialog.hide();
                }
                aButton.setEnabled(true);
                bButton.setEnabled(true);
                cButton.setEnabled(true);
                humButton.setEnabled(true);
                tempButton.setEnabled(true);
                lightButton.setEnabled(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothConnectionManager.mBleCallback = this;
        if (bluetoothConnectionManager.isConnected) {
            aButton.setEnabled(true);
            bButton.setEnabled(true);
            cButton.setEnabled(true);
            humButton.setEnabled(true);
            tempButton.setEnabled(true);
            lightButton.setEnabled(true);
        } else {
            aButton.setEnabled(false);
            bButton.setEnabled(false);
            cButton.setEnabled(false);
            humButton.setEnabled(false);
            tempButton.setEnabled(false);
            lightButton.setEnabled(false);
        }
    }

    @Override
    public void onDeviceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aButton.setEnabled(false);
                bButton.setEnabled(false);
                cButton.setEnabled(false);
                humButton.setEnabled(false);
                tempButton.setEnabled(false);
                lightButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bluetoothConnectionManager.disconnect();
    }
}
