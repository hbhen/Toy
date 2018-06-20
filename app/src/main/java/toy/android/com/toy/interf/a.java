package toy.android.com.toy.interf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by DTC on 2018/3/9.
 */

public class a {
    private boolean isNetworkAvailable = false;
    private NetworkInfo network;
    private ConnectivityManager connManager;

    private void check(Context ctx) {
        connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

//判断是否有可用网络
        network = connManager.getActiveNetworkInfo();
        if (null != network && network.isAvailable()) {
            if (!isNetworkAvailable) {
                isNetworkAvailable = true;
//                MyApplication.getInstance().setIsNetworkAvailable(1);
                System.out.println("有可用网络....");
            }
        } else {
            if (isNetworkAvailable) {
                isNetworkAvailable = false;
//                MyApplication.getInstance().setIsNetworkAvailable(0);
                System.out.println("无可用网络....");
            }
        }
    }
}
