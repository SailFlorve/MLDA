package com.lmachine.mlda.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lmachine.mlda.R;
import com.lmachine.mlda.bean.sport.SportInfo;

import java.util.List;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 运动
 */

public class SportRecyclerViewAdapter extends BaseQuickAdapter<SportInfo, BaseViewHolder> {

    public SportRecyclerViewAdapter(@LayoutRes int layoutResId, @Nullable List<SportInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SportInfo item) {
        helper.setImageResource(R.id.iv_sport_view, item.getPicId())
                .setText(R.id.tv_sport_view, item.getName())
                .setVisible(R.id.iv_finish, item.isFinished());
    }

}
