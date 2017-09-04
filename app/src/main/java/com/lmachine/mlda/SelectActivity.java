package com.lmachine.mlda;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.constant.SportType;
import com.lmachine.mlda.util.TimeUtil;
import com.lmachine.mlda.view.SportView;

import java.util.Locale;

public class SelectActivity extends BaseActivity {

    private final int HIGH_KNEES = 0;
    private final int SMALL_JUMP = 1;
    private final int JUMPING_JACKS = 2;

    private SportView highKnees;
    private SportView smallJump;
    private SportView jumpingJacks;
    private TextView testerInfoText;
    private LinearLayout rootLayout;

    private TestInfo testInfo = new TestInfo();

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

        highKnees = (SportView) findViewById(R.id.sport_view_high_knees);
        smallJump = (SportView) findViewById(R.id.sport_view_small_jump);
        jumpingJacks = (SportView) findViewById(R.id.sport_view_jumping_jacks);
        testerInfoText = (TextView) findViewById(R.id.tv_tester_info);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        highKnees.setImage(R.drawable.bg_high_knees);
        highKnees.setText(SportType.HIGH_KNEES);
        smallJump.setImage(R.drawable.bg_small_jump);
        smallJump.setText(SportType.SMALL_JUMP);
        jumpingJacks.setImage(R.drawable.bg_jumpling_jacks);
        jumpingJacks.setText(SportType.JUMPING_JACKS);

        testerInfoText.setText(String.format(
                Locale.getDefault(),
                "性别: %s 年龄: %d岁 身高: %dcm 体重: %d千克",
                testInfo.getSex(),
                testInfo.getAge(),
                testInfo.getStature(),
                testInfo.getWeight()));

        highKnees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                Intent intent = new Intent(SelectActivity.this, MonitorActivity.class);
                intent.putExtra("sport", SportType.HIGH_KNEES);
                startActivityForResult(intent, HIGH_KNEES, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, highKnees.getTextView(), "sport_text").toBundle());
            }
        });

        smallJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, MonitorActivity.class);
                intent.putExtra("sport", SportType.SMALL_JUMP);
                startActivityForResult(intent, SMALL_JUMP, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, smallJump.getTextView(), "sport_text").toBundle());
            }
        });

        jumpingJacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, MonitorActivity.class);
                intent.putExtra("sport", SportType.JUMPING_JACKS);
                startActivityForResult(intent, JUMPING_JACKS, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, jumpingJacks.getTextView(), "sport_text").toBundle());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + resultCode + data);
        if (data == null) return;
        TestInfo info = new TestInfo(testInfo);
        switch (requestCode) {
            case HIGH_KNEES:
                info.setType(SportType.HIGH_KNEES);
                highKnees.setFinish(true);
                break;
            case JUMPING_JACKS:
                info.setType(SportType.JUMPING_JACKS);
                jumpingJacks.setFinish(true);
                break;
            case SMALL_JUMP:
                info.setType(SportType.SMALL_JUMP);
                smallJump.setFinish(true);
                break;
        }
        info.setAccelerationData(data.getStringExtra("accData"));
        info.setGravityData(data.getStringExtra("graData"));
        info.setGyroscopeData(data.getStringExtra("gyroData"));
        info.setOrientationData(data.getStringExtra("oriData"));
        info.setTime(TimeUtil.getNowTime(TimeUtil.A));
        info.setDuration(data.getIntExtra("duration", 0));
        info.save();
        showSnackBar("数据已保存。");
        if (highKnees.getFinish() && jumpingJacks.getFinish() && smallJump.getFinish()) {
            new AlertDialog.Builder(this)
                    .setTitle("测试全部完成")
                    .setMessage("恭喜你完成了全部测试！")
                    .setPositiveButton("确定", null)
                    .setNegativeButton("返回主界面", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }
    }
}
