package com.lmachine.mlda.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SailFlorve on 2017/10/15 0015.
 * 轮播效果的TextView
 */

public class CarouselTextView extends android.support.v7.widget.AppCompatTextView {
    private String TAG = "CarouselTextView";
    private final int VIEW_FADE_OUT = 0;
    private final int VIEW_FADE_IN = 1;
    private final int CHANGE_TEXT = 2;

    private int fadeOutDuration = 500;
    private int fadeInDuration = 500;
    private int textShowedTime = 3000;

    private String[] stringArray = new String[]{};

    private Timer timer = null;
    private TimerTask timerTask = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case VIEW_FADE_OUT:
                    animate().alpha(0).setDuration(fadeOutDuration);
                    break;
                case VIEW_FADE_IN:
                    animate().alpha(1).setDuration(fadeInDuration);
                    break;
                case CHANGE_TEXT:
                    //Log.d("", "handleMessage: " + msg.getData().getString("text", ""));
                    setText(msg.getData().getString("text", "空文字。"));
                    break;
                default:
            }
            return true;
        }
    });

    public CarouselTextView(Context context) {
        super(context);
    }

    public CarouselTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CarouselTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearTimer();
    }

    public void setTextArray(String[] array) {
        stringArray = array;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        clearTimer();
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    private Message getMsg(int what) {
        return getMsg(what, null, null);
    }

    private Message getMsg(int what, String key, String data) {
        Message m = handler.obtainMessage();
        m.what = what;
        if (key != null && data != null) {
            Bundle b = new Bundle();
            b.putString(key, data);
            m.setData(b);
        }
        return m;
    }

    private void clearTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new UpdateTextTimerTask();
    }

    private class UpdateTextTimerTask extends TimerTask {

        @Override
        public void run() {
            String[] currentArray = stringArray;
            if (currentArray.length == 0) return;
            if (currentArray.length == 1) {
                handler.sendMessage(getMsg(CHANGE_TEXT, "text", currentArray[0]));
                return;
            }
            for (String s : stringArray) {
                try {
                    handler.sendMessage(getMsg(VIEW_FADE_OUT));
                    Thread.sleep(fadeOutDuration);

                    handler.sendMessage(getMsg(CHANGE_TEXT, "text", s));

                    handler.sendMessage(getMsg(VIEW_FADE_IN));
                    Thread.sleep(fadeInDuration);
                    Thread.sleep(textShowedTime);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
