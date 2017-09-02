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

public class SensorService extends Service {

    private String TAG = "SensorService";
    private MyBinder binder;
    private SensorManager sensorManager;
    private Sensor magSensor;//磁场传感器
    private Sensor gyroSensor;//陀螺仪
    private Sensor gravitySensor;//重力传感器
    private Sensor linearAccSensor;//线性加速度

    private float[] magData = new float[]{};
    private float[] gyroData = new float[]{};
    private float[] gravityData = new float[]{};
    private float[] linearAccData = new float[]{};

    private SensorDataListener dataListener;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magData = event.values;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroData = event.values;
                    break;
                case Sensor.TYPE_GRAVITY:
                    gravityData = event.values;
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    linearAccData = event.values;
                    break;
                default:
            }
            if (dataListener != null) {
                if (magData.length == 0 || linearAccData.length == 0
                        || gravityData.length == 0 || gyroData.length == 0) {
                    return;
                }
                float[] oriData = getOrientation(gravityData, linearAccData, magData);
                dataListener.onSensorDataChanged(oriData, linearAccData, gravityData, gyroData);
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
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private float[] getOrientation(float[] gravityData, float[] linearAccData, float[] magData) {
        float[] values = new float[3];
        float[] R = new float[9];
        float[] accData = new float[]{gravityData[0] + linearAccData[0],
                gravityData[1] + linearAccData[1],
                gravityData[2] + linearAccData[2]};
        SensorManager.getRotationMatrix(R, null, accData, magData);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
        return values;
    }

    public void getSensorStatus(SensorStatusCallback callback) {
        callback.onSensorInit(magSensor != null, gyroSensor != null,
                gravitySensor != null, linearAccSensor != null);
    }

    public void setSensorListener(SensorDataListener listener) {
        this.dataListener = listener;
    }

    public class MyBinder extends Binder {

        public SensorService getService() {
            return SensorService.this;
        }

        public void startMonitor() {
            sensorManager.registerListener(sensorEventListener, magSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(sensorEventListener, linearAccSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(sensorEventListener, gyroSensor, SensorManager.SENSOR_DELAY_UI);
        }

        public void stopMonitor() {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public interface SensorStatusCallback {
        void onSensorInit(boolean mag, boolean gyro, boolean gravity, boolean acc);
    }

    public interface SensorDataListener {
        void onSensorDataChanged(float[] oriData, float[] linearAccData, float[] gravityData, float[] gyroData);
    }
}
