package com.lmachine.mlda.bean.sport;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 运动实体类的基类
 */

public class SportInfo implements Serializable {
    private String name;
    private String des;
    private int picId;
    private int gifId;
    private boolean isFinished;

    public SportInfo(String name, String des, int picId, int gifId) {
        this.name = name;
        this.des = des;
        this.picId = picId;
        this.gifId = gifId;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public int getPicId() {
        return picId;
    }

    public int getGifId() {
        return gifId;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
