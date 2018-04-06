package com.ezhex1991.ezstreamingloader;

import android.app.Activity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamingLoader {
    private static final String TAG = "StreamingLoader";

    private static byte[] loadFile(Activity activity, String path) {
        InputStream inputStream = null;

        try {
            inputStream = activity.getAssets().open(path);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        try {
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return outputStream.toByteArray();
    }
}