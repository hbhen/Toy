package toy.android.com.toy.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toy.android.com.toy.R;
import toy.android.com.toy.bean.ToyLogoutReqBean;
import toy.android.com.toy.bean.ToyLogoutResBean;
import toy.android.com.toy.interf.MyInterface;
import toy.android.com.toy.internet.Constants;
import toy.android.com.toy.service.KeepLiveService;
import toy.android.com.toy.service.WifiSoundListenerService;
import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.MusicManager;
import toy.android.com.toy.utils.NetWorkUtil;
import toy.android.com.toy.utils.SPUtils;
import toy.android.com.toy.utils.VersionCodeAndVersionNameUtils;

public class MainActivity extends BaseActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    public static boolean isForeground = false;
    private String mDeviceId;
    private String mRid;
    private int mCurrent;
    public final static int REQUEST_READ_PHONE_STATE = 1;
    public final static int CAMERA = 2;
    public static final int RECORD_AUDIO = 4;
    PowerManager.WakeLock wakeLock = null;
    private Intent mKeepLiveIntent;
    private WifiManager mWifiManager;
    private TextView mShow_wifi;

    private KeepLiveService mKeepLiveService;
    private ServiceConnection mKeepLiveConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flagShowWhenLocked = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        int flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().addFlags(flagShowWhenLocked);
        getWindow().addFlags(flagKeepScreenOn);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        LogUtil.i(TAG, "onCreate  went  ");

        init();


    }

    private void init() {
        acquireWakeLock();
        LogUtil.i(TAG, "onStart: went");
        int localVersionCode = VersionCodeAndVersionNameUtils.getLocalVersionCode(this);
        String localVersionName = VersionCodeAndVersionNameUtils.getLocalVersionName(this);
        mShow_wifi = (TextView) findViewById(R.id.tv_show_wifi);
        mShow_wifi.setText("localVersionName:" + localVersionName + "localVersionCode:" + localVersionCode);
        boolean isfirstOpen = SPUtils.getBoolean(this, "isfirstopen", true);
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        JPushInterface.initCrashHandler(this);
        mKeepLiveIntent = new Intent();
        mKeepLiveIntent.setClass(MainActivity.this, KeepLiveService.class);
//        判断是不是第一次进入软件:第一次:只做两件事:1,设置权限 2,联网
        if (isfirstOpen) {
//            检查并设置权限(question:只需要在第一次进入app的时候检查权限么?)
//            checkPhonePermission();不用检查权限了,因为定制的系统,默认授权所有的权限
//            TODO 1.播放一段友好的欢迎话语
//            playWelcomeMusic();
            playWelcomeMusic1();

        } else {
            LogUtil.i(TAG, "onStart: fff");
            //非第一次进入app
            mWifiManager.setWifiEnabled(true);
            mRid = JPushInterface.getRegistrationID(getApplicationContext());
            LogUtil.i(TAG, "onStart(main): " + App.isNetWorkAvailable);
            if (NetWorkUtil.isNetworkConn(this)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                App.isNetWorkAvailable = true;
            }
            if (NetWorkUtil.isNetworkConn(this) && App.isNetWorkAvailable) {
                //有网络连接
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MediaPlayer musicPlayer = MusicManager.getMusicPlayer();
                musicPlayer = MusicManager.playConnected(musicPlayer, MainActivity.this);
                LogUtil.i(TAG, "onCreate: networkconnect?:yes");

//                有网络,每隔一段时间去保活.
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG, "run: zouzou");
//                        mKeepLiveIntent.setClass(MainActivity.this, KeepLiveService.class);
                        mKeepLiveIntent.putExtra("devicecode", mRid);
                        startService(mKeepLiveIntent);
                        LogUtil.i(TAG, "run: time" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(SystemClock.currentThreadTimeMillis()));
                    }
                }, 0, 90000);//多久重复一次?
                LogUtil.i(TAG, "onCreate: networkwificonnect?:" + NetWorkUtil.isNetworkConn(this));
            } else {
                //无网络连接
                MediaPlayer musicPlayer = MusicManager.getMusicPlayer();
                musicPlayer = MusicManager.playDisconnected(musicPlayer, MainActivity.this);
                LogUtil.i(TAG, "onCreate: networkwificonnect?:nope");
                getWifiKey();
                LogUtil.i(TAG, "onCreate: networkwificonnect?:" + NetWorkUtil.isNetworkConn(this));
            }
        }
