package com.lmachine.mlda.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lmachine.mlda.R;

import java.util.List;

public class BluetoothDeviceListAdapter extends BaseAdapter {
    private List<Pair<String, String>> deviceList;
    private Context context;

    public BluetoothDeviceListAdapter(Context context, List<Pair<String, String>> deviceList) {
        this.deviceList = deviceList;
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


    @Override
    @SuppressLint("ViewHolder")
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_list_item, null);
        Pair<String, String> device = (Pair<String, String>) getItem(position);
        TextView name = v.findViewById(R.id.tv_device_name);
        TextView mac = v.findViewById(R.id.tv_device_mac);
        String first = device.first;
        if (first == null || first.isEmpty() || first.trim().isEmpty()) {
            first = "未知设备";
        }
        name.setText(first);
        mac.setText(device.second);
        return v;
    }
}
