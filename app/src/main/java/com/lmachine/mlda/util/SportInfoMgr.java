package com.lmachine.mlda.util;

import android.content.Context;

import com.lmachine.mlda.R;
import com.lmachine.mlda.application.MyApplication;
import com.lmachine.mlda.bean.sport.SportInfo;

public class SportInfoMgr {
    public static SportInfo getInfo(String name) {
        Context context = MyApplication.getContext();
        String[] sportArr = context.getResources().getStringArray(R.array.sport_array);
        if (name.equals(sportArr[0])) {
            return new SportInfo(name, context.getString(R.string.high_knees_des), R.drawable.bg_high_knees);
        }
        if (name.equals(sportArr[1])) {
            return new SportInfo(name, context.getString(R.string.jumping_jack_des), R.drawable.bg_jumping_jacks);
        }
        if (name.equals(sportArr[2])) {
            return new SportInfo(name, context.getString(R.string.small_jump_des), R.drawable.bg_small_jump);
        }
        if (name.equals(sportArr[3])) {
            return new SportInfo(name, context.getString(R.string.deep_squat_des), R.drawable.bg_deep_squat);
        }
        if (name.equals(sportArr[4])) {
            return new SportInfo(name, context.getString(R.string.walk_des), R.drawable.bg_walk);
        }
        return new SportInfo("未知", "无描述", R.drawable.bg_walk);
    }
}
