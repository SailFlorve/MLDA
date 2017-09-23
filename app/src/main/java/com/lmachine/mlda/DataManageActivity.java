package com.lmachine.mlda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lmachine.mlda.adapter.DataRecyclerViewAdapter;
import com.lmachine.mlda.bean.TestInfo;
import com.lmachine.mlda.util.SaveUtil;
import com.lmachine.mlda.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataManageActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DataRecyclerViewAdapter adapter;
    private List<TestInfo> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manage);
        setToolbar(R.id.toolbar, true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    private void initView() {
        adapter = new DataRecyclerViewAdapter(R.layout.data_list_item, dataList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent i = new Intent(DataManageActivity.this, DataDetailActivity.class);
                i.putExtra("id", dataList.get(position).getId());
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void initData() {
        dataList.clear();
        List<TestInfo> infoList = new Select().from(TestInfo.class).queryList();
        dataList.addAll(infoList);
        Collections.reverse(dataList);
        adapter.notifyDataSetChanged();
        showSnackBar("找到" + dataList.size() + "条数据记录。");
        if (dataList.size() == 0) {
            Toast.makeText(this, "没有数据。", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_manage_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.output_data) {
            new AlertDialog.Builder(this)
                    .setTitle("导出数据")
                    .setMessage("数据将导出到: SD卡/MLDA/导出时间.txt。是否继续？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                outputData();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else if (item.getItemId() == R.id.clear_data) {
            new AlertDialog.Builder(this)
                    .setTitle("删除全部数据")
                    .setMessage("删除所有测试数据，删除后不可恢复。是否继续？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Delete().from(TestInfo.class).execute();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else if (item.getItemId() == R.id.data_help) {
            new AlertDialog.Builder(this)
                    .setTitle("帮助")
                    .setMessage(getString(R.string.data_help))
                    .setPositiveButton("很好", null)
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void outputData() {
        if (dataList.isEmpty()) {
            showSnackBar("数据为空！");
            return;
        }
        String str = new Gson().toJson(dataList);
        String fileName = TimeUtil.getNowTime(TimeUtil.E) + ".txt";
        if (SaveUtil.saveString(str, fileName)) {
            showSnackBar("数据已导出为 " + fileName + "。");
        } else {
            showSnackBar("数据导出失败。");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                outputData();
            } else {
                showSnackBar("必须允许读写SD卡权限，才能导出数据。");
            }
        }
    }
}
