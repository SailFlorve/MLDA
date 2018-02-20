package com.lmachine.mlda.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lmachine.mlda.util.SPUtil;

public class SensorService extends Service {

    private String TAG = "SensorService";
    private MyBinder binder;
    private SensorManager sensorManager;
    private Sensor magSensor;//磁场传感器
    private Sensor gyroSensor;//陀螺仪
    private Sensor gravitySensor;//重力传感器
    private Sensor linearAccSensor;//线性加速度
    private Sensor accSensor;

    private float[] accData = new float[]{};
    private float[] magData = new float[]{};
    private float[] gyroData = new float[]{};
    private float[] gravityData = new float[]{};
    private float[] linearAccData = new float[]{};

    private int rate;//频率 （毫秒）

    private SensorDataListener dataListener;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magData = event.values;
                    if (dataListener != null) {
                        if (magData.length == 0 || accData.length == 0) {
                            return;
                        }
                        float[] oriData = getOrientation(accData, magData);
                        dataListener.onOriDataChanged(oriData);
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroData = event.values;
                    if (dataListener != null) {
                        dataListener.onGyroDataChanged(new float[]{
                                gyroData[0], gyroData[1], gyroData[2]
                        });
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    gravityData = event.values;
                    if (dataListener != null) {
                        dataListener.onGravityDataChanged(new float[]{
                                gravityData[0], gravityData[1], gravityData[2]
                        });
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    linearAccData = event.values;
                    if (dataListener != null) {
                        dataListener.onAccDataChanged(new float[]{
                                linearAccData[0], linearAccData[1], linearAccData[2]
                        });
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accData = event.values;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public SensorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "onCreate: " + String.valueOf(magSensor == null)
                + String.valueOf(gyroSensor == null)
                + String.valueOf(gravitySensor == null)
                + String.valueOf(linearAccSensor == null));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        unregisterSensorListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private float[] getOrientation(float[] accData, float[] magData) {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accData, magData);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
        return values;
    }

    public void getSensorStatus(SensorStatusCallback callback) {
        callback.onSensorInit(magSensor != null, gyroSensor != null,
                gravitySensor != null, accSensor != null);
    }

    public void setSensorListener(SensorDataListener listener) {
        this.dataListener = listener;
    }

    public class MyBinder extends Binder {

        public SensorService getService() {
            return SensorService.this;
        }

        public void startMonitor() {
            String rateStr = SPUtil.load(SensorService.this).getString("data_rate", "40");
            rate = Integer.parseInt(rateStr);
            sensorManager.registerListener(sensorEventListener, magSensor, rate * 1000);
            sensorManager.registerListener(sensorEventListener, linearAccSensor, rate * 1000);
            sensorManager.registerListener(sensorEventListener, gravitySensor, rate * 1000);
            sensorManager.registerListener(sensorEventListener, gyroSensor, rate * 1000);
            sensorManager.registerListener(sensorEventListener, accSensor, rate * 1000);
        }

        public void stopMonitor() {
            unregisterSensorListener();
        }
    }

    private void unregisterSensorListener() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public interface SensorStatusCallback {
        void onSensorInit(boolean mag, boolean gyro, boolean gravity, boolean acc);
    }

    public interface SensorDataListener {
        void onOriDataChanged(float[] data);

        void onGyroDataChanged(float[] data);

        void onGravityDataChanged(float[] data);

        void onAccDataChanged(float[] data);
    }

    public Sensor getMagSensor() {
        return magSensor;
    }

    public Sensor getGyroSensor() {
        return gyroSensor;
    }

    public Sensor getGravitySensor() {
        return gravitySensor;
    }

    public Sensor getLinearAccSensor() {
        return linearAccSensor;
    }

    public int getRate() {
        return rate;
    }
}
