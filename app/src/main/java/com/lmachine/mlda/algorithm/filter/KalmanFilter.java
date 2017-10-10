package com.lmachine.mlda.algorithm.filter;

import android.util.Log;

import com.lmachine.mlda.algorithm.util.FilterUtil;

import java.util.List;

/**
 * 卡尔曼滤波器
 */
public class KalmanFilter {

    public static final String TAG = "KalmanFilter";
    public static final double Q_DEFAULT = 1E-5;
    public static final double R_DEFAULT = 0.01;

    /**
     * 滤波
     *
     * @param measureValue 测量值数组
     * @return 滤波后的值数组
     */
    public static double[] filter(double[] measureValue) {
        return filter(measureValue, Q_DEFAULT, R_DEFAULT);
    }

    /**
     * 滤波
     *
     * @param measureValue 测量值数组
     * @param Q            对模型的信任程度
     * @param R            对测量的信任程度
     * @return 滤波后的值数组
     */
    public static double[] filter(double[] measureValue, double Q, double R) {
        if (measureValue.length == 0) return measureValue;
        int size = measureValue.length;
        double[] xhat = new double[size];
        double[] P = new double[size];
        double[] xhatminus = new double[size];
        double[] Pminus = new double[size];
        double[] K = new double[size];

        xhat[0] = measureValue[0];
        P[0] = 1.0;

        for (int k = 1; k < size; k++) {
            xhatminus[k] = xhat[k - 1];  //X(k|k-1) = AX(k-1|k-1) + BU(k) + W(k),A=1,BU(k) = 0
            Pminus[k] = P[k - 1] + Q; //P(k|k-1) = AP(k-1|k-1)A' + Q(k) ,A=1

            K[k] = Pminus[k] / (Pminus[k] + R);  // Kg(k)=P(k|k-1)H'/[HP(k|k-1)H' + R],H=1
            xhat[k] = xhatminus[k] + K[k] * (measureValue[k] - xhatminus[k]);// X(k|k) = X(k|k-1) + Kg(k)[Z(k) - HX(k|k-1)], H=1
            P[k] = (1 - K[k]) * Pminus[k];  // P(k|k) = (1 - Kg(k)H)P(k|k-1), H=1
        }
        return xhat;
    }

    public static List<float[]> filter(List<float[]> measureValue) {
        return filter(measureValue, Q_DEFAULT, R_DEFAULT);
    }

    /**
     * 直接对测量结果进行滤波，返回滤波后的结果。
     * @param measureValue
     * @param Q
     * @param R
     * @return
     */
    public static List<float[]> filter(List<float[]> measureValue, double Q, double R) {
        Log.d(TAG, "filter: ");
        List<double[]> arrayList = FilterUtil.floatArrayListToXYZDoubleArray(measureValue);
        double[] xArrayFilter = filter(arrayList.get(0), Q, R);
        double[] yArrayFilter = filter(arrayList.get(1), Q, R);
        double[] zArrayFilter = filter(arrayList.get(2), Q, R);
        return FilterUtil.XYZDoubleArrayToFloatArrayList(xArrayFilter, yArrayFilter, zArrayFilter);
    }

}
