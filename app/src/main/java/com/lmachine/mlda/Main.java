package com.lmachine.mlda;

import com.lmachine.mlda.util.BluetoothDataResolver;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BluetoothDataResolver dataSync = new BluetoothDataResolver();

        dataSync.setDataChangedCallBack(new BluetoothDataResolver.DataChangedCallBack() {
            @Override
            public void onDataChanged(List<float[]> f) {
                for (float[] floats : f) {
                    System.out.println(Arrays.toString(floats));
                }
                System.out.println();
            }
        });

//        byte b1 = (byte) 0x1F;
//        byte b2 = (byte) 0x93;
//
//        short s1 = (short) (b1 & 0xFF);
//        short s2 = (short) (b2 & 0xFF);
//        short s3 = (short) (s1 << 8 | s2);
//
//        System.out.println(s3);
//        float d = (float) (s3 / 8192f * 9.78);
//        System.out.println(d);

        System.out.println(Integer.toHexString(102 & 0xff));
        System.exit(123);
        String str = "66 0B 3A FD 23 1F 93 02 D1 FF 90 1E 63 66 66 66 66 0B 13 FD 10 1F 78 02 BE FF 94 1E 81 66 66 66 66 0B 1F FD 0C 1F 80 02 DA FF 83 1E 6A 66 66 66 66 0B 00 FC FB 1F 73 02 D1 FF 8C 1E 7E 66 66 66 66 0A F0 FC F4 1F 7B 02 CD FF 95 1E 6A 66 66 66 66 0B 04 FC E9 1F 81 02 C0 FF 87 1E 8A 66 66 66 66 0B 1D FC E8 1F 60 02 C3 FF 95 1E 66 66 66 66 66 0B 38 FC EE 1F 64 02 C5 FF 95 1E 82 66 66 66 66 0B 62 FD 0D 1F 64 02 CB FF 97 1E 83 66 66 66 66 0B 71 FD 10 1F 6E 02 D6 FF 80 1E 71 66 66 66 66 0B 70 FD 20 1F 84 02 CD FF 90 1E 68";

        String[] arr = str.split(" ");
        byte[] b = new byte[arr.length];

        int i;
        for (i = 0; i < arr.length; i++) {
            b[i] = (byte) Integer.parseInt(arr[i], 16);
        }

        dataSync.dataSync(b);

    }

    private static String getHexString(byte b) {
        String str = Integer.toHexString(b);
        if (str.length() >= 2) {
            str = str.substring(str.length() - 2, str.length());
        }
        return str;
    }
}
