package com.lmachine.mlda;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.SPUtil;

import java.util.Random;


public class MainActivity extends BaseActivity implements ServiceConnection {

    private TextView randomInputText;
    private TextView magText;
    private TextView gyroText;
    private TextView gravityText;
    private TextView linearAccText;
    private CardView cardView;
    private RadioGroup radioGroup;
    private TextInputLayout ageText;
    private TextInputLayout statureText;
    private TextInputLayout weightText;
    private FloatingActionButton fab;

    private TextInputLayout[] textInputLayouts;

    private boolean haveMag;
    private boolean haveGyro;
    private boolean haveGravity;
    private boolean haveAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.id.toolbar, false);
        magText = findViewById(R.id.tv_magnetic);
        gyroText = findViewById(R.id.tv_gyroscope);
        gravityText = findViewById(R.id.tv_gravity);
        linearAccText = findViewById(R.id.tv_linear_acceleration);
        cardView = findViewById(R.id.card_view);
        radioGroup = findViewById(R.id.radio_group);
        randomInputText = findViewById(R.id.tv_random);
        ageText = findViewById(R.id.til_age);
        statureText = findViewById(R.id.til_stature);
        weightText = findViewById(R.id.til_weight);
        fab = findViewById(R.id.fab);

        textInputLayouts = new TextInputLayout[]{ageText, statureText, weightText};

        cardView.setAlpha(0);
        cardView.setTranslationY(80);
        cardView.animate().alpha(1).translationY(0).setDuration(750).start();

        fab.setOnClickListener(v -> {
            Log.d(TAG, "onClick: ");
            check();
        });

        randomInputText.setOnClickListener(v -> randomInput());
        bindService(new Intent(this, SensorService.class), this, BIND_AUTO_CREATE);

        int a = 1 / 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        clearAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_all) {
            clearAll();
        } else if (item.getItemId() == R.id.manage_data) {
            startActivity(new Intent(this, DataManageActivity.class));
        } else if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearError() {
        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.setErrorEnabled(false);
        }
    }

    private void clearAll() {
        radioGroup.clearCheck();
        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.setErrorEnabled(false);
            EditText et = textInputLayout.getEditText();
            if (et != null) {
                et.setText("");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void check() {
        clearError();
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton == null) {
            showSnackBar("请选择性别。");
            return;
        }
        for (TextInputLayout textInputLayout : textInputLayouts) {
            EditText editText = textInputLayout.getEditText();
            if (editText != null) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    textInputLayout.setError("此项不能为空。");
                    return;
                }
            } else {
                return;
            }
        }

        boolean checkSensor = SPUtil.load(this).getBoolean("check_sensor", true);
        if (checkSensor && !(haveAcc && haveMag && haveGravity && haveGyro)) {
            String sb = "传感器缺失: " +
                    (haveMag ? "" : "磁场传感器, ") +
                    (haveAcc ? "" : "线性加速度传感器, ") +
                    (haveGravity ? "" : "重力传感器, ") +
                    (haveGyro ? "" : "陀螺仪, ") +
                    "无法测试。";
            showSnackBar(sb);
            return;
        }

        String checkText = radioButton.getText().toString();
        int age = Integer.parseInt(ageText.getEditText().getText().toString());
        int stature = Integer.parseInt(statureText.getEditText().getText().toString());
        int weight = Integer.parseInt(weightText.getEditText().getText().toString());
        if ((age < 14 || age > 80) || (stature < 130 || stature > 200) || (weight < 30 || weight > 150)) {
            showSnackBar("请如实填写信息，不要胡编乱造。");
            return;
        }
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("sex", checkText.substring(0, 1));
        intent.putExtra("age", age);
        intent.putExtra("stature", stature);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }

    @SuppressWarnings("ConstantConditions")
    private void randomInput() {
        Random r = new Random();
        int sex = r.nextInt(2);
        radioGroup.check(sex == 0 ? R.id.radio_button_male : R.id.radio_button_female);
        int age = r.nextInt(7) + 16;
        ageText.getEditText().setText(String.valueOf(age));
        int stature = r.nextInt(36) + 150;
        statureText.getEditText().setText(String.valueOf(stature));
        int weight = r.nextInt(41) + 40;
        weightText.getEditText().setText(String.valueOf(weight));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SensorService.MyBinder binder = (SensorService.MyBinder) service;
        SensorService sensorService = binder.getService();
        sensorService.getSensorStatus((mag, gyro, gravity, acc) -> {
            Drawable right = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_right);
            Drawable error = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_error);
            right.setBounds(0, 0, 40, 40);
            error.setBounds(0, 0, 40, 40);
            haveMag = mag;
            haveGyro = gyro;
            haveGravity = gravity;
            haveAcc = acc;
            magText.setCompoundDrawables(mag ? right : error, null, null, null);
            gyroText.setCompoundDrawables(gyro ? right : error, null, null, null);
            gravityText.setCompoundDrawables(gravity ? right : error, null, null, null);
            linearAccText.setCompoundDrawables(acc ? right : error, null, null, null);
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
