package toy.android.com.toy.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by DTC on 2018/7/8.
 */

public class DeleteApkUtils {
    public static File getPathFile(String path){
        String apkName = path.substring(path.lastIndexOf("/"));
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), apkName);
        return outputFile;
    }

    public static void rmoveFile(String path){
        File file = getPathFile(path);
        file.delete();
    }
    public static void removeApk(){
        File file=new File(Environment.DIRECTORY_DOWNLOADS);
        File[] files = file.listFiles();
        for (File str:files) {
            String name = str.getName();
            if (name.contains("toy")){
                file.delete();
            }
        }

    }
}
