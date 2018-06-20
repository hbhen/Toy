package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Android on 2017/9/18.
 */

public class ControlPlayService extends Service {
    private static final String TAG = "playservice";
    private static MediaPlayer mMediaPlayer;
    public static final int OPEN_AUDIO = 1;
    public int playFlag;
    private static final String CURRENT_PLAY_MODE = "currentPlayMode";
    private static final String CURRENT_POSITION = "currentPosition";
    //播放模式的状态: 顺序播放
    public static final int PLAY_MODE_ORDER = 1;
    //单曲循环
    public static final int PLAY_MODE_SINGLE = 2;
    //随机播放
    public static final int PLAY_MODE_RANDOM = 3;
    //当前的播放模式
    private int currentPlayMode;
    private Random random;
    private int duration;
    private int currentPosition;
    private boolean flag;

    private SharedPreferences mSp;
    //    private boolean isStop = false;
    private boolean isPrepared = false;
    private boolean isPlaying = false;

    public static MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        return mMediaPlayer;
    }

    public static ControlPlayService sControlPlayService;

    public ControlPlayService() {
    }

    public static synchronized ControlPlayService getInstance() {
        if (sControlPlayService == null) {
            sControlPlayService = new ControlPlayService();
        }
        return sControlPlayService;
    }

    @Override
    public void onCreate() {
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
    }

    //开启服务 是 为了播放音乐
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String playurl = intent.getStringExtra("url");
        String stringExtra = intent.getStringExtra("method");
        int method = Integer.parseInt(stringExtra);
        Log.i(TAG, "serviceMethod: " + method);
        Log.i(TAG, "onStartCommand: playurl" + playurl);
        switch (method) {
            case 1:
                Log.i(TAG, "PLAY_MUSIC: went ");
                Log.i(TAG, "play_music: playurl" + playurl);
                PrepareMusic(playurl);
                PlayMusic(playurl);
//                }
                break;
            case 2:
                Log.i(TAG, "PAUSE_MUSIC: went ");
                pauseMusic();
                break;
            case 3:
                Log.i(TAG, "STOP_MUSIC: went ");
                stopMusic();
                break;
            default:
                break;
        }
        //获取音乐播放列表,这里只需要播放的url即可
        //如果当前正在播放,给服务器提示正在播放,并且不允许播放别的音乐.直到当前的音乐播放完成,才播放别的音乐,可以考虑用数据库,来保存服务器推送过来的url.并通过指针,注意播放,添加进来的音乐.
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopMusic() {
        Log.i(TAG, "stopMusic: 4");
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.seekTo(0);
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    private void pauseMusic() {
        Log.i(TAG, "pauseMusic: 3");
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.pause();
//        isStop = true;
        isPlaying = false;
    }

    private void PrepareMusic(String playurl) {
        Log.i(TAG, "PrepareMusic: 2");
        MediaPlayer mediaPlayer = getMediaPlayer();

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(playurl));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPrepared = true;
    }

    private void PlayMusic(String playurl) {
        Log.i(TAG, "PlayMusic: 1");
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.start();

//        mediaPlayer.setLooping(true);
        isPlaying = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Binder controlPlayBinder = new ControlPlayBinder();
        return null;
    }

    @Override
    public void onDestroy() {
//        日期:2018年3月6日 改
        MediaPlayer mediaPlayer = getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
//        stopSelf();
        super.onDestroy();
    }

    interface onMusicPlaying {
        void shutDownService(ControlPlayService controlPlayService, MediaPlayer mediaPlayer);
    }
}
