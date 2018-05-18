package com.lmachine.mlda.bean.sport;

import java.io.Serializable;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 运动实体类的基类
 */

public class SportInfo implements Serializable {
    private int id;
    private String name;
    private String des;
    private int picId;
    private int gifId;
    private boolean isFinished;

    public SportInfo() {

    }

    public SportInfo(String name, String des, int picId) {
        this.name = name;
        this.des = des;
        this.picId = picId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public int getGifId() {
        return gifId;
    }

    public void setGifId(int gifId) {
        this.gifId = gifId;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
