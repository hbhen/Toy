package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import toy.android.com.toy.R;
import toy.android.com.toy.service.WifiSoundListenerService;
import toy.android.com.toy.utils.ToastUtil;

//wifi的状态监听(对改变的动作监听)
public class WifiStateChangeReceiver extends BroadcastReceiver {
    public WifiStateChangeReceiver() {
    }

    private static final String TAG = "tag";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
//            ToastUtil.showToast(context, "RSSI_CHANGED_ACTION");
//        }
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            ToastUtil.showToast(context, "网络状态改变");
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (parcelableExtra != null) {
                NetworkInfo info = (NetworkInfo) parcelableExtra;
//                MediaPlayer mp = new MediaPlayer();
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
//                  声音提示
//                    playOffAlarm(context, mp);
                    ToastUtil.showToast(context, "wifi网络连接断开");
                    connectWifi(context);
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                    playOnAlarm(context, mp);
                    ToastUtil.showToast(context, "连接到网络:" + wifiInfo.getSSID());
                }
            } else if (intent.getAction().equals(WifiManager.EXTRA_WIFI_STATE)) {//wifi是否打开
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.i(TAG, "wifiState:" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.i(TAG, "wifiState:WIFI_STATE_DISABLED");
                        ToastUtil.showToast(context, "WIFI_STATE_DISABLED");//系统关闭wifi
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.i(TAG, "wifiState:WIFI_STATE_DISABLED");
                        ToastUtil.showToast(context, "WIFI_STATE_DISABLED");//系统打开wifi
                        break;
                }
            }
        }
    }
    private void connectWifi(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WifiSoundListenerService.class);
        context.startService(intent);
    }
    private void playOnAlarm(Context context, MediaPlayer mp) {
        mp.reset();
        mp.create(context, R.raw.sound_on);
        mp.start();
    }

    private void playOffAlarm(Context context, MediaPlayer mp) {
        mp.reset();
        mp.create(context, R.raw.sound_off);
        mp.start();
    }
}
