package toy.android.com.toy.service;

import android.accessibilityservice.AccessibilityService;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

import toy.android.com.toy.activity.Jump;
import toy.android.com.toy.utils.ToastUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppUpdateService extends AccessibilityService {

    private static final String TAG = "updateservice";
    private long mDownloadId;
    private DownloadManager mDm;
    private long mEnqueue;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {

    }
//    public AppUpdateService() {
//        super("AppUpdateService");
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: 走?");
        String url = intent.getStringExtra("updateurl");
//        url = "http://192.168.1.107:8080/update/toy2.0.apk";
        downloadApk(url);
        Log.i(TAG, "onStartCommand: url"+url);
        return START_REDELIVER_INTENT;
    }

    private void downloadApk(String updateurl) {
        mDm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateurl));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mEnqueue = mDm.enqueue(request);
        linstener(mEnqueue);

    }

    private void linstener(final long enqueue) {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == enqueue) {
                    ToastUtil.showToast(AppUpdateService.this, "任务:" + enqueue + "下载完成!");
                    Intent intentNew = new Intent();
                    intentNew.setClass(AppUpdateService.this, Jump.class);
                    intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppUpdateService.this.startActivity(intentNew);
                    stopSelf();
                }
            }
        };
        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopSelf();
    }

    public class InstallBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                installApk(AppUpdateService.this, mDownloadId);
                installSlient();
            }
        }
    }

    private void installSlient() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String packageName = "toy.android.com.toy";
        String packageName = "com.tongyuan.android.zhiquleyuan";
        String cmd = "pm install -l " + packageName + " -r " + absolutePath + File.separator + "update.apk";// + absolutePath + File.separator +
        // "update.apk"
        Log.i(TAG, "installSlient: cmd" + cmd);
        java.lang.Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "installSlient: 成功的消息:" + successMsg.toString() + "\n" + "错误消息: " + errorMsg.toString());
    }
}