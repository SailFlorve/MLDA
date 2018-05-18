package com.lmachine.mlda.fragment;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lmachine.mlda.MainActivity;
import com.lmachine.mlda.R;
import com.lmachine.mlda.adapter.BluetoothDeviceListAdapter;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.List;


public class BluetoothSensorFragment extends Fragment implements ServiceConnection {

    private TextView openBtText;
    private ListView btDeviceListView;
    private BluetoothDeviceListAdapter listAdapter;
    private List<Pair<String, String>> deviceStringList = new ArrayList<>();
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();

    private StringBuilder logBuilder;

    private SensorService sensorService;

    private BluetoothAdapter bluetoothAdapter;

    private BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            Log.d("BluetoothSensorFragment", "onReceive: " + intent.getAction());

            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            openBtText.setClickable(false);
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            addLog("正在开启蓝牙...");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            addLog("蓝牙已打开。");
                            discovery();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            addLog("正在蓝牙关闭...");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            addLog("蓝牙已关闭，点击开启蓝牙。");
                            openBtText.setClickable(true);
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    addLog("正在搜索设备...");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (deviceStringList.isEmpty()) {
                        addLog("没有搜索到设备，点此重新搜索。");
                    } else {
                        addLog("点击设备名称配对，或点此重新搜索。");
                    }
                    openBtText.setClickable(true);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Pair<String, String> pair = new Pair<>(d.getName(), d.getAddress());
                    Log.d("", "onReceive: " + pair.toString());
                    if (!deviceStringList.contains(pair)) {
                        deviceStringList.add(pair);
                        bluetoothDeviceList.add(d);
                        listAdapter.notifyDataSetChanged();
                    }
                    if (d.getBondState() == BluetoothDevice.BOND_BONDED) {
                        setBondedStatus(d);
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    switch (d.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            addLog("配对失败，请重试。点此重新搜索。");
                            openBtText.setClickable(true);
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            addLog("正在配对...");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            setBondedStatus(d);
                            break;
                        default:

                    }
                    break;
                default:

            }
        }
    };

    public BluetoothSensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logBuilder = new StringBuilder();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getContext().registerReceiver(bluetoothBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_sensor, container, false);

        SensorService.MyBinder binder = (SensorService.MyBinder) getArguments().getBinder("service");
        if (binder != null) {
            sensorService = binder.getService();
            Log.d(getClass().getName(), "onCreateView: find Service");
        }

        openBtText = view.findViewById(R.id.tv_open_bt);
        btDeviceListView = view.findViewById(R.id.lv_bt_device);

        openBtText.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            } else if (!bluetoothAdapter.isDiscovering()) {
                discovery();
            }
        });

        btDeviceListView.setOnItemClickListener((parent, view1, position, id) -> {
            BluetoothDevice device = bluetoothDeviceList.get(position);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                setBondedStatus(device);
            } else {
                device.createBond();
            }
        });

        listAdapter = new BluetoothDeviceListAdapter(getContext(), deviceStringList);
        btDeviceListView.setAdapter(listAdapter);

        sensorService.initBluetooth(new BluetoothUtil.BluetoothDeviceSensorInitStateCallback() {
            @Override
            public void onSuccess() {
                bluetoothAdapter = sensorService.getBluetoothAdapter();

                if (bluetoothAdapter.isEnabled()) {
                    addLog("蓝牙已打开，正在搜索设备...");
                    openBtText.setClickable(false);
                    discovery();
                } else {
                    addLog("蓝牙已关闭。点击此处打开蓝牙。");
                }
            }

            @Override
            public void onFailed() {
                addLog("设备不支持蓝牙。");
                openBtText.setClickable(false);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(bluetoothBroadcastReceiver);
        super.onDestroy();
    }

    public static BluetoothSensorFragment newInstance(SensorService service) {
        Bundle args = new Bundle();
        args.putBinder("service", service.getMyBinder());
        BluetoothSensorFragment fragment = new BluetoothSensorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SensorService.MyBinder binder = (SensorService.MyBinder) service;
        sensorService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private void setBondedStatus(BluetoothDevice device) {
        addLog("已经与" + device.getName() + "配对。");
        sensorService.setBondedDevice(device);
        bluetoothAdapter.cancelDiscovery();
    }

    private void addLog(String text) {
        openBtText.setText(text);
    }

    private void discovery() {
        MainActivity activity = (MainActivity) getActivity();
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (activity.checkPermissions(permissions)) {
            sensorService.startDiscovery();
        } else {
            requestPermissions(permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("", "onRequestPermissionsResult: ");
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                sensorService.startDiscovery();
            } else {
                MainActivity activity = (MainActivity) getActivity();
                activity.showSnackBar("要扫描设备，请允许权限。");
            }
        }
    }
}
