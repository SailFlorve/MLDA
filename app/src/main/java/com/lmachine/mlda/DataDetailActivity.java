package com.lmachine.mlda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.TestInfo_Table;
import com.lmachine.mlda.util.SaveUtil;
import com.lmachine.mlda.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView sex;
    private TextView age;
    private TextView weight;
    private TextView stature;
    private TextView type;
    private TextView duration;
    private TextView time;
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
        sex = (TextView) findViewById(R.id.tv_sex);
        age = (TextView) findViewById(R.id.tv_age);
        stature = (TextView) findViewById(R.id.tv_stature);
        weight = (TextView) findViewById(R.id.tv_weight);
        type = (TextView) findViewById(R.id.tv_type);
        duration = (TextView) findViewById(R.id.tv_duration);
        time = (TextView) findViewById(R.id.tv_time);
        ori = (TextView) findViewById(R.id.tv_ori);
        oriData = (TextView) findViewById(R.id.tv_ori_data);
        gyro = (TextView) findViewById(R.id.tv_gyro);
        gyroData = (TextView) findViewById(R.id.tv_gyro_data);
        gra = (TextView) findViewById(R.id.tv_gra);
        graData = (TextView) findViewById(R.id.tv_gra_data);
        acc = (TextView) findViewById(R.id.tv_acc);
        accData = (TextView) findViewById(R.id.tv_acc_data);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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
        time.setText("测试时间: " + testInfo.getTime());

        Type type = new TypeToken<List<float[]>>() {
        }.getType();

        oriDataList = new Gson().fromJson(testInfo.getOrientationData(), type);
        ori.setText("方向: " + oriDataList.size() + "条");
        oriData.setText(testInfo.getOrientationData());

        gyroDataList = new Gson().fromJson(testInfo.getGyroscopeData(), type);
        gyro.setText("陀螺仪: " + gyroDataList.size() + "条");
        gyroData.setText(testInfo.getGyroscopeData());

        graDataList = new Gson().fromJson(testInfo.getGravityData(), type);
        gra.setText("重力传感器: " + graDataList.size() + "条");
        graData.setText(testInfo.getGravityData());

        accDataList = new Gson().fromJson(testInfo.getAccelerationData(), type);
        acc.setText("线性加速度传感器: " + accDataList.size() + "条");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_data) {
            new AlertDialog.Builder(this)
                    .setTitle("删除此条数据")
                    .setMessage("删除此数据，删除后不可恢复。是否继续？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Delete().from(TestInfo.class).where(TestInfo_Table.id.eq(id)).execute();
                            startActivity(new Intent(DataDetailActivity.this, DataManageActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else if (item.getItemId() == R.id.output_data) {
            new AlertDialog.Builder(this)
                    .setTitle("导出此条数据")
                    .setMessage("数据将导出到: SD卡/MLDA/导出时间.txt。是否继续？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                output();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
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
        ListView dialogListView = (ListView) dialogView.findViewById(R.id.list_view);
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

    private void output() {
        String str = new Gson().toJson(testInfo, TestInfo.class);
        String fileName = TimeUtil.getNowTime(TimeUtil.B) + ".txt";
        if (SaveUtil.saveString(str, fileName)) {
            showSnackBar("数据已导出为 " + fileName + "。");
        } else {
            showSnackBar("数据导出失败。");
        }
    }
}
