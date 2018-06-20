package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;
import toy.android.com.toy.R;
import toy.android.com.toy.activity.App;
import toy.android.com.toy.bean.HasConnectedEvent;
import toy.android.com.toy.service.KeepLiveService;
import toy.android.com.toy.service.WifiSoundListenerService;
import toy.android.com.toy.utils.LogUtil;

//wifi的状态监听(对改变的动作监听)
public class WifiStateChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "tagnew";
    private static long WIFI_TIME = 0;
    private static long ETHERNET_TIME = 0;
    private static long NONE_TIME = 0;
    private static int LAST_TYPE = -3;
    private boolean flag1 = true;
    private boolean flag2 = true;
    private boolean isNetworkAvailable = false;
    private NetworkInfo network;
    private ConnectivityManager mConnectivityManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive: dddddddd");
//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            LogUtil.i(TAG, "connectivity_action  check  .");
            check(intent, context);
        }
        if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")){
            LogUtil.i(TAG, "connectivity_action  WIFI_STATE_CHANGED  .");
//            check(intent, context);
        }
        if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")){
            LogUtil.i(TAG, "android.net.wifi.STATE_CHANGE  .");
//            check(intent, context);
        }
    }

    //    这里有问题,App.isNetWorkAvailable
    private void check(Intent intent, final Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        判断是否有可用的网络
        network = mConnectivityManager.getActiveNetworkInfo();
        MediaPlayer mp = new MediaPlayer();
        if (network != null && network.isAvailable()) {
//            LogUtil.i(TAG,network.isAvailable()+"(down1)");
            LogUtil.i(TAG, "check:  上" + App.isNetWorkAvailable);
            if (!App.isNetWorkAvailable) {
                LogUtil.i(TAG,"!APP");
//                isNetworkAvailable = true;
                App.isNetWorkAvailable = true;
                EventBus.getDefault().post(new HasConnectedEvent(App.isNetWorkAvailable));
                String registrationID = JPushInterface.getRegistrationID(context);
                playOnAlarm(context, mp);

                intent.putExtra("devicecode", registrationID);
                intent.setClass(context, KeepLiveService.class);
                context.startService(intent);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mp = null;
                        LogUtil.i(TAG, "onCompletion: on 完了");
                    }
                });

                LogUtil.i(TAG, "check: 有网络.." + App.isNetWorkAvailable);
            }
        } else {
            LogUtil.i(TAG, "check: 下: " + App.isNetWorkAvailable);

            //TODO 在这里出问题了 , 拿到的App.isNetworkAvailable 是true ,没有改变状态,需要在没有网络的情况下把App.isNetworkAvailable的状态变成false;
            if (App.isNetWorkAvailable) {
//                LogUtil.i(TAG,network.isAvailable()+"(down2)");
//                isNetworkAvailable = false;
                App.isNetWorkAvailable = false;
                playOffAlarm(context, mp);
                connectWifi(context);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        LogUtil.i(TAG, "onCompletion: off 完了");
                        mp.release();
                        mp = null;
                    }
                });
                LogUtil.i(TAG, "check: 无网络.." + App.isNetWorkAvailable);
            }
        }
    }

    private void connectWifi(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WifiSoundListenerService.class);
        context.startService(intent);
    }

    private void playOnAlarm(Context context, MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        mp.reset();
        mp = MediaPlayer.create(context, R.raw.connected);
        mp.start();
    }

    private void playOffAlarm(Context context, MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        mp.reset();
        mp = MediaPlayer.create(context, R.raw.disconnected);
        mp.start();
    }

}
