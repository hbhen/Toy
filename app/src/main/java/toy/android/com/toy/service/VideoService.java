package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import toy.android.com.toy.activity.VideoActivity2;

/**
 * Created by DTC on 2017/10/2816:48.
 */
public class VideoService extends Service {
    private static final String TAG = "video";
    private static final String TAGD = "circle";
    Intent controlIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAGD, "(videoservice)onCreate: went");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String roomid = intent.getStringExtra("roomid");
        String method = intent.getStringExtra("method");
//        Log.d(TAGD, "(videoservice)onStartCommand: went1方法是" + method);
        controlIntent = intent;
//        controlIntent.putExtra("roomid", roomid);
//        controlIntent.putExtra("method", method);
//        controlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        controlIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        controlIntent.setClass(VideoService.this, VideoActivity.class);
//        startActivity(controlIntent);
//        Log.d(TAGD, "(videoservice)onStartCommand: went2方法是" + method);
        if (method.equals("1")) {
            Log.d(TAGD, "(videoservice)onStartCommand: went方法是" + method);
            controlIntent.putExtra("roomid", roomid);
            controlIntent.putExtra("method", "1");
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            controlIntent.setClass(VideoService.this, VideoActivity2.class);
            startActivity(controlIntent);
        } else if (method.equals("2")) {
            controlIntent.putExtra("roomid", roomid);
            controlIntent.putExtra("method", "2");
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            controlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            controlIntent.setClass(VideoService.this, VideoActivity2.class);
            startActivity(controlIntent);
//            stopSelf();
            Log.d(TAGD, "(videoservice)onStartCommand: went方法是" + method);
        }
        return START_STICKY;//需要不需要意外情况以后再次连接??现在是设置的需要连接!!
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(controlIntent);
        Log.d(TAGD, "(videoservice)onDestroy: went");
    }
}
