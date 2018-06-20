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

import java.io.File;
import java.io.IOException;

import toy.android.com.toy.activity.InstallUpdate;
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
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(path + File.separator + "toy.apk");
        if (file.exists()) {
            Log.i(TAG, "onStartCommand: true");
            File absoluteFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
            String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            try {
                File canonicalFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalFile();
                String canonicalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
                Log.i(TAG, "onStartCommand canonicalFile:" + canonicalFile);
                Log.i(TAG, "onStartCommand canonicalPath:" + canonicalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String parent = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParent();
            File parentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile();
            Log.i(TAG, "onStartCommand: path:" + path);
            Log.i(TAG, "onStartCommand absoluteFile: " + absoluteFile);
            Log.i(TAG, "onStartCommand absolutePath: " + absolutePath);
            Log.i(TAG, "onStartCommand parent: " + parent);

            Log.i(TAG, "onStartCommand parentFile: " + parentFile);
            Intent intentNew = new Intent();
            intentNew.setClass(AppUpdateService.this, InstallUpdate.class);
            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppUpdateService.this.startActivity(intentNew);
            stopSelf();

        } else {
            Log.i(TAG, "onStartCommand: false");
            downloadApk(url);
            Log.i(TAG, "onStartCommand: url" + url);
        }

        return START_REDELIVER_INTENT;
    }

    private void downloadApk(String updateurl) {
        mDm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateurl));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "toy.apk");
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
                    intentNew.setClass(AppUpdateService.this, InstallUpdate.class);
                    intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppUpdateService.this.startActivity(intentNew);
                    Log.i(TAG, "onReceive: 下载完成,跳到安装的步骤");
                    stopSelf();
                }
            }
        };

        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        stopSelf();
    }


}