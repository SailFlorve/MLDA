package com.lmachine.mlda.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by SailFlorve on 2017/8/28 0028.
 * 测试信息实体类
 */

@Table(database = AppDatabase.class)
public class TestInfo extends BaseModel implements Serializable {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String time;
    @Column
    private String sex;
    @Column
    private int stature;
    @Column
    private int weight;
    @Column
    private int age;
    @Column
    private int duration;
    @Column
    private String type;
    @Column
    private String sensorData;
    @Column(defaultValue = "0")
    private boolean isFiltered;
    @Column(defaultValue = "0")
    private int inputTimes;
    @Column(defaultValue = "40")
    private int rate;
    @Column
    private String remark;

    public TestInfo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getStature() {
        return stature;
    }

    public void setStature(int stature) {
        this.stature = stature;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSensorData() {
        return sensorData;
    }

    public void setSensorData(String sensorData) {
        this.sensorData = sensorData;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public int getInputTimes() {
        return inputTimes;
    }

    public void setInputTimes(int inputTimes) {
        this.inputTimes = inputTimes;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
