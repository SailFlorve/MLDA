package com.lmachine.mlda.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.SparseIntArray;

import com.lmachine.mlda.R;
import com.lmachine.mlda.bean.SensorInfo;

import java.util.ArrayList;
import java.util.List;

public class SensorInfoMgr {

    /**
     * 内置传感器初始化回调
     */
    public interface BuiltinSensorInitStateCallback {
        void onSuccess(List<Sensor> initSensor);

        void onFailed();
    }


    /*传感器使用类型*/
    public static final int TYPE_BUILT_IN_SENSOR = 1;
    public static final int TYPE_BLUETOOTH_DEVICE_SENSOR = 2;

    /*蓝牙传感器类型*/
    private static final int TYPE_BLUETOOTH_HOST = 101;
    private static final int TYPE_BLUETOOTH_SLAVE_1 = 102;
    private static final int TYPE_BLUETOOTH_SLAVE_2 = 103;
    private static final int TYPE_BLUETOOTH_SLAVE_3 = 104;

    private Context context;
    private SensorManager sensorManager;

    private int currentSensorType;

    private List<Sensor> builtinSensor;
    private List<Integer> bluetoothSensor;

    private String[] nameArray;
    private String[] desArray;
    private int[][] rangeArray;
    private boolean[][] showedArray;

    private SparseIntArray sensorTypeMap;

    public SensorInfoMgr(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        builtinSensor = new ArrayList<>();
        bluetoothSensor = new ArrayList<>();

        initMap();
        initBuiltin();
        initBluetooth();

        currentSensorType = TYPE_BUILT_IN_SENSOR;
    }

    private void initMap() {
        int[] sensorTypeArray = new int[]{
                Sensor.TYPE_GRAVITY,
                Sensor.TYPE_GYROSCOPE,
                Sensor.TYPE_LINEAR_ACCELERATION,
                Sensor.TYPE_MAGNETIC_FIELD,
                TYPE_BLUETOOTH_HOST,
                TYPE_BLUETOOTH_SLAVE_1,
                TYPE_BLUETOOTH_SLAVE_2,
                TYPE_BLUETOOTH_SLAVE_3
        };

        sensorTypeMap = new SparseIntArray();
        for (int i = 0; i < sensorTypeArray.length; i++) {
            sensorTypeMap.put(sensorTypeArray[i], i + 1);
        }
    }

    private void initBuiltin() {
        Sensor[] builtin = new Sensor[]{
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        };


        for (Sensor sensor : builtin) {
            if (sensor != null) {
                builtinSensor.add(sensor);
            }
        }
    }

    private void initBluetooth() {
        int[] bluetooth = new int[]{
                TYPE_BLUETOOTH_HOST,
                TYPE_BLUETOOTH_SLAVE_1,
                TYPE_BLUETOOTH_SLAVE_2,
                TYPE_BLUETOOTH_SLAVE_3
        };

        for (int i : bluetooth) {
            bluetoothSensor.add(i);
        }
    }

    public List<Sensor> getBuiltinSensor() {
        return builtinSensor;
    }

    public List<Integer> getBluetoothSensor() {
        return bluetoothSensor;
    }

    public List<SensorInfo> getSensorInfoList() {
        List<SensorInfo> list = new ArrayList<>();
        switch (currentSensorType) {
            case TYPE_BUILT_IN_SENSOR:
                for (Sensor sensor : builtinSensor) {
                    list.add(new SensorInfo(
                            generateSensorName(sensor.getType()),
                            sensor.getVendor(),
                            generateSensorDes(sensor.getType()),
                            generateRange(sensor.getType()),
                            generateAxisShowed(sensor.getType())));
                }
                break;
            case TYPE_BLUETOOTH_DEVICE_SENSOR:
                for (Integer sensor : bluetoothSensor) {
                    list.add(new SensorInfo(
                            generateSensorName(sensor),
                            generateBluetoothSensorVendor(sensor),
                            generateSensorDes(sensor),
                            generateRange(sensor),
                            generateAxisShowed(sensor)
                    ));
                }
                break;
            default:
        }
        return list;
    }

    String generateSensorName(int sensorType) {
        if (nameArray == null) {
            nameArray = new String[]{
                    "未知传感器",

                    context.getString(R.string.gravity_sensor),
                    context.getString(R.string.gyro),
                    context.getString(R.string.linear_acc_sensor),
                    context.getString(R.string.mag_sensor),

                    "主机",
                    "从机1",
                    "从机2",
                    "从机3"
            };
        }
        int index = sensorTypeMap.get(sensorType);
        return nameArray[index];

    }

    private String generateSensorDes(int type) {
        if (desArray == null) {
            desArray = new String[]{
                    "无描述。",

                    context.getString(R.string.gravity_info),
                    context.getString(R.string.gyro_info),
                    context.getString(R.string.acc_info),
                    context.getString(R.string.dir_info),

                    "主机加速度。",
                    "从机1加速度。",
                    "从机2加速度。",
                    "从机3加速度。"
            };
        }
        int index = sensorTypeMap.get(type);
        return desArray[index];
    }

    private int[] generateRange(int type) {
        if (rangeArray == null) {
            rangeArray = new int[][]{
                    {-10, 10},

                    {-12, 12},
                    {-10, 10},
                    {-20, 20},
                    {-100, 100},

                    {-10, 30},
                    {-10, 30},
                    {-10, 30},
                    {-10, 30}
            };
        }
        int index = sensorTypeMap.get(type);
        return rangeArray[index];
    }

    private boolean[] generateAxisShowed(int type) {
        if (showedArray == null) {
            showedArray = new boolean[][]{
                    {true, true, true, true},

                    {true, true, true, false},
                    {false, false, false, true},
                    {false, false, false, true},
                    {false, true, true, false},

                    {false, false, false, true},
                    {false, false, false, true},
                    {false, false, false, true},
                    {false, false, false, true}
            };
        }
        int index = sensorTypeMap.get(type);
        return showedArray[index];
    }

    private String generateBluetoothSensorVendor(int type) {
        switch (type) {
            case TYPE_BLUETOOTH_HOST:
                return "主机芯片";
            case TYPE_BLUETOOTH_SLAVE_1:
                return "从机1芯片";
            case TYPE_BLUETOOTH_SLAVE_2:
                return "从机2芯片";
            case TYPE_BLUETOOTH_SLAVE_3:
                return "从机3芯片";
            default:
                return "未知芯片";
        }
    }

    public int getCurrentSensorType() {
        return currentSensorType;
    }

    public void setCurrentSensorType(int currentSensorType) {
        this.currentSensorType = currentSensorType;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }
}
