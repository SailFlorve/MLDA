package com.lmachine.mlda.application;

import android.app.Application;

import com.lmachine.mlda.bean.AppDatabase;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 自定义application类
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        FlowManager.init(FlowConfig.builder(this)
//                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase.class)
//                        .databaseName(AppDatabase.NAME)
//                        .build())
//                .build());
        FlowManager.init(this);
    }
}
