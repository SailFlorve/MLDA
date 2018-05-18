package com.lmachine.mlda.util;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDataResolver {
    private DataChangedCallBack mDataChangedCallBack;

    private int sensorCount;
    private int headCount;
    private byte[] mBytes = new byte[0];

    private static final int HEAD_OK = 4;
    private static final double G = 9.79362;

    public BluetoothDataResolver() {

    }


    public BluetoothDataResolver(DataChangedCallBack mDataChangedCallBack) {
        this.mDataChangedCallBack = mDataChangedCallBack;
    }

    public void setDataChangedCallBack(DataChangedCallBack d) {
        mDataChangedCallBack = d;
    }

    public void dataSync(byte[] bytes) {
        mBytes = byteMerger(mBytes, bytes);
        dataSyncCheck();
    }

    public interface DataChangedCallBack {
        void onDataChanged(List<float[]> f);
    }


    private List<Integer> checkHead(byte[] bytes) {
        List<Integer> r = new ArrayList<>();
        for (int index = 0; index < bytes.length; index++) {
            if (bytes[index] == (byte) 0xFF) {
                headCount++;
                if (headCount == HEAD_OK) {
                    headCount = 0;
                    r.add(index - 3);
                }
            } else {
                headCount = 0;
            }
        }
        headCount = 0;
        return r;
    }

    private static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    private void addDataToList(byte[] bytes) {
        List<float[]> dataList = new ArrayList<>();
        int read = 0;
        float[] f = new float[3];
        int fPosition = 0;
        while (read < bytes.length - 1) {
            byte b = bytes[read];
            byte c = bytes[read + 1];
//            Log.d("addDataToList b and c", String.valueOf(b) + " " + String.valueOf(c));
//            short m;
//            float d;
//            m = (short) ((short) b << 8 | c);
//            Log.d("addDataToList b and c", Integer.toHexString(m));
//            d = (float) (m / 8192f * G);

            short s1 = (short) (b & 0xFF);
            short s2 = (short) (c & 0xFF);
            short s3 = (short) (s1 << 8 | s2);

            float d = (float) (s3 / 8192f * G);

            f[fPosition] = d;
            fPosition++;
            read += 2;
            if (fPosition == 3) {
                dataList.add(f);
                f = new float[3];
                fPosition = 0;
            }
        }
        if (dataList.size() == sensorCount) {
            mDataChangedCallBack.onDataChanged(dataList);
        }
    }

    private byte[] cutDataArray(int a, int b) {
        a += 4;
        byte[] r = new byte[sensorCount * 6];
        byte[] n = new byte[mBytes.length - b];
        for (int i = 0; a < mBytes.length; a++) {
            if (a < b && i < sensorCount * 6) {
                r[i] = mBytes[a];
                i++;
            } else if (a >= b) {
                n[a - b] = mBytes[a];
            }
        }
        mBytes = n;
        return r;
    }

    private void dataSyncCheck() {
        List<Integer> a = checkHead(mBytes);
        if (a.size() >= 2) {
            if (sensorCount == 0) {     //没有初始化传感器数目
                sensorCount = (a.get(1) - a.get(0) + 1) / 6;
            }
            byte[] c = cutDataArray(a.get(0), a.get(1));
            addDataToList(c);
            dataSyncCheck();
        }
    }
}

