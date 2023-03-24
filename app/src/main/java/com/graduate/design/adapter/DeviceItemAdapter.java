package com.graduate.design.adapter;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.graduate.design.R;

import java.util.ArrayList;
import java.util.List;

/*
* 在搜索蓝牙设备时，设备展示列表
* */

public class DeviceItemAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    public DeviceItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
        }

        TextView deviceName = convertView.findViewById(R.id.device_name);

        BluetoothDevice device = deviceList.get(position);

        // BUG 设备名称为空
        deviceName.setText(device.getName());

        return convertView;
    }

    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device) {
        if (!deviceList.contains(device) && device.getName() != null) {
            deviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public void addAllDevice(List<BluetoothDevice> bluetoothDevices){
        deviceList.clear();
        for(BluetoothDevice device : bluetoothDevices){
            addDevice(device);
        }
        notifyDataSetChanged();
    }

    public void clearDevice(){
        deviceList.clear();
        notifyDataSetChanged();
    }
}
