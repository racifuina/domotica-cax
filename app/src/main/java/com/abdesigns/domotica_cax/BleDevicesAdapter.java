package com.abdesigns.domotica_cax;

/**
 * Created by Rigoberto Acifuina on 22/03/17.
 */

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class BleDevicesAdapter extends RecyclerView.Adapter<BleDevicesAdapter.MyViewHolder> {
    private List<BluetoothDevice> deviceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, macAddressTextView;

        public MyViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            macAddressTextView = (TextView) view.findViewById(R.id.macAddressTextView);
        }
    }

    public BleDevicesAdapter(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        holder.nameTextView.setText(device.getName());
        holder.macAddressTextView.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
