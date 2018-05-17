package com.lmachine.mlda;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private String TAG = "BluetoothActivity";
    BluetoothDevice device;
    BluetoothSocket socket;

    private class readThread extends Thread {
        public void run() {
            Log.d(TAG, "run: readThread");

            InputStream is = null;
            try {
                is = socket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    byte[] b = new byte[16];
                    is.read(b);
                    StringBuilder sb = new StringBuilder();
                    for (byte b1 : b) {
                        sb.append(Integer.toHexString(b1 & 0xFF)).append(" ");
                    }
                    Log.d(TAG, "run: " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    char[] chars = line.toCharArray();
//                    for (char aChar : chars) {
//                        System.out.println(String.format("%x ", (int)aChar));
//
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private class ClientThread extends Thread {
        public void run() {
            Log.d(TAG, "run: ClientThread");
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                //连接
                Log.d(TAG, "run: 开始连接...");
                socket.connect();
                Log.d(TAG, "run: 连接成功");
                readThread mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                Log.d(TAG, "run:连接异常！");
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver BTReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: 找到:" + d.getName());
                if (d.getName() == null) {
                    return;
                }
                if (d.getName().equals("HDU-BlueTooth")) {
                    Log.d(TAG, "onReceive: 开始配对");
                    device = d;
                    if (d.getBondState() == BluetoothDevice.BOND_NONE) {
                        d.createBond();
                    } else if (d.getBondState() == BluetoothDevice.BOND_BONDED) {
                        ClientThread clientConnectThread = new ClientThread();
                        clientConnectThread.start();
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 获取蓝牙设备的连接状态
                int connectState = device.getBondState();
                Log.d(TAG, "onReceive: " + connectState);
                // 已配对
                if (connectState == BluetoothDevice.BOND_BONDED) {
                    try {
                        Log.d(TAG, "onReceive: 开始连接");
                        ClientThread clientConnectThread = new ClientThread();
                        clientConnectThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(BTReceive, intentFilter);

        mBluetoothAdapter.startDiscovery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_LONG).show();
        }
    }
}
