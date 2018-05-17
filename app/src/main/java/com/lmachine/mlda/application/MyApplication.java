package com.lmachine.mlda.application;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 自定义application类
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        FlowManager.init(FlowConfig.builder(this)
//                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase.class)
//                        .databaseName(AppDatabase.NAME)
//                        .build())
//                .build());
        FlowManager.init(this);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
