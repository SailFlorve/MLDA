package com.lmachine.mlda;

import android.animation.Animator;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.fragment.BluetoothSensorFragment;
import com.lmachine.mlda.fragment.BuiltinSensorFragment;
import com.lmachine.mlda.service.SensorService;
import com.lmachine.mlda.util.BluetoothUtil;
import com.lmachine.mlda.util.SPUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.lmachine.mlda.util.SensorInfoMgr.TYPE_BLUETOOTH_DEVICE_SENSOR;
import static com.lmachine.mlda.util.SensorInfoMgr.TYPE_BUILT_IN_SENSOR;


public class MainActivity extends BaseActivity implements ServiceConnection {

    private TextView randomInputText;
    private RadioGroup radioGroup;
    private TextInputLayout ageText;
    private TextInputLayout statureText;
    private TextInputLayout weightText;
    private FloatingActionButton fab;
    private RadioButton radioButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragments;

    private TextInputLayout[] textInputLayouts;

    private SensorService sensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.id.toolbar, false);
        radioGroup = findViewById(R.id.radio_group);
        randomInputText = findViewById(R.id.tv_random);
        ageText = findViewById(R.id.til_age);
        statureText = findViewById(R.id.til_stature);
        weightText = findViewById(R.id.til_weight);
        tabLayout = findViewById(R.id.tab_layout);
        radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        viewPager = findViewById(R.id.view_pager);
        fab = findViewById(R.id.fab);

        textInputLayouts = new TextInputLayout[]{ageText, statureText, weightText};

        showAnimation();
        fab.setOnClickListener(v -> {
            int pos = tabLayout.getSelectedTabPosition();

            if (pos == 0) {
                if (sensorService.isBuiltinSensorEmpty()) {
                    showSnackBar("内置传感器初始化未成功。");
                } else {
                    if (check()) {
                        sensorService.setCurrentSensorType(TYPE_BUILT_IN_SENSOR);
                        startMonitorActivity();
                    }
                }
            } else if (pos == 1) {
                if (check()) {
                    showProgressDialog("正在连接蓝牙...");
                    sensorService.connectBluetoothDevice(new BluetoothUtil.BluetoothConnectCallback() {
                        @Override
                        public void onConnectSuccess(BluetoothSocket socket) {
                            closeProgressDialog();
                            sensorService.setCurrentSensorType(TYPE_BLUETOOTH_DEVICE_SENSOR);
                            startMonitorActivity();
                        }

                        @Override
                        public void onConnectFailed(String msg) {
                            closeProgressDialog();
                            showSnackBar(msg);
                        }
                    });
                }
            }
        });

        randomInputText.setOnClickListener(v -> randomInput());

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
        } else if (item.getItemId() == R.id.manage_data) {
            startActivity(new Intent(this, DataManageActivity.class));
        } else if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAnimation() {
        LinearLayout linearLayout = findViewById(R.id.card_layout);
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                linearLayout.removeOnLayoutChangeListener(this);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Animator mCircularReveal = ViewAnimationUtils.createCircularReveal(
                            linearLayout, 0, 0, 0, 2500f);
                    mCircularReveal.setDuration(1000).start();
                } else {
                    linearLayout.setAlpha(0);
                    linearLayout.setTranslationY(80);
                    linearLayout.animate().alpha(1).translationY(0).setDuration(750).start();
                }
            }
        });
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
    private boolean check() {
        clearError();
        radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton == null) {
            showSnackBar("请选择性别。");
            return false;
        }
        for (TextInputLayout textInputLayout : textInputLayouts) {
            EditText editText = textInputLayout.getEditText();
            if (editText != null) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    textInputLayout.setError("此项不能为空。");
                    return false;
                }
            } else {
                return false;
            }
        }

        boolean checkSensor = SPUtil.load(this).getBoolean("check_sensor", true);


        int age = Integer.parseInt(ageText.getEditText().getText().toString());
        int stature = Integer.parseInt(statureText.getEditText().getText().toString());
        int weight = Integer.parseInt(weightText.getEditText().getText().toString());

        if ((age < 14 || age > 80) || (stature < 130 || stature > 200) || (weight < 30 || weight > 150)) {
            showSnackBar("请如实填写信息，不要胡编乱造。");
            return false;
        }

        return true;
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
        int weight = r.nextInt(41) + 41;
        weightText.getEditText().setText(String.valueOf(weight));
    }

    private void setSensorType() {

    }

    @SuppressWarnings("ConstantConditions")
    private void startMonitorActivity() {
        String checkText = radioButton.getText().toString();
        int age = Integer.parseInt(ageText.getEditText().getText().toString());
        int stature = Integer.parseInt(statureText.getEditText().getText().toString());
        int weight = Integer.parseInt(weightText.getEditText().getText().toString());

        Intent intent = new Intent(this, MonitorActivity.class);
        TestInfo testInfo = new TestInfo();
        testInfo.setSex(checkText.substring(0, 1));
        testInfo.setAge(age);
        testInfo.setStature(stature);
        testInfo.setWeight(weight);
        intent.putExtra("test_info", testInfo);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data == null) return;
                if (resultCode != RESULT_OK) return;

                TestInfo info = (TestInfo) data.getSerializableExtra("test_info");
                boolean saveResult = info.save();
                showSnackBar(saveResult ? "数据已保存。" : "数据保存失败。");
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SensorService.MyBinder binder = (SensorService.MyBinder) service;
        sensorService = binder.getService();

        fragments = new ArrayList<>();
        fragments.add(BuiltinSensorFragment.newInstance(sensorService));
        fragments.add(BluetoothSensorFragment.newInstance(sensorService));

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "内置传感器";
                } else {
                    return "外部传感器";
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
