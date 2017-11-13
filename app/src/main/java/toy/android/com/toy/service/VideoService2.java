package toy.android.com.toy.service;

import android.app.IntentService;
import android.content.Intent;

import toy.android.com.toy.activity.VideoActivity;

/**
 * Created by DTC on 2017/10/2816:48.
 */
public class VideoService2 extends IntentService {
    private static final String TAG = "video";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public VideoService2(String name) {
        super("video");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        intent.setClass(this, VideoActivity.class);
        this.startActivity(intent);
    }


}
