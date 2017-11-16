package toy.android.com.toy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import toy.android.com.toy.activity.VideoActivity;

/**
 * Created by DTC on 2017/10/2816:48.
 */

public class VideoService2 extends IntentService {
    private static final String TAG = "video";
    private static final String TAGD = "circle";

    public VideoService2() {
        super("video");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        Log.d(TAGD, "(videoservice2)onHandleIntent: went");
//        Log.d(TAGD, "(videoservice2)onHandleIntent: went" + intent.toString());
//        intent.setClass(this, VideoActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        this.startActivity(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String roomid = intent.getStringExtra("roomid");
        String method = intent.getStringExtra("method");
        Log.d(TAGD, "onStartCommand: went ");
        intent.setClass(this, VideoActivity.class);
        intent.putExtra("roomid", roomid);
        intent.putExtra("method", method);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAGD, "(videoservice2)onCreate: went");
    }
}
