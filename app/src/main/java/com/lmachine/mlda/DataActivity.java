package com.lmachine.mlda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.lmachine.mlda.util.DataUtil;
import com.lmachine.mlda.util.SaveUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 数据管理基类
 */

public abstract class DataActivity extends BaseActivity implements SaveUtil.SaveCallback {

    private boolean isUpload;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.output_data) {
            new AlertDialog.Builder(this)
                    .setItems(new String[]{"导出至本地", "导出并上传"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isUpload = which != 0;
                            if (checkPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                output(DataActivity.this);
                            }
                        }
                    }).create().show();
        } else if (item.getItemId() == R.id.delete_data) {
            new AlertDialog.Builder(this)
                    .setTitle("删除数据")
                    .setMessage("删除后不可恢复。是否删除？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete();
                        }
                    })
                    .setNegativeButton("不删除", null)
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                output(this);
            } else {
                showSnackBar("必须允许读写SD卡权限，才能导出数据。");
            }
        }
    }

    @Override
    public void onSaveSuccess(File file) {
        showSnackBar("数据已经导出到SD卡根目录下的MLDA文件夹。");
        if (isUpload) {
            showProgressDialog("正在上传");
            DataUtil.upload(file, new DataUtil.UploadCallback() {
                @Override
                public void onUploadSuccess(String msg) {
                    closeProgressDialog();
                    showDialog("上传成功。\n" + msg, "知道了");
                }

                @Override
                public void onUploadFailed(String msg) {
                    closeProgressDialog();
                    showDialog("上传失败。\n" + msg, "知道了");
                }
            });
        }
    }

    @Override
    public void onSaveFailed(String msg) {
        showSnackBar("数据导出失败。原因是: " + msg);
    }

    protected abstract void output(SaveUtil.SaveCallback callback);

    protected abstract void delete();
}
