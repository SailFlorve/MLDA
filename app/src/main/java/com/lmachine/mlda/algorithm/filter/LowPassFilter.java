package com.lmachine.mlda.algorithm.filter;

import android.util.Log;

import com.lmachine.mlda.algorithm.util.FilterUtil;

import java.util.List;

/**
 * 低通滤波器
 */
public class LowPassFilter {
    public static final String TAG = "LowPassFilter";
    private static final float WEIGHT_DEFAULT = 0.1f;

    public static double[] filter(double[] measureValue) {
        return filter(measureValue, WEIGHT_DEFAULT);
    }

    /**
     * 滤波
     *
     * @param measureValue 测量值
     * @param weight       权值，为0结果恒定不变，为1结果不会进行滤波
     * @return 滤波后的值
     */
    public static double[] filter(double[] measureValue, float weight) {
        if (measureValue.length == 0) return measureValue;
        double[] result = new double[measureValue.length];
        double last = measureValue[0];
        for (int i = 0; i < measureValue.length; i++) {
            result[i] = last * (1.0f - weight) + measureValue[i] * weight;
            last = result[i];
        }
        return result;
    }

    public static List<float[]> filter(List<float[]> measureValue) {
        return filter(measureValue, WEIGHT_DEFAULT);
    }

    /**
     * 直接对测量结果进行滤波，返回滤波后的结果。
     *
     * @param measureValue
     * @param weight
     * @return
     */
    public static List<float[]> filter(List<float[]> measureValue, float weight) {
        Log.d(TAG, "filter: ");
        List<double[]> arrayList = FilterUtil.floatArrayListToXYZDoubleArray(measureValue);
        double[] xArrayFilter = filter(arrayList.get(0), weight);
        double[] yArrayFilter = filter(arrayList.get(1), weight);
        double[] zArrayFilter = filter(arrayList.get(2), weight);
        return FilterUtil.XYZDoubleArrayToFloatArrayList(xArrayFilter, yArrayFilter, zArrayFilter);
    }

}
