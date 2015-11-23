package com.aplications.glp.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by ´ñ´b on 22/11/2015.
 */
public class FileManager {

    private static String TAG = "FileManager";
    private String appAlbumName = "GLP";

    static public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    static public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }
}
