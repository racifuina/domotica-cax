package com.abdesigns.domotica_cax;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.answers.Answers;

/**
 * Created by Rigoberto Acifuina on 26/03/17.
 */

public class DomoticaCAX extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        BluetoothConnectionManager bluetoothConnectionManager = BluetoothConnectionManager.getInstance();
    }
}
