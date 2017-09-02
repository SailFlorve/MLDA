package com.lmachine.mlda;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lmachine.mlda.bean.SensorData;
import com.lmachine.mlda.constant.SportType;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.view.SensorView;

import java.util.ArrayList;
import java.util.List;

public class MonitorActivity extends BaseActivity implements ServiceConnection {

    private ImageView titleImage;
    private TextView sportTitle;
    private TextView sportDes;

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

    private MyCountDownTimer countDownTimer = new MyCountDownTimer(3000, 300);
    private SensorService.MyBinder binder;

    private List<float[]> oriDataList = new ArrayList<>();
    private List<float[]> gyroDataList = new ArrayList<>();
    private List<float[]> gravityDataList = new ArrayList<>();
    private List<float[]> accDataList = new ArrayList<>();

    private int currentState = 0;//0: 未开始记录 1: 正在倒计时 2:正在记录 3.记录结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        setToolbar(R.id.toolbar, true);
        sportTitle = (TextView) findViewById(R.id.tv_sport_name);
        sportDes = (TextView) findViewById(R.id.tv_sport_des);
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
        String sportName = intent.getStringExtra("sport");
        sportTitle.setText(sportName);
        switch (sportName) {
            case SportType.HIGH_KNEES:
                Glide.with(this).load(R.drawable.high_knees).into(titleImage);
                sportDes.setText(getString(R.string.high_knees_des));
                break;
            case SportType.JUMPING_JACKS:
                Glide.with(this).load(R.drawable.jumping_jacks).into(titleImage);
                sportDes.setText(getString(R.string.jumping_jack_des));
                break;
            case SportType.SMALL_JUMP:
                Glide.with(this).load(R.drawable.small_jump).into(titleImage);
                sportDes.setText(getString(R.string.small_jump_des));
                break;
        }
        dirView.setSensorName("当前方向");
        dirView.setSensorInfo(getString(R.string.dir_info));
        gyroView.setSensorName("陀螺仪");
        gyroView.setSensorInfo(getString(R.string.gyro_info));
        gravityView.setSensorName("重力传感器");
        gravityView.setSensorInfo(getString(R.string.gravity_info));
        accView.setSensorName("线性加速度传感器");
        accView.setSensorInfo(getString(R.string.acc_info));
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
                    currentState = 3;
                    startButton.animate().translationY(500).setDuration(500);
                    buttonLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setTranslationY(500);
                    buttonLayout.animate().translationY(0).setDuration(500);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                SensorData ori = new SensorData();
                SensorData gyro = new SensorData();
                SensorData gra = new SensorData();
                SensorData acc = new SensorData();
                ori.setData(oriDataList);
                gyro.setData(gyroDataList);
                gra.setData(gravityDataList);
                acc.setData(accDataList);
                i.putExtra("oriData", new Gson().toJson(ori, SensorData.class));
                i.putExtra("gyroData", new Gson().toJson(gyro, SensorData.class));
                i.putExtra("graData", new Gson().toJson(gra, SensorData.class));
                i.putExtra("accData", new Gson().toJson(acc, SensorData.class));
                setResult(RESULT_OK, i);
                finish();
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
        sensorService.setSensorListener(new SensorService.SensorDataListener() {
            @Override
            public void onSensorDataChanged(float[] dirData, float[] linearAccData, float[] gravityData, float[] gyroData) {
                //Log.d(TAG, "onSensorDataChanged: 回调数据...");
                dirView.setSensorData(dirData);
                accView.setSensorData(linearAccData);
                gravityView.setSensorData(gravityData);
                gyroView.setSensorData(gyroData);
                if (currentState == 2) {
                    //Log.d(TAG, "onSensorDataChanged: 添加数据...");
                    oriDataList.add(dirData);
                    accDataList.add(linearAccData);
                    gravityDataList.add(gravityData);
                    gyroDataList.add(gyroData);
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

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
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
            currentState = 2;
            countDownLayout.setVisibility(View.GONE);
            startButton.setText("结束测试");
        }
    }
}
