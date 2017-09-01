package com.lmachine.mlda;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SailFlorve on 2017/8/28 0028.
 * Activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getSimpleName();
    protected View.OnClickListener emptyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolbar(@IdRes int toolbarId, boolean haveBackButton) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(haveBackButton);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showSnackBar(View root, String content) {
        Snackbar.make(root, content, Snackbar.LENGTH_SHORT).setAction("知道了", emptyClickListener).show();
    }

    protected void showSnackBar(View root, String content, String action, View.OnClickListener listener) {
        Snackbar.make(root, content, Snackbar.LENGTH_LONG).setAction(action, listener).show();
    }

    /**
     * 检查权限，未被允许则申请，并返回false否则返回true
     */
    protected boolean checkPermission(int requestCode, @NonNull String permission) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkPermissions(int requestCode, @NonNull String... permissions) {
        List<String> deniedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permission);
            }
        }
        if (deniedPermissionList.isEmpty()) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    (String[]) deniedPermissionList.toArray(), requestCode);
            return false;
        }
    }
}
