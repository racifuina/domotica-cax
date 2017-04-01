package com.abdesigns.domotica_cax;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceSelectionView extends AppCompatActivity implements BleCallback {
    //Bluetooth Variables.
    private final static int REQUEST_ENABLE_BT = 1;
    private BroadcastReceiver mReceiver;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private BleDevicesAdapter mAdapter;

    private ProgressDialog mProgressDialog;
    SharedPreferences pref;
    BluetoothConnectionManager bluetoothConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_dispositivo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pref = getSharedPreferences(getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
        bluetoothConnectionManager = BluetoothConnectionManager.getInstance();
        bluetoothConnectionManager.mBleCallback = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mProgressDialog = new ProgressDialog(this);

        mAdapter = new BleDevicesAdapter(deviceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    mProgressDialog.setTitle("Buscando dispositivos");
                    mProgressDialog.setMessage("Por favor espere...");
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.show();
                    deviceList.clear();
                    mAdapter.notifyDataSetChanged();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceList.add(device);
                    mAdapter.notifyDataSetChanged();
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND); //get event found
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intentFilter);
        bluetoothConnectionManager.disconnect();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothConnectionManager.mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    bluetoothConnectionManager.mBluetoothAdapter.startDiscovery();
                }
            }
        });

        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bluetoothConnectionManager.mBluetoothAdapter.cancelDiscovery();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                BluetoothDevice device = deviceList.get(position);
                mProgressDialog.setTitle("Conectando con el dispositivo " + device.getName());
                mProgressDialog.setMessage("Por favor espere...");
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();

                bluetoothConnectionManager.connectToDevice(device, new BluetoothSocketConnectionCallback() {
                    @Override
                    public void onSucess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.hide();
                                    Toast.makeText(DeviceSelectionView.this, "Exito al conectar", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(getResources().getString(R.string.mac_ble), bluetoothConnectionManager.mBluetoothDevice.getAddress());
                                    editor.commit();
                                    onBackPressed();
                                }

                            }
                        });
                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.hide();
                                    Toast.makeText(DeviceSelectionView.this, "Error al Conectar", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    //BleCallback methods.
    @Override
    public void onDeviceConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onDeviceDisconnected() {
        System.out.println("onDeviceDisconnected");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            bluetoothConnectionManager.mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bluetoothConnectionManager.mBluetoothAdapter.isDiscovering()) {
            bluetoothConnectionManager.mBluetoothAdapter.cancelDiscovery();
        }
    }
}
