package com.lmachine.mlda.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothUtil {

    public interface BluetoothConnectCallback {
        void onSuccess(BluetoothSocket socket);

        void onFailed(String msg);
    }

    public interface BluetoothDataListener {
        void onRead(byte[] b);

        void onConnectClosed();
    }

    public interface BluetoothDeviceSensorInitStateCallback {
        void onSuccess();

        void onFailed();

    }

    public static final int rate = 25;

    private Handler mainHandler;
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bondedDevice;
    private BluetoothSocket socket;

    private BluetoothDataListener dataListener;

    private Thread readThread = null;

    private Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            InputStream is = null;
            try {
                is = socket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if (is == null) return;
            byte[] b = new byte[16];

            while (true) {
                try {
                    int num = is.read(b);

                    byte[] newByte = new byte[num];
                    System.arraycopy(b, 0, newByte, 0, num);

                    StringBuilder sb = new StringBuilder();
                    for (byte b1 : newByte) {
                        sb.append(Integer.toHexString(b1 & 0xff)).append(" ");
                    }
                    if (dataListener != null) {
                        dataListener.onRead(newByte);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (dataListener != null) {
                        mainHandler.post(() -> dataListener.onConnectClosed());
                    }
                    break;
                }
            }
        }
    };

    public BluetoothUtil(Context context) {
        mainHandler = new Handler(Looper.getMainLooper());
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public void startDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.startDiscovery();
        }
    }

    public void stopDiscovery() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void setBondedDevice(BluetoothDevice device) {
        bondedDevice = device;
    }

    public void connect(BluetoothConnectCallback connectCallback) {
        if (!bluetoothAdapter.isEnabled()) {
            connectCallback.onFailed("请打开蓝牙。");
        } else if (bluetoothAdapter.isDiscovering()) {
            connectCallback.onFailed("请等待设备搜索完毕。");
        } else if (this.bondedDevice == null) {
            connectCallback.onFailed("请先配对。");
        } else if (bondedDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
            connectCallback.onFailed("配对状态失效。");
        } else {
            Thread thread = new Thread(() -> {
                try {
                    socket = bondedDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    socket.connect();
                    mainHandler.post(() -> connectCallback.onSuccess(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(() -> connectCallback.onFailed("连接失败, " + e.getMessage()));

                }
            });
            thread.start();
        }
    }

    public void startRead(BluetoothDataListener listener) {
        this.dataListener = listener;
        if (socket == null || !socket.isConnected()) {
            return;
        }
        readThread = new Thread(readRunnable);
        readThread.start();
    }

    public void stopRead() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
        if (readThread != null) {
            readThread = null;
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
