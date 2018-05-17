package com.lmachine.mlda.util;

import com.lmachine.mlda.application.MyApplication;

/**
 * Created by SailFlorve on 2017/9/4 0004.
 * 工具类
 */

public class Utility {
    public static int dipToPx(int dp) {
        float density = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static int sp2px(int spValue) {
        final float fontScale = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
