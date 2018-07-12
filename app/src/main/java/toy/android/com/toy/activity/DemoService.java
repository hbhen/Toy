package toy.android.com.toy.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by DTC on 2018/6/11.
 */
@Deprecated
public class DemoService extends Service {
    private static final String TAG="MainActivity-tag";
    private final IBinder mIBinder=new MyBinder();
    private boolean mBo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBo = intent.getBooleanExtra("bo", true);

        return mIBinder;
    }

    public class MyBinder extends Binder {
        DemoService getService() {
            return DemoService.this;
        }
    }
    public void init(){

        Log.i(TAG, "init: went  and bo="+mBo);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy(demoservice): went");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: went");
        return super.onUnbind(intent);
    }

}

