package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.ToastUtil;

/*
* wifi状态改变,无音乐播放(废弃,用音乐播放版 -- WifisStateChangeReceiver)
*
* */
@Deprecated
public class WlanConnectedBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "tagold";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean connected = wifi.isConnected();
            if (connected){
                LogUtil.i(TAG, "onReceive: true");
                ToastUtil.showToast(context,"true");
            }else {
                ToastUtil.showToast(context,"false");
                LogUtil.i(TAG, "onReceive: false");
            }
//            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            NetworkInfo info = connManager.getActiveNetworkInfo();
//            if (!update && wifi.isConnected()) {
//                update = true;
//                LogUtil.i(TAG, "onReceive: info:" + info.toString());
//            } else if (!wifi.isConnected()) {
//                update = false;
//                LogUtil.i(TAG, "onReceive: info:"+info.toString());
//            }
            LogUtil.i(TAG, "onReceive: connectivity_change");
        }
        if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            LogUtil.i(TAG, "onReceive: WIFI_STATE_CHANGED");
            ToastUtil.showToast(context,"WIFI_STATE_CHANGED");
        }
//        wifi change以后
        if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
            LogUtil.i(TAG, "onReceive:STATE_CHANGE ");
            ToastUtil.showToast(context,"STATE_CHANGE");
        }
    }
}
