package com.lmachine.mlda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.TestInfo_Table;
import com.lmachine.mlda.util.DataUtil;
import com.lmachine.mlda.util.SaveUtil;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataDetailActivity extends DataActivity implements View.OnClickListener {

    private TextView sex;
    private TextView age;
    private TextView weight;
    private TextView stature;
    private TextView type;
    private TextView duration;
    private TextView time;
    private TextView isFiltered;
    private TextView inputTimes;
    private TextView remark;
    private TextView rate;
    private TextView ori;
    private TextView oriData;
    private TextView gyro;
    private TextView gyroData;
    private TextView gra;
    private TextView graData;
    private TextView acc;
    private TextView accData;
    private Toolbar toolbar;

    private List<float[]> oriDataList = new ArrayList<>();
    private List<float[]> gyroDataList = new ArrayList<>();
    private List<float[]> graDataList = new ArrayList<>();
    private List<float[]> accDataList = new ArrayList<>();

    private List<float[]> dataList = new ArrayList<>();

    private TestInfo testInfo;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);
        setToolbar(R.id.toolbar, true);
        sex = findViewById(R.id.tv_sex);
        age = findViewById(R.id.tv_age);
        stature = findViewById(R.id.tv_stature);
        weight = findViewById(R.id.tv_weight);
        type = findViewById(R.id.tv_type);
        remark = findViewById(R.id.tv_remark);
        duration = findViewById(R.id.tv_duration);
        time = findViewById(R.id.tv_time);
        isFiltered = findViewById(R.id.tv_isFiltered);
        rate = findViewById(R.id.tv_rate);
        inputTimes = findViewById(R.id.tv_times_input);
        ori = findViewById(R.id.tv_ori);
        oriData = findViewById(R.id.tv_ori_data);
        gyro = findViewById(R.id.tv_gyro);
        gyroData = findViewById(R.id.tv_gyro_data);
        gra = findViewById(R.id.tv_gra);
        graData = findViewById(R.id.tv_gra_data);
        acc = findViewById(R.id.tv_acc);
        accData = findViewById(R.id.tv_acc_data);
        toolbar = findViewById(R.id.toolbar);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initView() {
        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        if (id == 0) {
            Toast.makeText(this, "没有信息。", Toast.LENGTH_SHORT).show();
            return;
        }

        testInfo = new Select().from(TestInfo.class).where(TestInfo_Table.id.eq(id)).querySingle();
        if (testInfo == null) {
            Toast.makeText(this, "没有信息。", Toast.LENGTH_SHORT).show();
            return;
        }

        toolbar.setTitle(testInfo.getType());
        sex.setText("性别: " + testInfo.getSex());
        age.setText("年龄: " + testInfo.getAge() + "岁");
        weight.setText("体重: " + testInfo.getWeight() + "kg");
        stature.setText("身高: " + testInfo.getStature() + "cm");
        type.setText("类型: " + testInfo.getType());
        duration.setText("持续时间: " + testInfo.getDuration() + "秒");
        rate.setText("数据采集频率: " + testInfo.getRate() + "ms");
        remark.setText("备注: " + testInfo.getRemark());
        time.setText("测试时间: " + testInfo.getTime());

        isFiltered.setText("是否被滤波: " + (testInfo.isFiltered() ? "是" : "否"));
        inputTimes.setText("输入的运动次数: " + testInfo.getInputTimes());

        Type type = new TypeToken<List<float[]>>() {
        }.getType();

        oriDataList = new Gson().fromJson(testInfo.getOrientationData(), type);
        ori.setText(getString(R.string.current_dir) + ": " + oriDataList.size() + "条\n"
                + testInfo.getMagSensorVendor() + " " + testInfo.getMagSensorName());
        oriData.setText(testInfo.getOrientationData());

        gyroDataList = new Gson().fromJson(testInfo.getGyroscopeData(), type);
        gyro.setText(getString(R.string.gyro) + ": " + gyroDataList.size() + "条\n"
                + testInfo.getGyroVendor() + " " + testInfo.getGyroName());
        gyroData.setText(testInfo.getGyroscopeData());

        graDataList = new Gson().fromJson(testInfo.getGravityData(), type);
        gra.setText(getString(R.string.gravity_sensor) + ": " + graDataList.size() + "条\n"
                + testInfo.getGravitySensorVendor() + " " + testInfo.getGravitySensorName());
        graData.setText(testInfo.getGravityData());

        accDataList = new Gson().fromJson(testInfo.getAccelerationData(), type);
        acc.setText(getString(R.string.linear_acc_sensor) + ": " + accDataList.size() + "条\n"
                + testInfo.getAccelerationSensorVendor() + " " + testInfo.getAccelerationSensorName());
        accData.setText(testInfo.getAccelerationData());

        oriData.setOnClickListener(this);
        gyroData.setOnClickListener(this);
        graData.setOnClickListener(this);
        accData.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        dataList.clear();
        switch (v.getId()) {
            case R.id.tv_ori_data:
                dataList.addAll(oriDataList);
                break;
            case R.id.tv_gyro_data:
                dataList.addAll(gyroDataList);
                break;
            case R.id.tv_gra_data:
                dataList.addAll(graDataList);
                break;
            case R.id.tv_acc_data:
                dataList.addAll(accDataList);
                break;
        }
        View dialogView = getLayoutInflater().inflate(R.layout.data_dialog, null);
        ListView dialogListView = dialogView.findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, floatListToString(dataList));
        dialogListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .create().show();
    }

    private List<String> floatListToString(List<float[]> dataList) {
        List<String> stringList = new ArrayList<>();
        for (float[] floats : dataList) {
            stringList.add(Arrays.toString(floats));
        }
        return stringList;
    }

    @Override
    protected void output(SaveUtil.SaveCallback callback) {
        DataUtil.output(new Gson().toJson(testInfo, TestInfo.class), this);
    }

    @Override
    protected void delete() {
        DataUtil.delete(id);
        startActivity(new Intent(DataDetailActivity.this, DataManageActivity.class));
        finish();
    }
}
