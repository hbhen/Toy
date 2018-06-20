package toy.android.com.toy.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

/**
 * Created by DTC on 2018/1/2511:59.
 */
public class Jump extends AppCompatActivity{
    private static final String TAG = "updateservice";
    private Button mMiaozhuang;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "toy.apk");
        String url = file.toString();
        onSilentInstall(url);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            Log.i(TAG, "run: " + true);
                        } else {
                            Log.i(TAG, "run: " + false);
                        }
                    }
                });

            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
