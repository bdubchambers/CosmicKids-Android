package com.game.framework.implementation;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.game.framework.Audio;
import com.game.framework.Music;
import com.game.framework.Sound;

/**
 * Created by Brandon on 4/25/2016.
 */
public class AndroidAudio implements Audio {
    AssetManager assets;
    SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();
        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public Music createMusic(String fileName) {
        try {
            AssetFileDescriptor assetFileDescriptor = assets.openFd(fileName);
            return new AndroidMusic(assetFileDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load music: '"+fileName+"'");
        }
    }

    @Override
    public Sound createSound(String fileName) {
        try {
            AssetFileDescriptor assetFileDescriptor = assets.openFd(fileName);
            int soundID = soundPool.load(assetFileDescriptor, 0);
            return new AndroidSound(soundPool, soundID);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load sound: '"+fileName+"'");
        }
    }
}
