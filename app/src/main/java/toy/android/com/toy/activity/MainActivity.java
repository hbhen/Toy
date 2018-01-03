package toy.android.com.toy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toy.android.com.toy.R;
import toy.android.com.toy.bean.ToyLoginReqBean;
import toy.android.com.toy.bean.ToyLoginResBean;
import toy.android.com.toy.bean.ToyLogoutReqBean;
import toy.android.com.toy.bean.ToyLogoutResBean;
import toy.android.com.toy.interf.MyInterface;
import toy.android.com.toy.internet.Constants;
import toy.android.com.toy.service.CheckNetWorkStateService;
import toy.android.com.toy.service.KeepLiveService;
import toy.android.com.toy.utils.NetWorkUtil;
import toy.android.com.toy.utils.SPUtils;
import toy.android.com.toy.utils.ToastUtil;

public class MainActivity extends BaseActivity {

    private final String TAG = "Main";
    public static boolean isForeground = false;
    private String mDeviceId;
    private String mVersionName;
    private int mVersionCode;
    private int mWifiRssi;
    private String mRid;
    private int mCurrent;

    private int mSystemVoice;
    private String token = "";
    public final static int REQUEST_READ_PHONE_STATE = 1;
    public final static int CAMERA = 2;
    public static final int RECORD_AUDIO = 4;

    PowerManager.WakeLock wakeLock = null;
    private int mCurrentBattery;
    private String mMic;
    private int mMusicVoice;
    private Intent mKeepLiveIntent;
    private WifiManager mWifiManager;
    List list = new ArrayList<>();
    private ConnectivityManager mConnectivityManager;
    private TextView mShow_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flagShowWhenLocked = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        int flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        getWindow().addFlags(flagShowWhenLocked);
        getWindow().addFlags(flagKeepScreenOn);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mShow_wifi = (TextView) findViewById(R.id.tv_show_wifi);
        boolean isfirstOpen = SPUtils.getBoolean(this, "isfirstopen", true);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //判断是不是第一次进入软件,是第一次,就什么都不干,就等着联网
        if (isfirstOpen) {
            Log.i(TAG, "onCreate: 第一次进入app");
            /*第一次进来,肯定是没有联网的,所以先打开wifi开关(一定).
            * 1,是跳转到监听声波的接收服务,在联网完成之后再去上传音量,等状态信息(这些信息是要等到toy能联网了才可以执行 .(如果没有联网,那就不能接受到后台发送的指令了,所以还是只能自己轮循联网的指令)
            * 2,那么在第一次进入app里面需要做什么操作? 播放一段音乐?放一段欢迎的话?
            *
            * */
//            1.播放一段友好的欢迎话语
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_off);
            mediaPlayer.start();

//            2.直接打开wifi开关
            mWifiManager.setWifiEnabled(true);
//            3.打开wifi开关以后,去做联网的操作.(这一步很不和谐,以后,应该是等待联网的指令再联网,也就是通过MyReceiver的指令);
            Log.i(TAG, "onCreate: 第一次进入app,设置wifi");
            connectWifi();

            /*如果是第一次打开玩具,联网,激活玩具
            * 1   声波联网
            * 1.1 开机自启动
            * 1.1.1第一次启动,就去联网做联网的相关操作;
            *      非第一次启动,判断有没有网络
            *      有网,就登录,激活;   无网络,做联网的相关操作;
            * 1.2 联网相关操作
            * 1.2.1 发出请求联网的提示音,等待联网的信号
            * 1.2.2 接收联网的信号,将网络信息保存,设置到wifi里
            *       成功,发出联网成功提示,等待手机给玩具的功能指令
            *       不成功,重新请求联网,重复1.2.1
            * 不是第一次启动玩具,判断有无网络,进行相关联网操作
            *       有网,登录(上传玩具的信息),等待手机传送的指令
            *       无网,做联网的相关操作
            * */
            SPUtils.putBoolean(this, "isfirstopen", false);
        } else {

            //非第一次进入app
            Log.i(TAG, "onCreate: 非第一次进入app,wifi是否可用(可用)");
            if (isWifeEnable()) {
                //打开wifi开关,判断当前的网络是否可用.或者是否有可用的网络
                if (isWifiConnect()) {
                    Log.i(TAG, "onCreate: 非第一次进入app,判断wifi是否连接(连接)");
                    String ssid = mWifiManager.getConnectionInfo().getSSID();
                    Log.i(TAG, "onCreate: ssid" + ssid);
                    ToastUtil.showToast(this, "ssid" + ssid);
                } else {
                    Log.i(TAG, "onCreate: 非第一次进入app,判断wifi是否连接(未连接)");
                    connectWifi();
                    ToastUtil.showToast(this, "(isWifiConnect)无网络连接1,正在去设置网络");
                }
            } else {
                Log.i(TAG, "onCreate: 非第一次进入app,wifi是否可用(不可用)");
                mWifiManager.setWifiEnabled(true);

                if (isWifiConnect()) {
                    Log.i(TAG, "onCreate: " + "有联网");
                    ToastUtil.showToast(this, "ssid:" + mWifiManager.getConnectionInfo().getSSID());
                    WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
                    String ssid = connectionInfo.getSSID();
                    String connectionInfoString = connectionInfo.toString();
                    ToastUtil.showToast(this, "ssid是:" + ssid);
                    Log.i(TAG, "onCreate: connectionInfoString" + connectionInfoString);
                    ToastUtil.showToast(this, "(isWifiConnect)无网络连接1");
                } else {
                    Log.i(TAG, "onCreate: " + "未联网");
                    connectWifi();
                    ToastUtil.showToast(this, "(isWifiConnect)无网络连接2");
                }
            }
        }

        checkPhonePermission();
        JPushInterface.initCrashHandler(this);
        acquireWakeLock();
        checkNetState();
        mRid = JPushInterface.getRegistrationID(getApplicationContext());
        if (NetWorkUtil.isWifiConn(this)) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mKeepLiveIntent = new Intent();
                    mKeepLiveIntent.setClass(MainActivity.this, KeepLiveService.class);
                    mKeepLiveIntent.putExtra("devicecode", mRid);
                    startService(mKeepLiveIntent);
                    Log.i(TAG, "run: time" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(SystemClock.currentThreadTimeMillis()));
                }
            }, 0, 180000);//多级重复一次?
        } else {
            ToastUtil.showToast(this, "无网络连接3");
        }
