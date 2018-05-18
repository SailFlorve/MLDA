package com.lmachine.mlda.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.lmachine.mlda.bean.SensorInfo;
import com.lmachine.mlda.util.BluetoothDataResolver;
import com.lmachine.mlda.util.BluetoothUtil;
import com.lmachine.mlda.util.SPUtil;
import com.lmachine.mlda.util.SensorInfoMgr;

import java.util.List;

public class SensorService extends Service {

    private static final String TAG = "SensorService";
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<SensorInfo> sensorList;

    private MyBinder myBinder;

    private SensorDataChangeListener dataChangeListener;

    private BluetoothDataResolver resolver;

    private BluetoothUtil bluetoothUtil;
    private SensorInfoMgr sensorInfoMgr;

    private int rate;

    public interface SensorDataChangeListener {
        void onDataChanged(float[] data, int position);

        void onDisconnected();
    }


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            for (int i = 0; i < sensorInfoMgr.getBuiltinSensor().size(); i++) {
                if (type == sensorInfoMgr.getBuiltinSensor().get(i).getType()) {
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(event.values, i);
                    }
                    break;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private BluetoothDataResolver.DataChangedCallBack resolverDataChangedCallBack =
            new BluetoothDataResolver.DataChangedCallBack() {
                @Override
                public void onDataChanged(List<float[]> f) {
                    mainHandler.post(() -> {
                        for (int i = 0; i < f.size(); i++) {
                            if (dataChangeListener != null) {
                                dataChangeListener.onDataChanged(f.get(i), i);
                            }
                        }
                    });
                }
            };

    public SensorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorInfoMgr = new SensorInfoMgr(this);
        bluetoothUtil = new BluetoothUtil(this);
        resolver = new BluetoothDataResolver();
        rate = SPUtil.load(this).getInt("rate", 40);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSensor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        return myBinder;
    }

    public class MyBinder extends Binder {

        public SensorService getService() {
            return SensorService.this;
        }
    }

    public void initBuiltinSensor(SensorInfoMgr.BuiltinSensorInitStateCallback callback) {
        List<Sensor> builtinSensor = sensorInfoMgr.getBuiltinSensor();

        if (builtinSensor.isEmpty()) {
            callback.onFailed();
        } else {
            callback.onSuccess(builtinSensor);
        }
    }

    public void initBluetooth(BluetoothUtil.BluetoothDeviceSensorInitStateCallback callback) {
        if (bluetoothUtil.getBluetoothManager() != null) {
            callback.onSuccess();
        } else {
            callback.onFailed();
        }
    }

    public void startSensor(SensorDataChangeListener listener) {
        setDataChangeListener(listener);

        SensorManager manager = sensorInfoMgr.getSensorManager();

        switch (sensorInfoMgr.getCurrentSensorType()) {
            case SensorInfoMgr.TYPE_BUILT_IN_SENSOR:
                for (Sensor sensor : sensorInfoMgr.getBuiltinSensor()) {
                    manager.registerListener(sensorEventListener, sensor, rate * 1000);
                }
                break;
            case SensorInfoMgr.TYPE_BLUETOOTH_DEVICE_SENSOR:
                resolver.setDataChangedCallBack(resolverDataChangedCallBack);

                bluetoothUtil.startRead(new BluetoothUtil.BluetoothDataListener() {
                    @Override
                    public void onRead(byte[] b) {
                        resolver.dataSync(b);
                    }

                    @Override
                    public void onConnectClosed() {
                        dataChangeListener.onDisconnected();
                    }
                });
                break;
            default:

        }
    }

    public void stopSensor() {
        switch (sensorInfoMgr.getCurrentSensorType()) {
            case SensorInfoMgr.TYPE_BUILT_IN_SENSOR:
                sensorInfoMgr.getSensorManager().unregisterListener(sensorEventListener);
                break;
            case SensorInfoMgr.TYPE_BLUETOOTH_DEVICE_SENSOR:
                bluetoothUtil.stopRead();
                break;
            default:

        }
    }

    public boolean isBuiltinSensorEmpty() {
        return sensorInfoMgr.getBuiltinSensor().isEmpty();
    }

    public void connectBluetoothDevice(BluetoothUtil.BluetoothConnectCallback connectCallback) {
        bluetoothUtil.connect(connectCallback);
    }

    public void startDiscovery() {
        bluetoothUtil.setBondedDevice(null);
        bluetoothUtil.startDiscovery();
    }

    public void setBondedDevice(BluetoothDevice device) {
        bluetoothUtil.setBondedDevice(device);
    }

    public MyBinder getMyBinder() {
        return myBinder;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothUtil.getBluetoothAdapter();
    }

    public void setCurrentSensorType(int current) {
        sensorInfoMgr.setCurrentSensorType(current);
        sensorList = sensorInfoMgr.getSensorInfoList();
        if (current == SensorInfoMgr.TYPE_BLUETOOTH_DEVICE_SENSOR) {
            rate = 25;
        }
    }

    public void setDataChangeListener(SensorDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public List<SensorInfo> getSensorList() {
        return sensorList;
    }

    public int getRate() {
        return rate;
    }

    public SensorInfoMgr getSensorInfoMgr() {
        return sensorInfoMgr;
    }
}
