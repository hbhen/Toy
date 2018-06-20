package toy.android.com.toy.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import toy.android.com.toy.R;

/**
 * Created by DTC on 2018/3/8.
 */

public class MusicManager {
    private static MediaPlayer mMediaPlayer;
    private Context mContext;

    public MusicManager() {
    }

    public MusicManager(Context context) {
        mContext = context;
    }

    public static MediaPlayer getMusicPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            return mMediaPlayer;
        } else {
            return mMediaPlayer;
        }
    }
//开始写的是没有MediaPlayer值返回的,但是这样在外面拿到的对象就不是同一个.
    public static MediaPlayer playWelcomeMusic(MediaPlayer mediaPlayer, Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.welcome);
            mediaPlayer.start();
            return mediaPlayer;
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.welcome);
            mediaPlayer.start();
            return mediaPlayer;
        }
    }

    public static void stopWelcomeMusic(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } else {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    public static MediaPlayer playDisconnected(MediaPlayer mediaPlayer, Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.disconnected);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.start();
            return mediaPlayer;
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.disconnected);
            mediaPlayer.start();
            return mediaPlayer;

        }
    }

    public static MediaPlayer playConnected(MediaPlayer mediaPlayer, Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.connected);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.start();
            return mediaPlayer;
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.connected);
            mediaPlayer.start();
            return mediaPlayer;
        }
    }
}
