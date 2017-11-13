package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import toy.android.com.toy.broadcastreceiver.ControlCallReceiver;

public class ControlCallSevice extends Service {
    public ControlCallSevice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ListenerManager.getInstance().registerListtener(new ListenerManager.IListener() {
//            @Override
//            public void notifyAllActivity(String s, JSOect object) {
//
//            }
//        });
        ControlCallReceiver controlPlayReceiver = new ControlCallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("contact_toy");
        registerReceiver(controlPlayReceiver, intentFilter);
    }
}
