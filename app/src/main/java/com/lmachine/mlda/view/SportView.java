package com.lmachine.mlda.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lmachine.mlda.R;

/**
 * Created by SailFlorve on 2017/8/29 0029.
 * 运动View
 */

public class SportView extends CardView {

    private ImageView imageView;
    private TextView textView;
    private ImageView finishView;
    private Context mContext;

    public SportView(Context context) {
        super(context);
        mContext = context;
    }

    public SportView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sport_view, this);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_sport_view);
        textView = (TextView) findViewById(R.id.tv_sport_view);
        finishView = (ImageView) findViewById(R.id.iv_finish);
    }

    public void setImage(@DrawableRes int id) {
        Glide.with(mContext).load(id).into(imageView);
        //imageView.setImageResource(id);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setFinish(boolean finish) {
        finishView.setVisibility(finish ? VISIBLE : GONE);
    }

    public boolean getFinish() {
        return finishView.getVisibility() == VISIBLE;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }
}
