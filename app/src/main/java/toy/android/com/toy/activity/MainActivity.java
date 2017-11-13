package toy.android.com.toy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toy.android.com.toy.R;
import toy.android.com.toy.bean.ActiveToyReqBean;
import toy.android.com.toy.bean.ActiveToyResBean;
import toy.android.com.toy.bean.ToyLoginReqBean;
import toy.android.com.toy.bean.ToyLoginResBean;
import toy.android.com.toy.interf.MyInterface;
import toy.android.com.toy.internet.Constants;
import toy.android.com.toy.utils.ToastUtil;


public class MainActivity extends BaseActivity {

    private final String TAG = "1212321";
    public static boolean isForeground = false;
    private String mDeviceId;
    private String mVersionName;
    private int mVersionCode;
    private int mWifiRssi;
    private String mRid;
    private BatteryReceiver mBatteryReceiver;
    private int mCurrent;
    private int mSystemVoice;
    private String token = "";
    int count = 0;
    public final static int REQUEST_READ_PHONE_STATE = 1;
    public final static int CAMERA = 2;
    public static final int RECORD_AUDIO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flagShowWhenLocked = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        int flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().addFlags(flagShowWhenLocked);
        getWindow().addFlags(flagKeepScreenOn);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        checkPhonePermission();
        JPushInterface.initCrashHandler(this);
        mRid = JPushInterface.getRegistrationID(getApplicationContext());
        Log.i("1212321", "jpushisstop: " + JPushInterface.isPushStopped(this));
        Log.i("devid", mRid);
        Log.i(TAG, "onCreate: ++++" + JPushInterface.isPushStopped(this));
        ToyLogin(mDeviceId);
    }

    private void checkPhonePermission() {
        Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO未启用");
        Log.i(TAG, "checkPhonePermission: " + "CAMERA未启用");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                    .READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                Log.i(TAG, "onRequestPermissionsResult: REQUEST_READ_PHONE_STATE" + "请求");
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                mDeviceId = telephonyManager.getDeviceId();
                ToastUtil.showToast(this, "mDeviced" + mDeviceId);
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
            ToastUtil.showToast(this, "mDeviced" + mDeviceId);
            Log.i(TAG, "deviced---" + mDeviceId);
            Log.i(TAG, "checkPhonePermission: " + "RECORD_AUDIO启用3");
            Log.i(TAG, "checkPhonePermission: " + "CAMERA启用3");
        }
    }

    private void init() {

        WifiManager systemService = (WifiManager) getSystemService(WIFI_SERVICE);
        //获取wifi信息API
        WifiInfo connectionInfo = systemService.getConnectionInfo();
        //wifi信号强度
        mWifiRssi = connectionInfo.getRssi();

        //获取音量API
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //当前音乐音量
        int musicVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //当前通话音量
        int callVoice = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        //当前铃声音量
        int ringVoice = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        //当前提示音音量
        int alarmVoice = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        //当前系统音量
        mSystemVoice = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Log.i(TAG, "onCreate: " + mDeviceId + "--");

        //获取版本信息:
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            mVersionCode = packageInfo.versionCode;
            mVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //获取当前设备的电量
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        Intent intent = new Intent();
        intent.setAction("");
        registerReceiver(mBatteryReceiver, intentFilter);
        sendBroadcast(intent);
    }

    private void initToy(String versionName, int wifiRssi, String rid, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyInterface myInterface = retrofit.create(MyInterface.class);
        ActiveToyReqBean.BODYBean bodyBean = new ActiveToyReqBean.BODYBean("A", "TOY", versionName, mCurrent + "",
                wifiRssi + "", "", "", mSystemVoice + "", mRid);
        ActiveToyReqBean activeToyReqBean = new ActiveToyReqBean("REQ", "HEART", "", new SimpleDateFormat
                ("yyyyMMddHHmmssSSS").format(new Date()), bodyBean, "", token, "1");
        Gson gson = new Gson();
        String s = gson.toJson(activeToyReqBean);
        Call<ActiveToyResBean> activeToyResBeanCall = myInterface.ACTIVE_TOY_RES_BEAN_CALL(s);
        activeToyResBeanCall.enqueue(new Callback<ActiveToyResBean>() {
            @Override
            public void onResponse(Call<ActiveToyResBean> call, Response<ActiveToyResBean> response) {
                Log.i(TAG, "onResponse:init toy" + response.message());
                Log.i(TAG, "onResponse:init toy " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ActiveToyResBean> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t);
            }
        });
    }

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
                ToastUtil.showToast(MainActivity.this, response.message());
                Log.i(TAG, "onResponse: toy login" + response.body().getTOKEN());
                Log.i(TAG, "onResponse: toy login" + response.body().getBODY());
                Log.i(TAG, "onResponse: toy login" + "成功了");
                token = response.body().getTOKEN();
                init();
                initToy(mVersionName, mWifiRssi, mRid, token);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: went");
//        Intent intent=new Intent(MainActivity.this,MyService.class);
//        stopService(intent);
        //正常退出的时候,走onDestroy方法,
        //可不可以,在onDestroy里面做启动
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

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            mCurrent = intent.getExtras().getInt("level");
            Bundle extras = intent.getExtras();

            int total = intent.getExtras().getInt("scale");
            Log.i(TAG, "onReceive-- " + "current:" + mCurrent + "total:" + total);

        }
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
