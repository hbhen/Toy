package toy.android.com.toy.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 00005001 on 2017/3/1.
 */

public class UpdateManager1 {
    private Activity mContext; //上下文

    private String apkUrl = "";  //apk下载地址
    //    private static final String savePath = "/sdcard/updateAPK/"; //apk保存到SD卡的路径
    private static final String savePath = "/" + Environment.getExternalStorageDirectory() + "/updateAPK/"; //apk保存到SD卡的路径
    private static final String saveFileName = savePath + "apkName.apk"; //完整路径名

    private ProgressBar mProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位

    private String serverVersion = "1.0"; //从服务器获取的版本号
    private String clientVersion = "0.0"; //客户端当前的版本号
    private String updateDescription = "";//"提示：如不更新将无法正常使用"; //更新内容描述信息
    private boolean forceUpdate = true; //是否强制更新
    private String content;

    private AlertDialog alertDialog1, alertDialog2; //表示提示对话框、进度条对话框
    private AlertDialog.Builder dialog;

    /**
     * 构造函数
     */
    public UpdateManager1(Activity context, String versionStr, String urlStr, Boolean forceUpdateBool, String content) {
        this.mContext = context;
        this.serverVersion = versionStr;
        this.apkUrl = urlStr;
        this.forceUpdate = forceUpdateBool;
        this.content = content;
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        clientVersion = info.versionName;
    }

    /**
     * 显示更新对话框
     */
    public void showNoticeDialog() {
        //如果版本最新，则不需要更新
        if (serverVersion == null || serverVersion.equals(clientVersion)) {
            return;
        }

//        //是否强制更新

        alertDialog1 = dialog.create();
        alertDialog1.setCancelable(false);
        alertDialog1.show();
    }


    /**
     * 显示进度条对话框
     */

    /**
     * 下载apk的线程
     */
    public void downloadAPK() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
//                    LogUtil.e("conn.getContentLength()=" + conn.getContentLength());
                    int length = conn.getContentLength();
//                    LogUtil.e("length=" + length);
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String apkFile = saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
//                        LogUtil.e("numread=" + numread);

                        count += numread;
//                        LogUtil.e("count=" + count);
//                        LogUtil.e("length=" + length);

                        progress = (int) (((float) count / length) * 100);
//                        LogUtil.e("progress=" + progress);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWNLOADING);
                        if (numread <= 0) {
                            //下载完成通知安装
                            mHandler.sendEmptyMessage(DOWNLOADED);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelFlag); //点击取消就停止下载.

                    fos.close();
                    is.close();
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新UI的handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case DOWNLOADING:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOADED:
                    if (alertDialog2 != null)
                        alertDialog2.dismiss();
                    installAPK();
                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(mContext, "网络断开，请稍候再试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 下载完成后自动安装apk
     */
    public void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
//        mContext.startActivity(intent);
        //String fileName = cursor.getString(fileNameIdx);//承接我的代码，filename指获取到了我的文件相应路径
        if (saveFileName != null) {
            if (saveFileName.endsWith(".apk")) {
                if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                    File file = new File(saveFileName);
                    Uri apkUri = FileProvider.getUriForFile(mContext, "com.jph.takephoto.fileprovider", file);//在AndroidManifest中的android:authorities值
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    mContext.startActivity(install);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(new File(saveFileName)), "application/vnd.android.package-archive");
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(install);
                }
            }
        }
    }


}
