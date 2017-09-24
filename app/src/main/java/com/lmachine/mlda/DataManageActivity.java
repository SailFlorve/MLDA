package com.lmachine.mlda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.lmachine.mlda.util.DataUtil;
import com.lmachine.mlda.util.SaveUtil;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataManageActivity extends DataActivity {

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
        if (item.getItemId() == R.id.data_help) {
            new AlertDialog.Builder(this)
                    .setTitle("帮助")
                    .setMessage(getString(R.string.data_help))
                    .setPositiveButton("很好", null)
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void output(SaveUtil.SaveCallback callback) {
        if (dataList.isEmpty()) {
            showSnackBar("数据为空！");
            return;
        }
        DataUtil.output(new Gson().toJson(dataList), this);
    }

    @Override
    protected void delete() {
        DataUtil.deleteAll();
        finish();
    }
}
