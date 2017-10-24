package com.lmachine.mlda;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar(R.id.toolbar, true);
        if (savedInstanceState == null) {
            SettingFragment settingFragment = new SettingFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_content, settingFragment)
                    .commit();
        }
    }

    public static class SettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
