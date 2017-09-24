package com.lmachine.mlda.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.lmachine.mlda.BaseActivity;
import com.lmachine.mlda.DataDetailActivity;
import com.lmachine.mlda.DataManageActivity;
import com.lmachine.mlda.bean.Status;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.TestInfo_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 数据管理类
 */

public class DataUtil {

    public interface UploadCallback {
        void onUploadSuccess(String msg);

        void onUploadFailed(String msg);
    }

    public static void output(final String str, SaveUtil.SaveCallback callback) {
        String fileName = TimeUtil.getNowTime(TimeUtil.E) + ".txt";
        SaveUtil.saveString(str, fileName, callback);
    }

    public static void delete(final int id) {
        new Delete().from(TestInfo.class).where(TestInfo_Table.id.eq(id)).execute();
    }

    public static void deleteAll() {
        new Delete().from(TestInfo.class).execute();
    }

    public static void upload(File file, final UploadCallback callback) {
        //...\
        final Handler mHandler = new Handler(Looper.getMainLooper());
        HttpUtil.load("http://skylance.xin/UploadFileServlet")
                .addFile("upfile", file.getName(), file)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onUploadFailed("网络连接失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.body().string();
                        Log.d("upload", "onResponse: " + responseStr);
                        final Status status = new Gson().fromJson(responseStr, Status.class);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (status.getStatus()) {
                                    callback.onUploadSuccess(status.getMessage());
                                } else {
                                    callback.onUploadFailed(status.getMessage());
                                }
                            }
                        });
                    }
                });
    }
}
