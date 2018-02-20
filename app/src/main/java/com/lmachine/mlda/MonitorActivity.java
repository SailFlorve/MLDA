package com.lmachine.mlda;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
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
import android.widget.SeekBar;
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
import com.lmachine.mlda.util.SoundMgr;
import com.lmachine.mlda.view.CarouselTextView;
import com.lmachine.mlda.view.SensorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonitorActivity extends BaseActivity implements ServiceConnection {

    private ImageView titleImage;
    private TextView sportTitle;
    private TextView sportDes;
    private CarouselTextView tipText;
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

    private boolean isCountDown;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(3000, 100);
    private SensorService.MyBinder binder;

    private List<float[]> oriDataList = new ArrayList<>();
    private List<float[]> gyroDataList = new ArrayList<>();
    private List<float[]> gravityDataList = new ArrayList<>();
    private List<float[]> accDataList = new ArrayList<>();

    private int duration;

    private int currentState = 0;//0: 未开始记录 1: 正在倒计时 2:正在记录 3.记录结束

    private TestInfo testInfo = new TestInfo();

    private SoundMgr soundMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_monitor);
        setToolbar(R.id.toolbar, true);
        sportTitle = findViewById(R.id.tv_sport_name);
        sportDes = findViewById(R.id.tv_sport_des);
        tipText = findViewById(R.id.tv_tip_text);
        chronometer = findViewById(R.id.chronometer);
        titleImage = findViewById(R.id.iv_monitor_title);
        dirView = findViewById(R.id.sensor_view_dir);
        gyroView = findViewById(R.id.sensor_view_gyro);
        gravityView = findViewById(R.id.sensor_view_gravity);
        accView = findViewById(R.id.sensor_view_acc);
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
        SportInfo sport = (SportInfo) intent.getSerializableExtra("sport");

        sportTitle.setText(sport.getName());
        tipText.setTextArray(getResources().getStringArray(R.array.tip_text));
        Glide.with(this).load(sport.getGifId()).into(titleImage);
        sportDes.setText(sport.getDes());

        dirView.setSensorName(getString(R.string.current_dir));
        dirView.setSensorDes(getString(R.string.dir_info));
        dirView.setYAxisRange(-240, 240);
        dirView.setShow(false, true, true, false);
        gyroView.setSensorName(getString(R.string.gyro));
        gyroView.setSensorDes(getString(R.string.gyro_info));
        gyroView.setYAxisRange(-10, 10);
        gyroView.setShow(false, false, false, true);
        gravityView.setSensorName(getString(R.string.gravity_sensor));
        gravityView.setSensorDes(getString(R.string.gravity_info));
        gravityView.setShow(true, true, true, false);
        gravityView.setYAxisRange(-12, 12);
        accView.setSensorName(getString(R.string.linear_acc_sensor));
        accView.setSensorDes(getString(R.string.acc_info));
        accView.setYAxisRange(-20, 20);
        accView.setShow(false, false, false, true);
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
            final AppCompatSeekBar seekBar = inputView.findViewById(R.id.seek_bar);
            final TextView tv = inputView.findViewById(R.id.tv_seek_bar);
            tv.setText("拖动选择要从尾部删除的数据的秒数。");
            seekBar.setMax(testInfo.getDuration());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress != 0) {
                        tv.setText("将会去除最后" + progress + "秒的数据。");
                    } else {
                        tv.setText("不会去除数据。");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            AlertDialog dialog = new AlertDialog.Builder(MonitorActivity.this)
                    .setTitle("数据处理")
                    .setView(inputView)
                    .setPositiveButton("确定", (dialog1, which) -> {
                        EditText times = inputView.findViewById(R.id.et_times_input_dialog);
                        EditText remark = inputView.findViewById(R.id.et_remark);
                        String timesStr = times.getText().toString();
                        String remarkStr = remark.getText().toString();

                        testInfo.setInputTimes(TextUtils.isEmpty(timesStr) ? 0 : Integer.parseInt(timesStr));
                        testInfo.setRemark(TextUtils.isEmpty(remarkStr) ? "无" : remarkStr);

                        showProgressDialog("正在处理数据...");
                        handleData(seekBar.getProgress(), () -> {
                            Intent i = new Intent();
                            closeProgressDialog();
                            i.putExtra("test_info", testInfo);
                            setResult(RESULT_OK, i);
                            finish();
                        });
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
        });

        retestButton.setOnClickListener(v -> {
            oriDataList.clear();
            accDataList.clear();
            gravityDataList.clear();
            gyroDataList.clear();
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (SensorService.MyBinder) service;
        SensorService sensorService = binder.getService();

        Sensor mag = sensorService.getMagSensor();
        Sensor gra = sensorService.getGravitySensor();
        Sensor gyro = sensorService.getGyroSensor();
        Sensor acc = sensorService.getLinearAccSensor();

        if (mag != null) {
            dirView.setSensorVendor("磁场传感器:",
                    mag.getVendor() + " " + mag.getName());
            testInfo.setMagSensorName(mag.getName());
            testInfo.setMagSensorVendor(mag.getVendor());
        }

        if (gra != null) {
            gravityView.setSensorVendor(gra.getVendor(),
                    gra.getName());
            testInfo.setGravitySensorName(gra.getName());
            testInfo.setGravitySensorVendor(gra.getVendor());
        }

        if (gyro != null) {
            gyroView.setSensorVendor(gyro.getVendor(),
                    gyro.getName());
            testInfo.setGyroName(gyro.getName());
            testInfo.setGyroVendor(gyro.getVendor());

        }

        if (acc != null) {
            accView.setSensorVendor(acc.getVendor(),
                    acc.getName());
            testInfo.setAccelerationSensorName(acc.getName());
            testInfo.setAccelerationSensorVendor(acc.getVendor());
        }

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

        testInfo.setRate(sensorService.getRate());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: ");
        binder.stopMonitor();
    }

    //去除尾部、滤波
    private void handleData(final int subTimes, final FilterCallback callback) {

        new Thread(() -> {
            int subEnd = subTimes * 1000 / testInfo.getRate();
            if (oriDataList.size() > 0 && gravityDataList.size() > 0
                    && gyroDataList.size() > 0 && accDataList.size() > 0) {
                oriDataList.subList(oriDataList.size() - 1 - subEnd, oriDataList.size() - 1).clear();
                gravityDataList.subList(gravityDataList.size() - 1 - subEnd, gravityDataList.size() - 1).clear();
                gyroDataList.subList(gyroDataList.size() - 1 - subEnd, gyroDataList.size() - 1).clear();
                accDataList.subList(accDataList.size() - 1 - subEnd, accDataList.size() - 1).clear();

                int filterType = Integer.parseInt(SPUtil.load(MonitorActivity.this).getString("filter_type", "0"));
                testInfo.setFiltered(filterType != 0);
                testInfo.setOrientationData(new Gson().toJson(
                        filterType == 0 ? oriDataList :
                                (filterType == 1 ? KalmanFilter.filter(oriDataList) : LowPassFilter.filter(oriDataList))));
                testInfo.setGravityData(new Gson().toJson(filterType == 0 ? gravityDataList :
                        (filterType == 1 ? KalmanFilter.filter(gravityDataList) : LowPassFilter.filter(gravityDataList))));
                testInfo.setGyroscopeData(new Gson().toJson(filterType == 0 ? gyroDataList :
                        (filterType == 1 ? KalmanFilter.filter(gyroDataList) : LowPassFilter.filter(gyroDataList))));
                testInfo.setAccelerationData(new Gson().toJson(filterType == 0 ? accDataList :
                        (filterType == 1 ? KalmanFilter.filter(accDataList) : LowPassFilter.filter(accDataList))));
            }
            runOnUiThread(callback::onFilterFinished);
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
