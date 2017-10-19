package com.lmachine.mlda.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

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
    private String orientationData;
    @Column
    private String gyroscopeData;
    @Column
    private String gravityData;
    @Column
    private String accelerationData;
    @Column
    private String magSensorVendor;
    @Column
    private String magSensorName;
    @Column
    private String gyroVendor;
    @Column
    private String gyroName;
    @Column
    private String gravitySensorVendor;
    @Column
    private String gravitySensorName;
    @Column
    private String accelerationSensorVendor;
    @Column
    private String accelerationSensorName;
    @Column(defaultValue = "0")
    private boolean isFiltered;
    @Column(defaultValue = "0")
    private int inputTimes;
    @Column(defaultValue = "40")
    private int rate;

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

    public String getMagSensorVendor() {
        return magSensorVendor;
    }

    public void setMagSensorVendor(String magSensorVendor) {
        this.magSensorVendor = magSensorVendor;
    }

    public String getMagSensorName() {
        return magSensorName;
    }

    public void setMagSensorName(String magSensorName) {
        this.magSensorName = magSensorName;
    }

    public String getGyroVendor() {
        return gyroVendor;
    }

    public void setGyroVendor(String gyroVendor) {
        this.gyroVendor = gyroVendor;
    }

    public String getGyroName() {
        return gyroName;
    }

    public void setGyroName(String gyroName) {
        this.gyroName = gyroName;
    }

    public String getGravitySensorVendor() {
        return gravitySensorVendor;
    }

    public void setGravitySensorVendor(String gravitySensorVendor) {
        this.gravitySensorVendor = gravitySensorVendor;
    }

    public String getGravitySensorName() {
        return gravitySensorName;
    }

    public void setGravitySensorName(String gravitySensorName) {
        this.gravitySensorName = gravitySensorName;
    }

    public String getAccelerationSensorVendor() {
        return accelerationSensorVendor;
    }

    public void setAccelerationSensorVendor(String accelerationSensorVendor) {
        this.accelerationSensorVendor = accelerationSensorVendor;
    }

    public String getAccelerationSensorName() {
        return accelerationSensorName;
    }

    public void setAccelerationSensorName(String accelerationSensorName) {
        this.accelerationSensorName = accelerationSensorName;
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
}
