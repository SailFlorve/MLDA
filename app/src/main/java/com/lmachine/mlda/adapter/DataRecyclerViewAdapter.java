package com.lmachine.mlda.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lmachine.mlda.R;
import com.lmachine.mlda.bean.TestInfo;

import java.util.List;
import java.util.Locale;

/**
 * Created by SailFlorve on 2017/9/3 0003.
 * 数据列表adapter
 */

public class DataRecyclerViewAdapter extends BaseQuickAdapter<TestInfo, BaseViewHolder> {
    public DataRecyclerViewAdapter(@LayoutRes int layoutResId, @Nullable List<TestInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestInfo item) {
        helper.setText(R.id.tv_sport_name, item.getType())
                .setText(R.id.tv_sport_duration, item.getDuration() + "秒")
                .setText(R.id.tv_sport_time, item.getTime())
                .setText(R.id.tv_sport_tester_info,
                        String.format(Locale.getDefault(),
                                "%s · %d岁 · %dcm · %dkg",
                                item.getSex(),
                                item.getAge(),
                                item.getStature(),
                                item.getWeight()));
    }
}
