package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class CompleteInstallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "updateservice";

    public CompleteInstallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
//            if (packageName.contains("toy.android.com.toy"))
            if (packageName.contains("com.tongyuan.android.zhiquleyuan")) {
                Intent openIntent = new Intent();
                PackageManager pm = context.getPackageManager();
//                Log.i(TAG, "onReceive: completeinstallbroadcastreceiver:" + intent.getDataString());
                openIntent = pm.getLaunchIntentForPackage(intent.getDataString());
                openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
                context.unregisterReceiver(this);
            }
            System.out.println("安装了:" + packageName + "包名的程序");
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {

            if (intent.getDataString().contains("com.tongyuan.android.zhiquleyuan")) {
                Intent myIntent = new Intent();
                PackageManager pm = context.getPackageManager();
                try {
                    myIntent = pm.getLaunchIntentForPackage(intent.getDataString().substring(8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
                context.unregisterReceiver(this);
            }
            String packageName = intent.getDataString();
            System.out.println("卸载了:" + packageName + "包名的程序");
        }
    }

}
