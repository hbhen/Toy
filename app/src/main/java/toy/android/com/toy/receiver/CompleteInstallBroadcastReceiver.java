package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import toy.android.com.toy.activity.MainActivity;
import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.ToastUtil;

public class CompleteInstallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "updateservice";

    public CompleteInstallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive(CompleteInstallBroadcastReceiver): " + intent.getAction());
        //接收安装广播
        if (intent == null) {
            Log.i(TAG, "onReceive: intent 为空!");
            ToastUtil.showToast(context, "intent为空");
            return;
        } else {
            Log.i(TAG, "onReceive: intent不为空!");
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String packageName = intent.getDataString();
            Log.i(TAG, "onReceive: replaced");
            ToastUtil.showToast(context, "监听到系统广播移除,安装完成");
            if (packageName.contains("toy.android.com.toy")) {
                Log.i(TAG, "onReceive: context的状态是否为null:::" + context.toString());
                PackageManager pm = context.getPackageManager();
                Log.i(TAG, "onReceive: pm是否为空?:" + pm.toString());
                Log.i(TAG, "onReceive: (intent.getDataString):" + intent.getDataString());
//                intent = pm.getLaunchIntentForPackage(intent.getDataString());

                //这个地方获取的时候为空了  为什么为空?  pm不为空
                if (intent == null) {
                    Log.i(TAG, "onReceive: intent 为空");
                    return;
                } else {

                    Log.i(TAG, "onReceive: intent的值是多少??1111:" + intent.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, MainActivity.class);
                    Log.i(TAG, "onReceive: intent的值是多少??2222:" + intent.toString());
                    context.startActivity(intent);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                context.unregisterReceiver(this);
                    ToastUtil.showToast(context, "安装了 " + packageName + "包名的程序");
                    Log.i(TAG, "onReceive: 安装了: " + packageName + "包名的程序");
//                    DeleteApkUtils.removeApk();
                    LogUtil.i(TAG,"进行了删除apk的操作");
                }
            }
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            ToastUtil.showToast(context, "监听到系统广播移除");
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            ToastUtil.showToast(context, "监听到系统广播添加");
        }
        //接收卸载广播
//        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
//            Log.i(TAG, "onReceive: replaced");
//            if (intent.getDataString().contains("toy.android.com.toy")) {
//                Intent myIntent = new Intent();
//                PackageManager pm = context.getPackageManager();
//                try {
//                    myIntent = pm.getLaunchIntentForPackage(intent.getDataString().substring(8));
//                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(myIntent);
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    context.unregisterReceiver(this);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            String packageName = intent.getDataString();
//            Log.i(TAG, "onReceive(CompleteInstallBroadcastReceiver):卸载了: " + packageName + "包名的程序");
//            System.out.println("卸载了:" + packageName + "包名的程序");
//        }

    }

}
