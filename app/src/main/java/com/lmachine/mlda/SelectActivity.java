package com.lmachine.mlda;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lmachine.mlda.adapter.SportRecyclerViewAdapter;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.sport.SportInfo;
import com.lmachine.mlda.constant.SportType;
import com.lmachine.mlda.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectActivity extends BaseActivity {

    private final int HIGH_KNEES = 0;
    private final int SMALL_JUMP = 1;
    private final int JUMPING_JACKS = 2;

    private RecyclerView recyclerView;
    private SportRecyclerViewAdapter listAdapter;

    private TextView testerInfoText;
    private LinearLayout rootLayout;

    private TestInfo testInfo = new TestInfo();
    private List<SportInfo> sportInfoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        setToolbar(R.id.toolbar, true);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        testInfo.setSex(intent.getStringExtra("sex"));
        testInfo.setStature(intent.getIntExtra("stature", 0));
        testInfo.setWeight(intent.getIntExtra("weight", 0));
        testInfo.setAge(intent.getIntExtra("age", 0));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        testerInfoText = (TextView) findViewById(R.id.tv_tester_info);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        listAdapter = new SportRecyclerViewAdapter(R.layout.sport_view, sportInfoList);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        addSport();

        listAdapter.notifyDataSetChanged();
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SelectActivity.this, MonitorActivity.class);
                intent.putExtra("sport", sportInfoList.get(position));

                startActivityForResult(intent, position, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, view.findViewById(R.id.tv_sport_view), "sport_text").toBundle());
            }
        });

        testerInfoText.setText(String.format(
                Locale.getDefault(),
                "性别: %s 年龄: %d岁 身高: %dcm 体重: %d千克",
                testInfo.getSex(),
                testInfo.getAge(),
                testInfo.getStature(),
                testInfo.getWeight()));
    }

    private void addSport() {
        sportInfoList.add(new SportInfo(
                SportType.HIGH_KNEES,
                getString(R.string.high_knees_des),
                R.drawable.bg_high_knees,
                R.drawable.high_knees));

        sportInfoList.add(new SportInfo(
                SportType.SMALL_JUMP,
                getString(R.string.small_jump_des),
                R.drawable.bg_small_jump,
                R.drawable.small_jump));

        sportInfoList.add(new SportInfo(
                SportType.JUMPING_JACKS,
                getString(R.string.jumping_jack_des),
                R.drawable.bg_jumpling_jacks,
                R.drawable.jumping_jacks));

        sportInfoList.add(new SportInfo(
                SportType.DEEP_SQUAT,
                "深蹲。",
                R.drawable.bg_deep_squat,
                R.drawable.deep_squad));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + resultCode + data);

        if (data == null) return;

        sportInfoList.get(requestCode).setFinished(true);
        listAdapter.notifyDataSetChanged();

        TestInfo info = (TestInfo) data.getSerializableExtra("test_info");
        info.setType(sportInfoList.get(requestCode).getName());
        info.setTime(TimeUtil.getNowTime(TimeUtil.A));
        info.setSex(testInfo.getSex());
        info.setStature(testInfo.getStature());
        info.setWeight(testInfo.getWeight());
        info.setAge(testInfo.getAge());
        info.save();
        showSnackBar("数据已保存。");
    }
}
