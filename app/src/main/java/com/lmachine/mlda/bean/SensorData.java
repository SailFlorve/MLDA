package com.lmachine.mlda.bean;

import java.util.List;

/**
 * Created by SailFlorve on 2017/8/28 0028.
 * 传感器数据Gson实体类
 */

public class SensorData {
    private List<float[]> data;

    public List<float[]> getData() {
        return data;
    }

    public void setData(List<float[]> data) {
        this.data = data;
    }

    public static class SensorValue {
        private float x;
        private float y;
        private float z;

        public SensorValue(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }
    }

}
