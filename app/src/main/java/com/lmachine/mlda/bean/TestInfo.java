package com.lmachine.mlda.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by SailFlorve on 2017/8/28 0028.
 * 测试信息实体类
 */

@Table(database = AppDatabase.class)
public class TestInfo extends BaseModel {
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
    private String orientationData;
    @Column
    private String gyroscopeData;
    @Column
    private String gravityData;
    @Column
    private String accelerationData;

    public TestInfo() {

    }

    public TestInfo(TestInfo testInfo) {
        this.time = testInfo.getTime();
        this.sex = testInfo.getSex();
        this.stature = testInfo.getStature();
        this.weight = testInfo.getWeight();
        this.age = testInfo.getAge();
        this.type = testInfo.getType();
        this.orientationData = testInfo.getOrientationData();
        this.gyroscopeData = testInfo.getGyroscopeData();
        this.gravityData = testInfo.getGravityData();
        this.accelerationData = testInfo.getAccelerationData();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrientationData() {
        return orientationData;
    }

    public void setOrientationData(String orientationData) {
        this.orientationData = orientationData;
    }

    public String getGyroscopeData() {
        return gyroscopeData;
    }

    public void setGyroscopeData(String gyroscopeData) {
        this.gyroscopeData = gyroscopeData;
    }

    public String getGravityData() {
        return gravityData;
    }

    public void setGravityData(String gravityData) {
        this.gravityData = gravityData;
    }

    public String getAccelerationData() {
        return accelerationData;
    }

    public void setAccelerationData(String accelerationData) {
        this.accelerationData = accelerationData;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
