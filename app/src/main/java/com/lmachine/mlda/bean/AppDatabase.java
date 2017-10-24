package com.lmachine.mlda.bean;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 数据库配置信息实体类
 */

@Database(version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "TestData";
    public static final int VERSION = 5;

    public class MyMigration extends AlterTableMigration<TestInfo> {

        public MyMigration(Class<TestInfo> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "remark");
        }
    }
}
