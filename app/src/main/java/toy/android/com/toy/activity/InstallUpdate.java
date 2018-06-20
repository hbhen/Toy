package toy.android.com.toy.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

import toy.android.com.toy.receiver.InstallBroadcastReceiver;

/**
 * Created by DTC on 2018/1/2511:59.
 */
public class InstallUpdate extends AppCompatActivity {
    private static final String TAG = "updateservice";
    private Button mMiaozhuang;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "toy.apk");
        String url = file.toString();
        boolean root = isRoot();
        Log.i(TAG, "onCreate: 是否获得root?: " + root);
//        onSilentInstall(url);
        //开始安装
        installSlient(url);
//        installApk(url);
    }

    public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    public void onSilentInstall(final String apkPath) {
        if (!isRoot()) {
            Toast.makeText(this, "没有ROOT权限，不能使用秒装", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "onSilentInstall: apkPath:" + apkPath);
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SilentInstall installHelper = new SilentInstall();
                Log.i(TAG, "run: result:" + apkPath);
                final boolean result = installHelper.install(apkPath);
                Log.i(TAG, "run: result:" + result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            Log.i(TAG, "run: " + true);
                            IntentFilter intentFilter = new IntentFilter();
//                            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
//                            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
                            InstallBroadcastReceiver installBroadcastReceiver = new InstallBroadcastReceiver();

                            InstallUpdate.this.registerReceiver(installBroadcastReceiver, intentFilter);
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator +
                                    "toy.apk");
                            if (file.exists()) {
                                file.delete();
                                Log.i(TAG, "run: 删除文件");
                            }
                        } else {
                            Log.i(TAG, "run: " + false);
                        }
                    }
                });
            }
        }).start();
    }

//    private void installApk(String url) {
//        Log.i(TAG, "开始执行安装: " + url);
//        File apkFile = new File(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(mContext, "你的包名.fileprovider", apkFile);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            Log.w(TAG, "正常进行安装");
//            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//        }
//        startActivity(intent);
//    }

    //没有权限 不能自动安装
    private void installSlient(String url) {
        String packageName = "toy.android.com.toy";
        String cmd = "pm install -r " + url + "\n";
//        String cmd = "pm install -l " + packageName + " -r " + absolutePath + File.separator + "toy.apk";// + absolutePath + File.separator +
        // "update.apk"
        Log.i(TAG, "installSlient: cmd - " + cmd);
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
            android.os.Process.killProcess(android.os.Process.myPid());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
