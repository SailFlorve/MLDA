package com.lmachine.mlda.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.lmachine.mlda.R;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 显示传感器的View
 */

public class SensorView extends CardView {

    private TextView sensorName;
    private TextView sensorX;
    private TextView sensorY;
    private TextView sensorZ;

    public SensorView(Context context) {
        super(context);
    }

    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sensor_view, this);
        sensorName = (TextView) findViewById(R.id.tv_sensor_name);
        sensorX = (TextView) findViewById(R.id.tv_sensor_x);
        sensorY = (TextView) findViewById(R.id.tv_sensor_y);
        sensorZ = (TextView) findViewById(R.id.tv_sensor_z);
        setSensorName("传感器");
        setSensorData(new float[]{0, 0, 0});
    }

    public void setSensorName(String name) {
        sensorName.setText(name);
    }

    public void setSensorData(float[] data) {
        if(data.length ==3) {
            sensorX.setText(String.format("x: %.2f", data[0]));
            sensorY.setText(String.format("y: %.2f", data[1]));
            sensorZ.setText(String.format("z: %.2f", data[2]));
        } else if (data.length == 1) {
            sensorX.setText("x: " + data[0]);
        }
    }
}
