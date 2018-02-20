package com.lmachine.mlda;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lmachine.mlda.adapter.SportRecyclerViewAdapter;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.sport.SportInfo;
import com.lmachine.mlda.bean.sport.SportInfo_Table;
import com.lmachine.mlda.constant.SportType;
import com.lmachine.mlda.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SelectActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SportRecyclerViewAdapter listAdapter;

    private TextView testerInfoText;
    private LinearLayout rootLayout;

    private TestInfo testInfo = new TestInfo();
    private List<SportInfo> sportInfoList = new ArrayList<>();

    private SportInfo[] defaultSportInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        setToolbar(R.id.toolbar, true);

        defaultSportInfo = new SportInfo[]{
                new SportInfo(
                        SportType.HIGH_KNEES,
                        getString(R.string.high_knees_des),
                        R.drawable.bg_high_knees,
                        R.drawable.bg_high_knees),
                new SportInfo(
                        SportType.SMALL_JUMP,
                        getString(R.string.small_jump_des),
                        R.drawable.bg_small_jump,
                        R.drawable.bg_small_jump),

                new SportInfo(
                        SportType.JUMPING_JACKS,
                        getString(R.string.jumping_jack_des),
                        R.drawable.bg_jumping_jacks,
                        R.drawable.bg_jumping_jacks),

                new SportInfo(
                        SportType.DEEP_SQUAT,
                        getString(R.string.deep_squat_des),
                        R.drawable.bg_deep_squat,
                        R.drawable.bg_deep_squat),
                new SportInfo(
                        SportType.WALK,
                        getString(R.string.walk_des),
                        R.drawable.bg_walk,
                        R.drawable.bg_walk)
        };

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        testInfo.setSex(intent.getStringExtra("sex"));
        testInfo.setStature(intent.getIntExtra("stature", 0));
        testInfo.setWeight(intent.getIntExtra("weight", 0));
        testInfo.setAge(intent.getIntExtra("age", 0));

        recyclerView = findViewById(R.id.recycler_view);
        testerInfoText = findViewById(R.id.tv_tester_info);
        rootLayout = findViewById(R.id.root_layout);

        listAdapter = new SportRecyclerViewAdapter(R.layout.sport_view, sportInfoList);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        addSport();

        listAdapter.notifyDataSetChanged();
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SelectActivity.this, MonitorActivity.class);
                intent.putExtra("sport", sportInfoList.get(position));

                Pair pair1 = Pair.create(view.findViewById(R.id.tv_sport_view), "sport_text");
                Pair pair2 = Pair.create(view.findViewById(R.id.sport_bg_layout), "sport_bg");

                startActivityForResult(intent, position, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this,
                        pair1, pair2).toBundle()
                );
            }
        });
        listAdapter.setOnItemLongClickListener((BaseQuickAdapter.OnItemLongClickListener) (adapter, view, position) -> {
            if (position >= defaultSportInfo.length) {
                new AlertDialog.Builder(SelectActivity.this).setTitle("删除运动")
                        .setMessage("是否删除？")
                        .setNegativeButton("不删除", null)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSport(sportInfoList.get(position).getId());
                                sportInfoList.clear();
                                addSport();
                            }
                        }).show();
            }
            return true;
        });
        testerInfoText.setText(String.format(
                Locale.getDefault(),
                "性别: %s  年龄: %d  身高: %dcm  体重: %d千克",
                testInfo.getSex(),
                testInfo.getAge(),
                testInfo.getStature(),
                testInfo.getWeight()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_sport) {
            final View view = getLayoutInflater().inflate(R.layout.sport_input_dialog, null);
            new AlertDialog.Builder(this).setTitle("添加运动")
                    .setView(view)
                    .setPositiveButton("确定", (dialog, which) -> {
                        EditText sportName = view.findViewById(R.id.dialog_et_1);
                        EditText sportDes = view.findViewById(R.id.dialog_et_2);
                        String name = sportName.getText().toString();
                        String des = sportDes.getText().toString();
                        sportInfoList.add(new SportInfo(
                                name,
                                TextUtils.isEmpty(des) ? "没有详细说明。" : des,
                                R.drawable.bg_sport,
                                R.drawable.bg_sport));
                        listAdapter.notifyDataSetChanged();
                        saveSport(name, TextUtils.isEmpty(des) ? "没有详细说明。" : des);
                        recyclerView.smoothScrollToPosition(sportInfoList.size() - 1);
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSport() {
        sportInfoList.addAll(Arrays.asList(defaultSportInfo));
        loadSport();
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

    private void saveSport(String name, String des) {
        SportInfo sportInfo = new SportInfo(name, des, R.drawable.bg_sport, R.drawable.bg_sport);
        sportInfo.save();
    }

    private void loadSport() {
        List<SportInfo> addedSport = SQLite.select().from(SportInfo.class).queryList();
        sportInfoList.addAll(addedSport);
        listAdapter.notifyDataSetChanged();
    }

    private void deleteSport(int id) {
        SportInfo info = SQLite.select().from(SportInfo.class).where(SportInfo_Table.id.eq(id)).querySingle();
        if (info == null) {
            Toast.makeText(this, "删除对象不存在。", Toast.LENGTH_SHORT).show();
        } else {
            info.delete();
        }
    }
}
