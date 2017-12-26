package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WifiConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = WifiConnectedReceiver.class.getSimpleName();
    String action = "toy.android.com.toy.wificonnected";

    public WifiConnectedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {



//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
