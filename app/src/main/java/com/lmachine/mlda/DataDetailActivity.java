package com.lmachine.mlda;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lmachine.mlda.bean.SensorInfo;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.TestInfo_Table;
import com.lmachine.mlda.util.DataUtil;
import com.lmachine.mlda.util.SaveUtil;
import com.lmachine.mlda.util.TimeUtil;
import com.lmachine.mlda.util.Utility;
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

    private LinearLayout dataLayout;

    private Toolbar toolbar;


    private TestInfo testInfo;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);
        setToolbar(R.id.toolbar, true);
        toolbar = findViewById(R.id.toolbar);
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
        dataLayout = findViewById(R.id.data_layout);
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

        Type floatListType = new TypeToken<List<float[]>>() {
        }.getType();

        Type sensorInfoType = new TypeToken<List<SensorInfo>>() {
        }.getType();

        String sensorDataJson = testInfo.getSensorData();
        if (sensorDataJson == null || sensorDataJson.length() == 0) {
            TextView des = new TextView(this);
            des.setText("不兼容老版本数据。");
            dataLayout.addView(des);
            return;
        }
        List<SensorInfo> sensorInfoList = new Gson().fromJson(sensorDataJson, sensorInfoType);

        for (SensorInfo sensorInfo : sensorInfoList) {
            TextView des = new TextView(this);
            des.setText(sensorInfo.getName() + ": " + sensorInfo.getData().size() + "条");
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dataLayout.addView(des, params1);

            TextView data = new TextView(this);
            data.setText(floatListToString(sensorInfo.getData()));
            data.setBackgroundColor(Color.parseColor("#ededed"));
            data.setEllipsize(TextUtils.TruncateAt.END);
            data.setMaxLines(4);
            int padding = Utility.dipToPx(6);
            data.setPadding(padding, padding, padding, padding);
            data.setTextSize(12);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = Utility.dipToPx(5);
            params2.setMargins(0, margin, 0, margin);
            dataLayout.addView(data, params2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
//        View dialogView = getLayoutInflater().inflate(R.layout.data_dialog, null);
//        ListView dialogListView = dialogView.findViewById(R.id.list_view);
//        ArrayAdapter<String> arrayAdapter;
//        dialogListView.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
//        new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .create().show();
    }

    private String floatListToString(List<float[]> dataList) {
        StringBuilder sb = new StringBuilder();
        for (float[] floats : dataList) {
            sb.append(Arrays.toString(floats)).append(" ");
        }
        return sb.toString().substring(0, 300);
    }

    private List<String> floatListToStringList(List<float[]> dataList) {
        List<String> stringList = new ArrayList<>();
        for (float[] floats : dataList) {
            stringList.add(Arrays.toString(floats));
        }
        return stringList;
    }

    @Override
    protected void output(SaveUtil.SaveCallback callback) {
        String time = TimeUtil.getNowTime(TimeUtil.E);
        String sportDes = testInfo.getType() + testInfo.getInputTimes() + "次";
        DataUtil.output(new Gson().toJson(testInfo, TestInfo.class), time + " " + sportDes + ".json", this);
    }

    @Override
    protected void delete() {
        DataUtil.delete(id);
        startActivity(new Intent(DataDetailActivity.this, DataManageActivity.class));
        finish();
    }
}
