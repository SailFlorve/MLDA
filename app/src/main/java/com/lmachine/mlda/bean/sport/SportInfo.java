package com.lmachine.mlda.bean.sport;

import com.lmachine.mlda.bean.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by SailFlorve on 2017/9/23 0023.
 * 运动实体类的基类
 */

@Table(database = AppDatabase.class)
public class SportInfo extends BaseModel implements Serializable {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String name;
    @Column
    private String des;
    @Column
    private int picId;
    @Column
    private int gifId;
    private boolean isFinished;

    public SportInfo() {

    }

    public SportInfo(String name, String des, int picId, int gifId) {
        this.name = name;
        this.des = des;
        this.picId = picId;
        this.gifId = gifId;
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
