package com.lmachine.mlda;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lmachine.mlda.algorithm.filter.FilterCallback;
import com.lmachine.mlda.algorithm.filter.KalmanFilter;
import com.lmachine.mlda.algorithm.filter.LowPassFilter;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.sport.SportInfo;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.SPUtil;
import com.lmachine.mlda.view.SensorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonitorActivity extends BaseActivity implements ServiceConnection {

    private ImageView titleImage;
    private TextView sportTitle;
    private TextView sportDes;
    private TextView tipText;
    private Chronometer chronometer;

    private SensorView dirView;
    private SensorView gyroView;
    private SensorView gravityView;
    private SensorView accView;

    private Button startButton;
    private Button saveButton;
    private Button retestButton;

    private FrameLayout countDownLayout;
    private TextView countDownText;

    private LinearLayout buttonLayout;

    private MyCountDownTimer countDownTimer = new MyCountDownTimer(3000, 100);
    private SensorService.MyBinder binder;

    private List<float[]> oriDataList = new ArrayList<>();
    private List<float[]> gyroDataList = new ArrayList<>();
    private List<float[]> gravityDataList = new ArrayList<>();
    private List<float[]> accDataList = new ArrayList<>();

    private int currentState = 0;//0: 未开始记录 1: 正在倒计时 2:正在记录 3.记录结束

    TestInfo testInfo = new TestInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        setToolbar(R.id.toolbar, true);
        sportTitle = (TextView) findViewById(R.id.tv_sport_name);
        sportDes = (TextView) findViewById(R.id.tv_sport_des);
        tipText = (TextView) findViewById(R.id.tv_tip_text);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        titleImage = (ImageView) findViewById(R.id.iv_monitor_title);
        dirView = (SensorView) findViewById(R.id.sensor_view_dir);
        gyroView = (SensorView) findViewById(R.id.sensor_view_gyro);
        gravityView = (SensorView) findViewById(R.id.sensor_view_gravity);
        accView = (SensorView) findViewById(R.id.sensor_view_acc);
        startButton = (Button) findViewById(R.id.btn_start_test);
        saveButton = (Button) findViewById(R.id.btn_save);
        retestButton = (Button) findViewById(R.id.btn_retest);
        buttonLayout = (LinearLayout) findViewById(R.id.btn_layout);
        countDownLayout = (FrameLayout) findViewById(R.id.count_down_layout);
        countDownText = (TextView) findViewById(R.id.tv_count_down);
        initView();
        bindService(new Intent(this, SensorService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        binder.stopMonitor();
        unbindService(this);
    }

    @Override
    public void onBackPressed() {
        if (currentState == 1 || currentState == 2 || currentState == 3) {
            new AlertDialog.Builder(this)
                    .setTitle("退出确认")
                    .setMessage("如果退出，测试数据将会丢失。是否确认退出？")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else {
            super.onBackPressed();
        }
    }

    protected void initView() {
        Intent intent = getIntent();
        SportInfo sport = (SportInfo) intent.getSerializableExtra("sport");

        sportTitle.setText(sport.getName());

        Glide.with(this).load(sport.getGifId()).into(titleImage);
        sportDes.setText(sport.getDes());

        dirView.setSensorName("当前方向");
        dirView.setSensorDes(getString(R.string.dir_info));
        gyroView.setSensorName("陀螺仪");
        gyroView.setSensorDes(getString(R.string.gyro_info));
        gravityView.setSensorName("重力传感器");
        gravityView.setSensorDes(getString(R.string.gravity_info));
        accView.setSensorName("线性加速度传感器");
        accView.setSensorDes(getString(R.string.acc_info));
        buttonLayout.setVisibility(View.GONE);
        countDownLayout.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState == 0) {
                    currentState = 1;
                    countDown();
                    startButton.setText("取消");
                } else if (currentState == 1) {
                    currentState = 0;
                    countDownTimer.cancel();
                    countDownLayout.setVisibility(View.GONE);
                    startButton.setText("开始测试");
                } else if (currentState == 2) {
                    chronometer.stop();
                    currentState = 3;
                    startButton.animate().translationY(500).setDuration(500);
                    buttonLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setTranslationY(500);
                    buttonLayout.animate().translationY(0).setDuration(500);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {

                final View inputView = getLayoutInflater().inflate(R.layout.times_input_dialog, null);

                AlertDialog dialog = new AlertDialog.Builder(MonitorActivity.this)
                        .setTitle("输入\"" + sportTitle.getText().toString() + "\"次数")
                        .setView(inputView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et = (EditText) inputView.findViewById(R.id.et_times_input_dialog);
                                String timesStr = et.getText().toString();
                                if (TextUtils.isEmpty(timesStr)) {
                                    testInfo.setInputTimes(0);
                                } else {
                                    testInfo.setInputTimes(Integer.parseInt(timesStr));
                                }
                                showProgressDialog("正在处理数据...");
                                saveData(new FilterCallback() {
                                    @Override
                                    public void onFilterFinished() {
                                        Intent i = new Intent();
                                        closeProgressDialog();
                                        i.putExtra("test_info", testInfo);
                                        setResult(RESULT_OK, i);
                                        finish();
                                    }
                                });
                            }
                        }).create();
                dialog.show();
            }
        });

        retestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oriDataList.clear();
                accDataList.clear();
                gravityDataList.clear();
                gyroDataList.clear();
                Log.d(TAG, "onClick: 数据已经清除");
                currentState = 0;
                startButton.animate().translationY(0).setDuration(500);
                buttonLayout.animate().translationY(500).setDuration(500);
                startButton.setText("开始测试");
            }
        });

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                int seconds = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
                chronometer.setText(String.format(Locale.getDefault(), "%02d秒", seconds));
            }
        });
    }

    private void countDown() {
        countDownText.setText("");
        countDownLayout.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (SensorService.MyBinder) service;
        SensorService sensorService = binder.getService();

        Sensor mag = sensorService.getMagSensor();
        Sensor gra = sensorService.getGravitySensor();
        Sensor gyro = sensorService.getGyroSensor();
        Sensor acc = sensorService.getLinearAccSensor();

        dirView.setSensorVendor(
                "磁场传感器:", mag.getVendor() + " " + mag.getName());

        gravityView.setSensorVendor(gra.getVendor(),
                gra.getName());

        gyroView.setSensorVendor(gyro.getVendor(),
                gyro.getName());

        accView.setSensorVendor(acc.getVendor(),
                acc.getName());

        testInfo.setMagSensorName(mag.getName());
        testInfo.setMagSensorVendor(mag.getVendor());

        testInfo.setGravitySensorName(gra.getName());
        testInfo.setGravitySensorVendor(gra.getVendor());

        testInfo.setGyroName(gyro.getName());
        testInfo.setGyroVendor(gyro.getVendor());

        testInfo.setAccelerationSensorName(acc.getName());
        testInfo.setAccelerationSensorVendor(acc.getVendor());

        sensorService.setSensorListener(new SensorService.SensorDataListener() {
            @Override
            public void onOriDataChanged(float[] data) {
                dirView.setSensorData(data);
                if (currentState == 2) {
                    oriDataList.add(data);
                }
            }

            @Override
            public void onGyroDataChanged(float[] data) {
                gyroView.setSensorData(data);
                if (currentState == 2) {
                    gyroDataList.add(data);
                }
            }

            @Override
            public void onGravityDataChanged(float[] data) {
                gravityView.setSensorData(data);
                if (currentState == 2) {
                    gravityDataList.add(data);
                }
            }

            @Override
            public void onAccDataChanged(float[] data) {
                accView.setSensorData(data);
                if (currentState == 2) {
                    accDataList.add(data);
                }
            }
        });
        binder.startMonitor();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: ");
        binder.stopMonitor();
    }

    private void saveData(final FilterCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int filterType = Integer.parseInt(SPUtil.load(MonitorActivity.this).getString("filter_type", "0"));
                testInfo.setFiltered(filterType != 0);
                testInfo.setOrientationData(new Gson().toJson(
                        filterType == 0 ? oriDataList :
                                (filterType == 1 ? KalmanFilter.filter(oriDataList) : LowPassFilter.filter(oriDataList))));
                testInfo.setGravityData(new Gson().toJson(filterType == 0 ? oriDataList :
                        (filterType == 1 ? KalmanFilter.filter(gravityDataList) : LowPassFilter.filter(gravityDataList))));
                testInfo.setGyroscopeData(new Gson().toJson(filterType == 0 ? oriDataList :
                        (filterType == 1 ? KalmanFilter.filter(gyroDataList) : LowPassFilter.filter(gyroDataList))));
                testInfo.setAccelerationData(new Gson().toJson(filterType == 0 ? oriDataList :
                        (filterType == 1 ? KalmanFilter.filter(accDataList) : LowPassFilter.filter(accDataList))));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testInfo.setDuration(Integer.valueOf(chronometer.getText().toString().split("秒")[0]));
                        callback.onFilterFinished();
                    }
                });
            }
        }).start();
    }

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "onTick: " + millisUntilFinished);
            String time = String.valueOf((millisUntilFinished / 1000) + 1);
            if (countDownText.getText().toString().equals(time)) return;
            countDownText.setAlpha(0);
            countDownText.setScaleX(0);
            countDownText.setScaleY(0);
            countDownText.setText(time);
            countDownText.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300);
        }

        @Override
        public void onFinish() {
            tipText.setText("已用时间: ");
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            currentState = 2;
            countDownLayout.setVisibility(View.GONE);
            startButton.setText("结束测试");
        }
    }
}
