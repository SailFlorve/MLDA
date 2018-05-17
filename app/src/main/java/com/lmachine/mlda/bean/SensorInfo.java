package com.lmachine.mlda.bean;

import java.util.ArrayList;
import java.util.List;

public class SensorInfo {
    private String name;
    private String vendor;
    private String des;
    private int[] range;
    private boolean[] axisShowed;

    private List<float[]> data;

    public SensorInfo(String name, String vendor, String des) {
        data = new ArrayList<>();
        this.name = name;
        this.vendor = vendor;
        this.des = des;
    }

    public SensorInfo(String name, String vendor, String des, int[] range, boolean[] axisShowed) {
        data = new ArrayList<>();
        this.name = name;
        this.vendor = vendor;
        this.des = des;
        this.range = range;
        this.axisShowed = axisShowed;
    }

    public void addData(float[] d) {
        data.add(d);
    }

    public List<float[]> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    public boolean[] getAxisShowed() {
        return axisShowed;
    }

    public void setAxisShowed(boolean[] axisShowed) {
        this.axisShowed = axisShowed;
    }
}
