package com.lmachine.mlda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    private ProgressDialog progressDialog = null;

    protected View.OnClickListener emptyClickListener = v -> {
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        closeProgressDialog();
        super.onDestroy();
    }

    protected void setToolbar(@IdRes int toolbarId, boolean haveBackButton) {
        Toolbar toolbar = findViewById(toolbarId);
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

    public void showSnackBar(String content) {
        View root = findViewById(R.id.root_layout);
        Snackbar.make(root, content, Snackbar.LENGTH_LONG).setAction("知道了", emptyClickListener).show();
    }

    public void showSnackBar(String content, String action, View.OnClickListener listener) {
        View root = findViewById(R.id.root_layout);
        Snackbar.make(root, content, Snackbar.LENGTH_LONG).setAction(action, listener).show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showProgressDialog(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        } else {
            progressDialog.dismiss();
        }
        progressDialog.setMessage(text);

        progressDialog.show();
    }

    public void showDialog(String message, String positive) {
        showDialog(null, message, positive, null, null, null);
    }

    public void showDialog(String title, String message, String positive, String negative,
                           DialogInterface.OnClickListener positiveListener,
                           DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positive)) {
            builder.setPositiveButton(positive, positiveListener);
        }
        if (!TextUtils.isEmpty(negative)) {
            builder.setNegativeButton(negative, negativeListener);
        }
        builder.create().show();
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