//        ToyLogin(mDeviceId);
    }

    //    判断wifi是否连接上
    private boolean isWifiConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        Log.i(TAG, "isWifiConnect: activeNetworkInfo" + activeNetworkInfo.toString());
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isAvailable();
        }
        return false;
    }

    //    判断wifi的开关是否打开?
    private boolean isWifeEnable() {
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        return mWifiManager.isWifiEnabled();
    }

    //    打开服务,设置wifi信息(通过声波接收)
    private void connectWifi() {
        Log.i(TAG, "connectWifi: 跳转前");
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, WifiSoundListenerActivity.class);
        startActivity(intent);
        Log.i(TAG, "connectWifi: 跳转后");
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

    private void checkNetState() {
        //开服务,去后台,不断的循环判断网络状态;
        Intent intent = new Intent();
        intent.setClass(this, CheckNetWorkStateService.class);
        startService(intent);
    }

    private void checkPhonePermission() {
        Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO权限");
        Log.i(TAG, "checkPhonePermission: " + "CAMERA权限");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                    .READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                Log.i(TAG, "onRequestPermissionsResult: REQUEST_READ_PHONE_STATE" + "请求");
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                mDeviceId = telephonyManager.getDeviceId();
//                ToastUtil.showToast(this, "mDeviced" + mDeviceId);
                Log.i(TAG, "deviced---" + mDeviceId);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "checkPhonePermission: " + "CAMERA未启用");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA);
            } else {
                Log.i(TAG, "checkPhonePermission: " + "CAMERA启用");
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO未启用");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
            } else {
                Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO启用");
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mDeviceId = telephonyManager.getDeviceId();
//            ToastUtil.showToast(this, "mDeviced" + mDeviceId);
            Log.i(TAG, "deviced---" + mDeviceId);
            Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO启用3");
            Log.i(TAG, "checkPhonePermission: " + "CAMERA启用3");
        }
    }

    //    获取当前设备的电量
    public class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrent = intent.getIntExtra("level", -1);
            int total = intent.getIntExtra("scale", -1);
            Log.d(TAG, "onReceive的currentBattery:" + mCurrent + "total:" + total);
            unregisterReceiver(this);
        }
    }

    //    first step 让玩具和服务器产生关联 激活玩具
    private void ToyLogin(String deviceId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyInterface anInterface = retrofit.create(MyInterface.class);
        ToyLoginReqBean.BODYBean bodyBean = new ToyLoginReqBean.BODYBean(deviceId, "", mRid, "A",
                mVersionName);
        ToyLoginReqBean toyLoginReqBean = new ToyLoginReqBean("REQ", "LOG", "", new SimpleDateFormat
                ("yyyyMMddHHmmssSSS").format(new Date()), bodyBean, "", "", "1");
        Gson gson = new Gson();
        String s = gson.toJson(toyLoginReqBean);
        Log.i(TAG, s);
        Call<ToyLoginResBean> toyLoginResBeanCall = anInterface.TOY_LOGIN_RES_BEAN_CALL(s);

        toyLoginResBeanCall.enqueue(new Callback<ToyLoginResBean>() {
            @Override
            public void onFailure(Call<ToyLoginResBean> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t);
            }

            @Override
            public void onResponse(Call<ToyLoginResBean> call, Response<ToyLoginResBean> response) {
//                ToastUtil.showToast(MainActivity.this, response.message());
                Log.i(TAG, "onResponse: toy login" + response.body().getTOKEN());
                Log.i(TAG, "onResponse: toy login" + response.body().getBODY());
                Log.i(TAG, "onResponse: toy login" + "成功了");
                token = response.body().getTOKEN();
                SPUtils.putString(MainActivity.this, "token", token);
//                initDeviceInfo();
//                toyHeart(mVersionName, mWifiRssi, mRid, token);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: went");
        //退出登录|
        this.stopService(mKeepLiveIntent);
        toyLogout();

//        Intent intent=new Intent(MainActivity.this,MyService.class);
//        stopService(intent);
        //正常退出的时候,走onDestroy方法,
        //可不可以,在onDestroy里面做启动
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
//                Log.d(TAG, "onResponse(KeepLiveService): " + response.body().getMSG());
//                Log.d(TAG, "onResponse(KeepLiveService): " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ToyLogoutResBean> call, Throwable t) {
                Log.d(TAG, "onFailure(KeepLiveService): " + t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        isForeground = true;

        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.i(TAG, "onRequestPermissionsResult: REQUEST_READ_PHONE_STATE" + "允许");
                }
                break;
            case CAMERA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.i(TAG, "onRequestPermissionsResult: CAMERA" + "允许");
                }
                break;
            case RECORD_AUDIO:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.i(TAG, "onRequestPermissionsResult: RECORD_AUDIO" + "允许");
                }
                break;
            default:
                break;
        }
    }

}
