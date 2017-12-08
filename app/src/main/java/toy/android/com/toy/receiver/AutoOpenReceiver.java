package toy.android.com.toy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import toy.android.com.toy.activity.MainActivity;
import toy.android.com.toy.utils.ToastUtil;

public class AutoOpenReceiver extends BroadcastReceiver {
    private static final String TAG = "tag";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ToastUtil.showToast(context, "启动成功");
            Log.d(TAG, "onReceive: begin");
            Intent intent1 = new Intent();
            intent1.setClass(context, MainActivity.class);
            intent1.setAction(Intent.ACTION_MAIN);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            Log.d(TAG, "onReceive: over");
        }
    }
}
