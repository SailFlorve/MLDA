package com.lmachine.mlda.bean;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 数据库配置信息实体类
 */

@Database(version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "TestData";
    public static final int VERSION = 9;

    @Migration(version = 9, database = AppDatabase.class)
    public static class Migration9 extends AlterTableMigration<TestInfo> {

        public Migration9(Class<TestInfo> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "sensorData");
        }
    }

}
