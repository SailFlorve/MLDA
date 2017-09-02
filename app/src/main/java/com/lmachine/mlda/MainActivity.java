package com.lmachine.mlda;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.SaveUtil;
import com.lmachine.mlda.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;


public class MainActivity extends BaseActivity implements ServiceConnection {

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
    private LinearLayout rootLayout;

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
        magText = (TextView) findViewById(R.id.tv_magnetic);
        gyroText = (TextView) findViewById(R.id.tv_gyroscope);
        gravityText = (TextView) findViewById(R.id.tv_gravity);
        linearAccText = (TextView) findViewById(R.id.tv_linear_acceleration);
        cardView = (CardView) findViewById(R.id.card_view);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        ageText = (TextInputLayout) findViewById(R.id.til_age);
        statureText = (TextInputLayout) findViewById(R.id.til_stature);
        weightText = (TextInputLayout) findViewById(R.id.til_weight);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        textInputLayouts = new TextInputLayout[]{ageText, statureText, weightText};

        cardView.setAlpha(0);
        cardView.setTranslationY(80);
        cardView.animate().alpha(1).translationY(0).setDuration(750).start();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        bindService(new Intent(this, SensorService.class), this, BIND_AUTO_CREATE);
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
        } else if (item.getItemId() == R.id.show_data) {
            //startActivity(new Intent(this, DataPresentActivity.class));
            int num = (int) new Select().from(TestInfo.class).count();
            showSnackBar(rootLayout, "共有" + num + "条历史记录，查看功能正在开发中。");
        } else if (item.getItemId() == R.id.output_data) {
            if (checkPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                outputData();
            }
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
        RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton == null) {
            showSnackBar(rootLayout, "请选择性别。");
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
        if (!haveMag || !haveAcc || !haveGravity || !haveGyro) {
            showSnackBar(rootLayout, "传感器缺失，无法测试。");
            return;
        }
        String checkText = radioButton.getText().toString();
        int age = Integer.parseInt(ageText.getEditText().getText().toString());
        int stature = Integer.parseInt(statureText.getEditText().getText().toString());
        int weight = Integer.parseInt(weightText.getEditText().getText().toString());
        if ((age < 14 || age > 80) || (stature < 130 || stature > 200) || (weight < 30 || weight > 150)) {
            showSnackBar(rootLayout, "请如实填写信息，不要胡编乱造。");
            return;
        }
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("sex", checkText.substring(0, 1));
        intent.putExtra("age", age);
        intent.putExtra("stature", stature);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }

    private void outputData() {
        List<TestInfo> testInfoList = new Select().from(TestInfo.class).queryList();
        if (testInfoList.isEmpty()) {
            showSnackBar(rootLayout, "数据为空！");
            return;
        }
        String str = new Gson().toJson(testInfoList);
        String fileName = TimeUtil.getNowTime(TimeUtil.B) + ".txt";
        if (SaveUtil.saveString(str, fileName)) {
            showSnackBar(rootLayout, "数据已导出到: SD卡根目录/MLDA/" + fileName);
        } else {
            showSnackBar(rootLayout, "数据导出失败。");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                outputData();
            } else {
                showSnackBar(rootLayout, "必须允许读写SD卡权限，才能导出数据。");
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SensorService.MyBinder binder = (SensorService.MyBinder) service;
        SensorService sensorService = binder.getService();
        sensorService.getSensorStatus(new SensorService.SensorStatusCallback() {
            @Override
            public void onSensorInit(boolean mag, boolean gyro, boolean gravity, boolean acc) {
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
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
