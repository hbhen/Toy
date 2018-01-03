package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import toy.android.com.toy.receiver.MyReceiver;

public class MyService extends Service {
    private int timer = 0;
    private boolean isRunning = true;
    MyReceiver mMyReceiver = new MyReceiver();

    public MyService() {
    }

    private RecyclerBinder mBinder = new RecyclerBinder();


    class RecyclerBinder extends Binder {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Notification.Builder notifation=new Notification.Builder(this);
//        Intent intent=new Intent(this,MainActivity.class);
////        PendingIntent pendingIntent=PendingIntent.getActivity(MyService.this,0,intent);
//        Log.i("service", "onCreate: went");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("niu");
        registerReceiver(mMyReceiver,intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service", "onStartCommand: went");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Log.i("service", "onDestroy: went");
    }
}
