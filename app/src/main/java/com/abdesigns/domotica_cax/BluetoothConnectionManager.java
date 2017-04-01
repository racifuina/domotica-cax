package com.abdesigns.domotica_cax;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.provider.SyncStateContract;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Rigoberto Acifuina on 22/03/17.
 */

interface BluetoothSocketConnectionCallback {

    void onSucess();

    void onError();
}


public class BluetoothConnectionManager {
    final public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static Boolean isConnected = false;
    private static BluetoothConnectionManager mBluetoothConnectionManager = null;
    static BluetoothAdapter mBluetoothAdapter = null;
    static BluetoothDevice mBluetoothDevice = null;
    static BluetoothSocket bluetoothSocket;
    public static BleCallback mBleCallback;

    public static BluetoothConnectionManager getInstance() {
        if (mBluetoothConnectionManager == null)
            mBluetoothConnectionManager = new BluetoothConnectionManager();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothConnectionManager;
    }

    public void connectToDevice(BluetoothDevice device, BluetoothSocketConnectionCallback callback) {
        mBluetoothAdapter.cancelDiscovery();
        try {
            BluetoothSocket socketConnection = device.createRfcommSocketToServiceRecord(myUUID);
            socketConnection.connect();
            mBluetoothDevice = device;
            isConnected = true;
            callback.onSucess();
            bluetoothSocket = socketConnection;
            mBleCallback.onDeviceConnected();
        } catch (IOException e) {
            isConnected = false;
            callback.onError();
            mBleCallback.onDeviceDisconnected();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void issueCommand(String command) {
        if (isConnected && bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write(command.getBytes());
                System.out.println("writing! comand!!! +++++++++");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void disconnect() {
        if (isConnected && bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
