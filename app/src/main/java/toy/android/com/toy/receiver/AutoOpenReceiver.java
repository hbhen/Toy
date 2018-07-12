package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import toy.android.com.toy.activity.MainActivity;
import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.ToastUtil;
@Deprecated
public class AutoOpenReceiver extends BroadcastReceiver {
    //    private static final String TAG = AutoOpenReceiver.class.getSimpleName();
    public static final String TAG = "updateservice(auto)";

    //|| intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent == null) {
//            ToastUtil.showToast(context, "onReceive: (AutoOpenReceiver)intent 为空");
//            Log.i(TAG, "onReceive: (AutoOpenReceiver)intent 为空");
//            return;
//        } else {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
//                ToastUtil.showToast(context, "onReceive: (AutoOpenReceiver)收到通知 : " + intent.getAction().toString());
            LogUtil.i(TAG, "onReceive: begin");
            Intent intent1 = new Intent();
//            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("toy.android.com.toy");
//            launchIntentForPackage.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            launchIntentForPackage.setClass(context, MainActivity.class);
//            launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(launchIntentForPackage);
//            context.startActivity(launchIntentForPackage);
            intent1.setClass(context, MainActivity.class);
            intent1.setAction(Intent.ACTION_MAIN);
            intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            ToastUtil.showToast(context, "onReceive: (AutoOpenReceiver)intent 不为空,准备启动");
            Log.i(TAG, "onReceive(AutoOpenReceiver): context:" + context.toString());
            Log.i(TAG, "onReceive: (AutoOpenReceiver):intent:" + intent.toString());
            context.startActivity(intent1);
        }
        Log.d(TAG, "onReceive: over");
    }
//    }
}
