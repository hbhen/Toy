package toy.android.com.toy.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by DTC on 2017/12/1317:31.
 */

public class NetWorkUtil {
    //获取当前的网络状态
    private static final int CURRENT_NETWORK_STATES_MOBILE = 0;//移动网络
    private static final int CURRENT_NETWORK_STATES_NO = -1;//-1：没有网络
    private static final int CURRENT_NETWORK_STATES_WIFI = 1;//1：WIFI网络
    private static final int CURRENT_NETWORK_STATES_WAP = 2;//2：wap网络
    private static final int CURRENT_NETWORK_STATES_NET = 3;//3：net网络
    private static Intent mKeepLiveIntent;

    public static int getAPNType(Context context) {
        int netType = CURRENT_NETWORK_STATES_NO;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络信息
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        //得到网络类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = networkInfo.getExtraInfo().toLowerCase().equals("cmnet") ? CURRENT_NETWORK_STATES_NET : CURRENT_NETWORK_STATES_WAP;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = CURRENT_NETWORK_STATES_WIFI;
        }
        return netType;
    }

    /**
     * 判断WiFi网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConn(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断数据流量是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConn(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConn(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断当前的进行或者服务是否存在（是否运行）
    public static boolean isServiceRunning(Context mContext, String className) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        //30 就是一个数字 可以用常量Integer.MAX_VALUE 代替

        if (!(serviceList.size() > 0)) {
            return false;
        }
        boolean isRunning = false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static int getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return CURRENT_NETWORK_STATES_WIFI;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return CURRENT_NETWORK_STATES_MOBILE;
            }
        } else {
            return CURRENT_NETWORK_STATES_NO;
        }
        return CURRENT_NETWORK_STATES_NO;
    }

    /*
    * @params available :网络是否可用
    *       (0:表示当前无网络 1:表示当前有网络)
    * */
    public void setIsNetworkAvailable(int available) {

        if (available == 0) {

        } else if (available == 1) {

        }
    }

}