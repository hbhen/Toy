package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import toy.android.com.toy.activity.VideoActivity;

/**
 * Created by DTC on 2017/10/2816:48.
 */
public class VideoService extends Service {
    private static final String TAG = "video";
    Intent controlIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:(video) went");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String roomid = intent.getStringExtra("roomid");
        controlIntent = intent;
        if (controlIntent.getIntExtra("method",0)==1){
            Log.i(TAG, "onCreate:(video) went1");
            controlIntent.putExtra("roomid", roomid);
            controlIntent.putExtra("method", 1);
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            controlIntent.setClass(VideoService.this, VideoActivity.class);
            startActivity(controlIntent);
        }else{
            Log.i(TAG, "onCreate:(video) went1");
            controlIntent.putExtra("roomid", roomid);
            controlIntent.putExtra("method", 2);
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                controlIntent=intent;
//                controlIntent.putExtra("roomid",roomid);
//                controlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                controlIntent.setClass(VideoService.this, VideoActivity.class);
//                startActivity(controlIntent);
//
//            }
//        }).run();
        return START_STICKY;//需要不需要意外情况以后再次连接??现在是设置的需要连接!!
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(controlIntent);
    }
}
