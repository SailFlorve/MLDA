package com.lmachine.mlda;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.bean.TestInfo_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

public class DataPresentActivity extends BaseActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private int currentStatus = 0;

    private int TYPE_CHOOSE = 0;
    private int TIME_CHOOSE = 1;
    private int SENSOR_CHOOSE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_present);
        setToolbar(R.id.toolbar, true);
        listView = (ListView) findViewById(R.id.list_view);
        List<TestInfo> testInfoList = new Select(TestInfo_Table.type).distinct().from(TestInfo.class).queryList();
        for (TestInfo testInfo : testInfoList) {
            Toast.makeText(this, testInfo.getType(), Toast.LENGTH_SHORT).show();
        }
    }
}
