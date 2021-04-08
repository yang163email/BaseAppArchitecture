package com.yan.myapplication.manager;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MediaManager {

    public static MediaPlayer mPlayer;
    private static boolean isPause;
    public static boolean isPrepared = false;
    public static String filepathStrings;

    public static void playSound(String filePathString,
                                 OnCompletionListener onCompletionListener) {//
        if (mPlayer == null) {
            mPlayer = getMediaPlayer();
            //保险起见，设置报错监听
            mPlayer.setOnErrorListener((mp, what, extra) -> {
                mPlayer.reset();
                return false;
            });
        } else {
            mPlayer.reset();//就恢复
        }
        try {
            filepathStrings = filePathString;
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setDataSource(filePathString);
            mPlayer.setVolume(90, 90);
            mPlayer.setLooping(false);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isPrepared = true;
                }
            });
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止函数
    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }


    //停止函数
    public static void reset() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();
        }
    }

    //停止函数
    public static boolean isStart() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    //继续
    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }


    public static void release() {
        if (mPlayer != null) {
            isPrepared = false;
            mPlayer.release();
            mPlayer = null;
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return new MediaPlayer();
    }
}
