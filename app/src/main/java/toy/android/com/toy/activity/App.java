package toy.android.com.toy.activity;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Android on 2017/9/4.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }
}
