package toy.android.com.toy.activity;


import android.app.Application;
import android.content.Context;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Android on 2017/9/4.
 */

public class App extends Application {
    public static Context mContext;
    public static boolean isNetWorkAvailable = false;//全局的网络是否连接的标记
    public static int serviceCount=0;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }

    public static Context getContext() {
        return mContext;
    }

}
