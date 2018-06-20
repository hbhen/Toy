package toy.android.com.toy.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by DTC on 2018/4/17.
 */

public class CommUtils {
    public static File getPathFile(String path) {
        String apkName = path.substring(path.lastIndexOf("/"));
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), apkName);
        return outputFile;
    }
    public static void removeFile(String path){
        File file=getPathFile(path);
        file.delete();
    }
}
