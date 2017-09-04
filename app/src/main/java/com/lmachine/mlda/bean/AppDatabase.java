package com.lmachine.mlda.bean;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 数据库配置信息实体类
 */

@Database(version = AppDatabase.VERSION)
public class AppDatabase {
    public static final int VERSION = 2;
}
