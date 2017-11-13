package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Android on 2017/9/6.
 */
//在这个里面做激活的操作
public class TimeService extends Service{
    private final String TAG = "1212321";
    int count=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Timer timer=new Timer();
        final TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                count++;
//                ToastUtil.showToast(TimeService.this,"我");
                Log.i(TAG, "run: "+count);
            }

        };
        timer.schedule(timerTask,1,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //app退出后,后台仍然在运行 有好处有坏处.那我能在这个service里面做什么呢?

    }
}
