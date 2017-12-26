package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiDisconnectedReceiver extends BroadcastReceiver {
    public WifiDisconnectedReceiver() {
    }

    private static final String TAG = "tag";
    private String action = "toy.android.com.toy.wifidisconnected";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.EXTRA_WIFI_STATE)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:

                    break;
                case WifiManager.WIFI_STATE_DISABLING:

                    break;
            }
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
