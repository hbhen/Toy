package toy.android.com.toy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by DTC on 2018/2/8.
 */

public class UpdateManager {
    private Context mContext;
    private String apkUrl = "";
    private static final String savePath = File.separator + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static final String saveFileName = savePath + "update.apk";

    private ProgressBar mProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕

    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位

    public UpdateManager(Activity context, String url) {
        this.mContext = context;
        this.apkUrl = url;
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = info.versionName;
    }


}