//        checkNetState();
    }

    @Override
    protected void onStart() {
        super.onStart();
//      什么操作都不做了,改到oncreate里面做逻辑,因为,如果每次重启,会多加载一次网络的监听录音,造成多次开启,声波编码失败)
        LogUtil.i(TAG, "onStart went ");

    }

    private void playWelcomeMusic1() {
        MediaPlayer musicPlayer = MusicManager.getMusicPlayer();
//        musicPlayer.reset();

        musicPlayer = MusicManager.playWelcomeMusic(musicPlayer, this);
        mWifiManager.setWifiEnabled(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getWifiKey();
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
                //            2.直接打开wifi开关
//            3.打开wifi开关以后,去做联网的操作.(这一步很不和谐,以后,应该是等待联网的指令再联网,也就是通过MyReceiver的指令);
                LogUtil.i(TAG, "onCreate: 第一次进入app,设置wifi");
//                先不打开改变是否是第一次进入app的状态,现在是测试阶段,我要知道每次打开app播放音乐的情况,是否冲突!!!!!
                SPUtils.putBoolean(MainActivity.this, "isfirstopen", false);
            }
        });
    }

    private void playWelcomeMusic() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.welcome);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        final MediaPlayer finalMediaPlayer = mediaPlayer;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();

                //            2.直接打开wifi开关
                mWifiManager.setWifiEnabled(true);
//            3.打开wifi开关以后,去做联网的操作.(这一步很不和谐,以后,应该是等待联网的指令再联网,也就是通过MyReceiver的指令);
                LogUtil.i(TAG, "onCreate: 第一次进入app,设置wifi");
                getWifiKey();
//                先不打开改变是否是第一次进入app的状态,现在是测试阶段,我要知道每次打开app播放音乐的情况,是否冲突!!!!!
//                SPUtils.putBoolean(MainActivity.this, "isfirstopen", false);

            }
        });
    }

    private void getWifiKey() {
        Intent wifiSoundListenerServiceIntent = new Intent();
        wifiSoundListenerServiceIntent.setClass(this, WifiSoundListenerService.class);
        startService(wifiSoundListenerServiceIntent);
    }

    //    判断wifi的开关是否打开?
    private boolean isWifeEnable() {
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        return mWifiManager.isWifiEnabled();
    }

    //    获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }

    }

    //    获取当前设备的电量
    public class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrent = intent.getIntExtra("level", -1);
            int total = intent.getIntExtra("scale", -1);
            LogUtil.d(TAG, "onReceive的currentBattery:" + mCurrent + "total:" + total);
            unregisterReceiver(this);
        }
    }

    private void toyLogout() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyInterface myInterface = retrofit.create(MyInterface.class);
        ToyLogoutReqBean toyLogoutReqBean = new ToyLogoutReqBean("REQ", "LOGOUT", "", new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date()),
                "", SPUtils.getString(this, "token", ""), "1");
        Gson gson = new Gson();
        String s = gson.toJson(toyLogoutReqBean);
        Call<ToyLogoutResBean> toyLogoutResBeanCall = myInterface.TOY_LOGOUT_RES_BEAN_CALL(s);
        toyLogoutResBeanCall.enqueue(new Callback<ToyLogoutResBean>() {
            @Override
            public void onResponse(Call<ToyLogoutResBean> call, Response<ToyLogoutResBean> response) {
//                if (response == null) {
//                    LogUtil.i(TAG,"response 空");
//                    return;
//                } else {
//                    LogUtil.i(TAG,"不空");
//                    LogUtil.i(TAG,"onResponse(KeepLiveService): " + response.body().toString());
//                    LogUtil.i(TAG, "onResponse(KeepLiveService): " + response.body().getBODY().toString());
//                    LogUtil.i(TAG, "onResponse(KeepLiveService): " + response.body().getMSG());
//                    LogUtil.i(TAG, "onResponse(KeepLiveService): " + response.body().toString());
//
//                }
            }

            @Override
            public void onFailure(Call<ToyLogoutResBean> call, Throwable t) {
                LogUtil.d(TAG, "onFailure(KeepLiveService): " + t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume: went");
        isForeground = true;
        JPushInterface.onResume(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause: went");
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        toyLogout();
        stopService(mKeepLiveIntent);
        stopService(new Intent(this,WifiSoundListenerService.class));
        LogUtil.i(TAG, "onStop: went");
    }

    @Override
    protected void onDestroy() {

        LogUtil.i(TAG, "onDestroy: went");
        super.onDestroy();
        //退出登录|
//        this.stopService(mKeepLiveIntent);
//        toyLogout();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    LogUtil.i(TAG, "onRequestPermissionsResult: REQUEST_READ_PHONE_STATE" + "允许");
                }
                break;
            case CAMERA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    LogUtil.i(TAG, "onRequestPermissionsResult: CAMERA" + "允许");
                }
                break;
            case RECORD_AUDIO:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    LogUtil.i(TAG, "onRequestPermissionsResult: RECORD_AUDIO" + "允许");
                }
                break;
            default:
                break;
        }
    }

}
