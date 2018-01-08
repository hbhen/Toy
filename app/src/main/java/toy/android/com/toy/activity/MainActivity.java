package toy.android.com.toy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import toy.android.com.toy.service.CheckNetWorkStateService;
import toy.android.com.toy.service.KeepLiveService;
import toy.android.com.toy.service.WifiSoundListenerService;
import toy.android.com.toy.utils.NetWorkUtil;
import toy.android.com.toy.utils.SPUtils;

public class MainActivity extends BaseActivity {

    private final String TAG = "Main";
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
        acquireWakeLock();
        JPushInterface.initCrashHandler(this);
//        判断是不是第一次进入软件:第一次:只做两件事:1,设置权限 2,联网
        if (isfirstOpen) {
//            检查并设置权限(question:只需要在第一次进入app的时候检查权限么?)
            checkPhonePermission();
//            TODO 1.播放一段友好的欢迎话语

//            2.直接打开wifi开关
            mWifiManager.setWifiEnabled(true);
//            3.打开wifi开关以后,去做联网的操作.(这一步很不和谐,以后,应该是等待联网的指令再联网,也就是通过MyReceiver的指令);
            Log.i(TAG, "onCreate: 第一次进入app,设置wifi");
            getWifiKey();
            SPUtils.putBoolean(this, "isfirstopen", false);
        } else {
            //非第一次进入app
            mWifiManager.setWifiEnabled(true);
            mRid = JPushInterface.getRegistrationID(getApplicationContext());
            if (NetWorkUtil.isNetworkConn(this)) {//有网络连接
                Log.i(TAG, "onCreate: networkconnect?:yes");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: zouzou");
                        mKeepLiveIntent = new Intent();
                        mKeepLiveIntent.setClass(MainActivity.this, KeepLiveService.class);
                        mKeepLiveIntent.putExtra("devicecode", mRid);
                        startService(mKeepLiveIntent);
                        Log.i(TAG, "run: time" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(SystemClock.currentThreadTimeMillis()));
                    }
                }, 0, 180000);//多级重复一次?
                Log.i(TAG, "onCreate: networkwificonnect?:" + NetWorkUtil.isNetworkConn(this));
            } else {//无网络连接
                Log.i(TAG, "onCreate: networkwificonnect?:nope");
                getWifiKey();
                Log.i(TAG, "onCreate: networkwificonnect?:" + NetWorkUtil.isNetworkConn(this));
            }
        }
//        checkNetState();

    }

    private void getWifiKey() {
        Intent intent = new Intent();
//        intent.setClass(this, TestService.class);
        intent.setClass(this, WifiSoundListenerService.class);
        startService(intent);
    }

    //    判断wifi的开关是否打开?
    private boolean isWifeEnable() {
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: went");
        //退出登录|
        this.stopService(mKeepLiveIntent);
        toyLogout();
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
