package com.lmachine.mlda.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RawRes;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SailFlorve on 2017/10/18 0018.
 * 声音管理类
 */

public class SoundMgr {
    private String TAG = "SoundMgr";

    private SoundPool soundPool;
    private SparseArrayCompat<Integer> idMap = new SparseArrayCompat<>();

    private boolean isPlaySound;

    @SuppressWarnings("deprecation")
    public SoundMgr(Context context, @RawRes int... soundIds) {
        isPlaySound = SPUtil.load(context).getBoolean("play_sound", true);
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(soundIds.length);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//设置音频流的合适的属性
            builder.setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(soundIds.length, AudioManager.STREAM_MUSIC, 0);
        }
        for (int i = 0; i < soundIds.length; i++) {
            idMap.put(i, soundPool.load(context, soundIds[i], 1));
        }
    }

    public void play(int index) {
        if (isPlaySound) {
            int result = soundPool.play(idMap.get(index), 1, 1, 1, 0, 1);
        }
    }

    public void release() {
        soundPool.release();
    }
}
