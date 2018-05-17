package com.lmachine.mlda.fragment;

import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmachine.mlda.R;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.SensorInfoMgr;

import java.util.List;

public class BuiltinSensorFragment extends Fragment {

    private TextView builtinSensorText;
    private SensorService sensorService;

    public BuiltinSensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_builtin_sensor, container, false);
        SensorService.MyBinder binder = (SensorService.MyBinder) getArguments().getBinder("service");
        if (binder != null) {
            sensorService = binder.getService();
            Log.d(getClass().getName(), "onCreateView: 已经货到Service");
        }
        builtinSensorText = view.findViewById(R.id.tv_builtin_sensor_list);

        sensorService.initBuiltinSensor(new SensorInfoMgr.BuiltinSensorInitStateCallback() {
            @Override
            public void onSuccess(List<Sensor> initSensor) {
                StringBuilder sb = new StringBuilder();
                for (Sensor sensor : initSensor) {
                    sb.append(sensor.getName()).append("\n");
                }
                builtinSensorText.setText(sb.toString());
            }

            @Override
            public void onFailed() {
                builtinSensorText.setText("传感器初始化失败。");
            }
        });

        return view;
    }

    public static BuiltinSensorFragment newInstance(SensorService service) {
        Bundle args = new Bundle();
        args.putBinder("service", service.getMyBinder());

        BuiltinSensorFragment fragment = new BuiltinSensorFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
