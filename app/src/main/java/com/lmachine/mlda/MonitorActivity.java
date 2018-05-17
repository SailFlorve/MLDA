package com.lmachine.mlda;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.lmachine.mlda.bean.SensorInfo;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.SPUtil;
import com.lmachine.mlda.util.SoundMgr;
import com.lmachine.mlda.util.TimeUtil;
import com.lmachine.mlda.view.CarouselTextView;
import com.lmachine.mlda.view.SensorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonitorActivity extends BaseActivity implements ServiceConnection {

    private ImageView titleImage;
    private TextView sportTitle;
    private TextView sportDes;
    private LinearLayout titleLayout;
    private CarouselTextView tipText;
    private Chronometer chronometer;

    private Button startButton;
    private Button saveButton;
    private Button retestButton;

    private FrameLayout countDownLayout;
    private TextView countDownText;

    private LinearLayout buttonLayout;
    private LinearLayout sensorViewLayout;

    private boolean isCountDown;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(3000, 100);
    private SensorService sensorService;

    private int duration;

    private int currentState = 0;//0: 未开始记录 1: 正在倒计时 2:正在记录 3.记录结束

    private TestInfo testInfo;

    private SoundMgr soundMgr;

    private List<SensorInfo> sensorInfoList;

    private List<SensorView> sensorViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_monitor);
        setToolbar(R.id.toolbar, true);
        sportTitle = findViewById(R.id.tv_sport_name);
        sportDes = findViewById(R.id.tv_sport_des);
        tipText = findViewById(R.id.tv_tip_text);
        titleLayout = findViewById(R.id.ll_sport_title);
        chronometer = findViewById(R.id.chronometer);
        sensorViewLayout = findViewById(R.id.sensor_view_layout);
        titleImage = findViewById(R.id.iv_monitor_title);

        startButton = findViewById(R.id.btn_start_test);
        saveButton = findViewById(R.id.btn_save);
        retestButton = findViewById(R.id.btn_retest);
        buttonLayout = findViewById(R.id.btn_layout);
        countDownLayout = findViewById(R.id.count_down_layout);
        countDownText = findViewById(R.id.tv_count_down);
        initSettings();
        initView();

        bindService(new Intent(this, SensorService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        sensorService.stopSensor();
        unbindService(this);
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (currentState == 1 || currentState == 2 || currentState == 3) {
            new AlertDialog.Builder(this)
                    .setTitle("退出确认")
                    .setMessage("如果退出，测试数据将会丢失。是否确认退出？")
                    .setPositiveButton("退出", (dialog, which) -> finish())
                    .setNegativeButton("取消", null)
                    .create().show();
        } else {
            super.onBackPressed();
        }
    }

    protected void initSettings() {
        SPUtil.SharedPrefsManager spMgr = SPUtil.load(this);
        isCountDown = spMgr.getBoolean("count_down", true);
    }

    protected void initView() {
        Intent intent = getIntent();
        testInfo = (TestInfo) intent.getSerializableExtra("test_info");

        PopupMenu popupMenu = new PopupMenu(MonitorActivity.this, titleLayout);
        String[] sportArray = getResources().getStringArray(R.array.sport_array);
        for (String s : sportArray) {
            popupMenu.getMenu().add(s);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            sportTitle.setText(item.getTitle());
            return true;
        });

        titleLayout.setOnClickListener(v -> popupMenu.show());

        sportTitle.setText(sportArray[0]);
        sportDes.setText(String.format(
                Locale.getDefault(),
                "性别: %s  年龄: %d  身高: %dcm  体重: %d千克",
                testInfo.getSex(),
                testInfo.getAge(),
                testInfo.getStature(),
                testInfo.getWeight()));

        tipText.setTextArray(getResources().getStringArray(R.array.tip_text));
        Glide.with(this).load(R.drawable.bg_high_knees).into(titleImage);

//        dirView.setSensorName(getString(R.string.current_dir));
//        dirView.setSensorDes(getString(R.string.dir_info));
//        dirView.setYAxisRange(-5, 5);
//        dirView.setShow(false, true, true, false);
//        gyroView.setSensorName(getString(R.string.gyro));
//        gyroView.setSensorDes(getString(R.string.gyro_info));
//        gyroView.setYAxisRange(-10, 10);
//        gyroView.setShow(false, false, false, true);
//        gravityView.setSensorName(getString(R.string.gravity_sensor));
//        gravityView.setSensorDes(getString(R.string.gravity_info));
//        gravityView.setShow(true, true, true, false);
//        gravityView.setYAxisRange(-12, 12);
//        accView.setSensorName(getString(R.string.linear_acc_sensor));
//        accView.setSensorDes(getString(R.string.acc_info));
//        accView.setYAxisRange(-20, 20);
//        accView.setShow(false, false, false, true);

        buttonLayout.setVisibility(View.GONE);
        countDownLayout.setVisibility(View.GONE);

        soundMgr = new SoundMgr(this,
                R.raw.one,
                R.raw.two,
                R.raw.three,
                R.raw.go);


        startButton.setOnClickListener(v -> {
            //点击了开始测试
            if (currentState == 0) {
                startButton.setText("取消");
                currentState = 1;
                if (isCountDown) {
                    countDown();
                } else {
                    finishCountDown();
                }
            } else if (currentState == 1) {
                //点击了取消
                tipText.setVisibility(View.VISIBLE);
                currentState = 0;
                countDownTimer.cancel();
                countDownLayout.setVisibility(View.GONE);
                startButton.setText("开始测试");
            } else if (currentState == 2) {
                //点击了结束测试
                chronometer.stop();
                chronometer.setVisibility(View.GONE);
                tipText.setVisibility(View.VISIBLE);
                currentState = 3;
                startButton.animate().translationY(500).setDuration(500);
                buttonLayout.setVisibility(View.VISIBLE);
                buttonLayout.setTranslationY(500);
                buttonLayout.animate().translationY(0).setDuration(500);
                testInfo.setDuration(duration);
            }
        });

        saveButton.setOnClickListener(v -> {

            final View inputView = getLayoutInflater().inflate(R.layout.times_input_dialog, null);
//            final AppCompatSeekBar seekBar = inputView.findViewById(R.id.seek_bar);
//            final TextView tv = inputView.findViewById(R.id.tv_seek_bar);
//            tv.setText("不会去除数据。");
//            seekBar.setMax(testInfo.getDuration());
//
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (progress != 0) {
//                        tv.setText("将会去除最后" + progress + "秒的数据。");
//                    } else {
//                        tv.setText("不会去除数据。");
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });

            AlertDialog dialog = new AlertDialog.Builder(MonitorActivity.this)
                    .setTitle("额外信息输入")
                    .setView(inputView)
                    .setPositiveButton("确定", (dialog1, which) -> {
                        EditText times = inputView.findViewById(R.id.et_times_input_dialog);
                        EditText remark = inputView.findViewById(R.id.et_remark);
                        String timesStr = times.getText().toString();
                        String remarkStr = remark.getText().toString();

                        testInfo.setInputTimes(TextUtils.isEmpty(timesStr) ? 0 : Integer.parseInt(timesStr));
                        testInfo.setRemark(TextUtils.isEmpty(remarkStr) ? "无" : remarkStr);
                        testInfo.setType(sportTitle.getText().toString());
                        testInfo.setTime(TimeUtil.getNowTime(TimeUtil.A));
                        testInfo.setRate(sensorService.getRate());

                        String jsonStr = new Gson().toJson(sensorInfoList);
                        testInfo.setSensorData(jsonStr);

//                        showProgressDialog("正在处理数据...");
//                        closeProgressDialog();

                        Intent i = new Intent();
                        i.putExtra("test_info", testInfo);
                        setResult(RESULT_OK, i);
                        finish();

                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
        });

        retestButton.setOnClickListener(v -> {
//            oriDataList.clear();
//            accDataList.clear();
//            gravityDataList.clear();
//            gyroDataList.clear();
            Log.d(TAG, "onClick: 数据已经清除");
            currentState = 0;
            startButton.animate().translationY(0).setDuration(500);
            buttonLayout.animate().translationY(500).setDuration(500);
            startButton.setText("开始测试");
            tipText.setVisibility(View.VISIBLE);
            chronometer.setVisibility(View.GONE);
        });

        chronometer.setOnChronometerTickListener(chronometer -> {
            int seconds = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
            duration = seconds;
            chronometer.setText(String.format(Locale.getDefault(), "已用时间: %02d秒", seconds));
        });
    }

    private void countDown() {
        countDownText.setText("");
        countDownLayout.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

    private void showCountDownText(String time) {
        if (countDownText.getText().toString().equals(time)) return;
        countDownText.setAlpha(0);
        countDownText.setScaleX(1.5f);
        countDownText.setScaleY(1.5f);
        countDownText.setText(time);
        countDownText.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300);

        switch (time) {
            case "1":
                soundMgr.play(0);
                break;
            case "2":
                soundMgr.play(1);
                break;
            case "3":
                soundMgr.play(2);
                break;
        }

    }

    private void finishCountDown() {
        soundMgr.play(3);
        tipText.setVisibility(View.GONE);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        currentState = 2;
        countDownLayout.setVisibility(View.GONE);
        startButton.setText("结束测试");
    }

    private void initSensorViewLayout() {
        sensorViewList = new ArrayList<>();

        for (SensorInfo sensorInfo : sensorInfoList) {
            SensorView sensorView = new SensorView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            sensorView.setSensorName(sensorInfo.getName());
            sensorView.setSensorDes(sensorInfo.getDes());
            sensorView.setRate(sensorService.getRate());
            sensorView.setYAxisRange(sensorInfo.getRange()[0], sensorInfo.getRange()[1]);
            sensorView.setSensorVendor(sensorInfo.getVendor());
            boolean[] showedArr = sensorInfo.getAxisShowed();
            sensorView.setShow(showedArr[0], showedArr[1], showedArr[2], showedArr[3]);

            sensorViewLayout.addView(sensorView, params);
            sensorViewList.add(sensorView);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SensorService.MyBinder myBinder = (SensorService.MyBinder) service;
        sensorService = myBinder.getService();

        sensorInfoList = sensorService.getSensorList();
        initSensorViewLayout();

        sensorService.startSensor(new SensorService.SensorDataChangeListener() {
            @Override
            public void onDataChanged(float[] data, int position) {
                if (sensorViewList == null) {
                    return;
                }
                if (position <= sensorViewList.size()) {
                    sensorViewList.get(position).setSensorData(data);
                }
                if (currentState == 2) {
                    sensorInfoList.get(position).addData(new float[]{data[0], data[1], data[2]});
                }
                closeProgressDialog();
            }

            @Override
            public void onDisconnected() {
                showProgressDialog("连接中断...");
            }
        });


    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: ");

        sensorService.stopSensor();
    }

//    //去除尾部、滤波
//    private void handleData(final int subTimes, final FilterCallback callback) {
//
//        new Thread(() -> {
//            int subEnd = subTimes * 1000 / testInfo.getRate();
//            for (SensorInfo sensorInfo : sensorInfoList) {
//                List<float[]> data = sensorInfo.getData();
//                data.subList(data.size() - 1 - subEnd, data.size() - 1).clear();
//                int filterType = Integer.parseInt(SPUtil.load(MonitorActivity.this).getString("filter_type", "0"));
//                testInfo.setFiltered(filterType != 0);
//                data = filterType == 1 ? KalmanFilter.filter(data) : LowPassFilter.filter(data);
//            }
//
//            if (oriDataList.size() > 0 && gravityDataList.size() > 0
//                    && gyroDataList.size() > 0 && accDataList.size() > 0) {
//                oriDataList.subList(oriDataList.size() - 1 - subEnd, oriDataList.size() - 1).clear();
//                gravityDataList.subList(gravityDataList.size() - 1 - subEnd, gravityDataList.size() - 1).clear();
//                gyroDataList.subList(gyroDataList.size() - 1 - subEnd, gyroDataList.size() - 1).clear();
//                accDataList.subList(accDataList.size() - 1 - subEnd, accDataList.size() - 1).clear();
//
//                int filterType = Integer.parseInt(SPUtil.load(MonitorActivity.this).getString("filter_type", "0"));
//                testInfo.setFiltered(filterType != 0);
//                testInfo.setOrientationData(new Gson().toJson(
//                        filterType == 0 ? oriDataList :
//                                (filterType == 1 ? KalmanFilter.filter(oriDataList) : LowPassFilter.filter(oriDataList))));
//                testInfo.setGravityData(new Gson().toJson(filterType == 0 ? gravityDataList :
//                        (filterType == 1 ? KalmanFilter.filter(gravityDataList) : LowPassFilter.filter(gravityDataList))));
//                testInfo.setGyroscopeData(new Gson().toJson(filterType == 0 ? gyroDataList :
//                        (filterType == 1 ? KalmanFilter.filter(gyroDataList) : LowPassFilter.filter(gyroDataList))));
//                testInfo.setAccelerationData(new Gson().toJson(filterType == 0 ? accDataList :
//                        (filterType == 1 ? KalmanFilter.filter(accDataList) : LowPassFilter.filter(accDataList))));
//            }
//            runOnUiThread(callback::onFilterFinished);
//        }).start();
//    }

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
            //Log.d(TAG, "onTick: " + millisUntilFinished);
            String time = String.valueOf((millisUntilFinished / 1000) + 1);
            showCountDownText(time);
        }

        @Override
        public void onFinish() {
            finishCountDown();
        }
    }
}
