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
import android.view.accessibility.AccessibilityEvent;

import java.io.File;

import toy.android.com.toy.activity.InstallUpdate;
import toy.android.com.toy.utils.DeleteFileUtil;
import toy.android.com.toy.utils.LogUtil;
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
//  @Dtc 2018/07/08 更改内容： 判断文件是否存在，如果存在就删除文件，然后再去下载。
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand: 走?");
        String url = intent.getStringExtra("updateurl");
        LogUtil.i(TAG, "onStartCommand: url是多少：" + url);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(path + File.separator + "toy.apk");
        if (file.exists()) {
//            如果存在就删除，然后重新下载
            DeleteFileUtil.deleteFile(file.toString());
            LogUtil.i(TAG, "onStartCommand: true");

//            File absoluteFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
//            String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//            try {
//                File canonicalFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalFile();
//                String canonicalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
//                LogUtil.i(TAG, "onStartCommand canonicalFile:" + canonicalFile);
//                LogUtil.i(TAG, "onStartCommand canonicalPath:" + canonicalPath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String parent = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParent();
//            File parentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile();
//            LogUtil.i(TAG, "onStartCommand: path:" + path);
//            LogUtil.i(TAG, "onStartCommand absoluteFile: " + absoluteFile);
//            LogUtil.i(TAG, "onStartCommand absolutePath: " + absolutePath);
//            LogUtil.i(TAG, "onStartCommand parent: " + parent);
//
//            LogUtil.i(TAG, "onStartCommand parentFile: " + parentFile);
//            Intent intentNew = new Intent();
//            intentNew.setClass(AppUpdateService.this, InstallUpdate.class);
//            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            AppUpdateService.this.startActivity(intentNew);
//            stopSelf();

        }
//        else {
//            LogUtil.i(TAG, "onStartCommand: false");
//        }
        downloadApk(url);
//            LogUtil.i(TAG, "onStartCommand: url" + url);
        return START_REDELIVER_INTENT;
    }

    private void downloadApk(String updateurl) {
        mDm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateurl));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "toy.apk");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        LogUtil.i(TAG, "downloadApk: publicdir:" + Environment.DIRECTORY_DOWNLOADS);
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
                    LogUtil.i(TAG, "onReceive: 下载完成,跳到安装的步骤");
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