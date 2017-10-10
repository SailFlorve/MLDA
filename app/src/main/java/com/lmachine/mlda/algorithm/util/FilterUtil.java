package com.lmachine.mlda.algorithm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterUtil {
    public static <T> List<T> arrayToList(T[] array) {
        return Arrays.asList(array);
    }

    public static double[] intArrayToDoubleArray(int[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    public static float[] doubleArrayToFloatArray(double[] array) {
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (float) array[i];
        }
        return result;
    }

    public static double[] floatArrayToDoubleArray(float[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    public static int[] doubleArrayToIntArray(double[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (int) array[i];
        }
        return result;
    }

    /**
     * 把测量结果List Float[] 转换成x、y、z三个的double数组，存在list里。
     *
     * @param measureValue
     * @return
     */
    public static List<double[]> floatArrayListToXYZDoubleArray(List<float[]> measureValue) {
        double[] xArray = new double[measureValue.size()];
        double[] yArray = new double[measureValue.size()];
        double[] zArray = new double[measureValue.size()];
        for (int i = 0; i < measureValue.size(); i++) {
            xArray[i] = measureValue.get(i)[0];
            yArray[i] = measureValue.get(i)[1];
            zArray[i] = measureValue.get(i)[2];
        }
        List<double[]> result = new ArrayList<>();
        result.add(xArray);
        result.add(yArray);
        result.add(zArray);
        return result;
    }

    public static List<float[]> XYZDoubleArrayToFloatArrayList(double[] x, double[] y, double[] z) {
        List<float[]> result = new ArrayList<>();
        for (int i = 0; i < x.length; i++) {
            result.add(new float[]{(float) x[i], (float) y[i], (float) z[i]});
        }
        return result;
    }
}
