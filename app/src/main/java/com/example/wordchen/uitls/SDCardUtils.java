package com.example.wordchen.uitls;

import android.os.Environment;

public class SDCardUtils {

    public static boolean getStorageState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
