package com.ezhex1991.ezfloatingwindow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class FloatingWindow {
    public static final String TAG = "FloatingWindow";
    public static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] drawableToBytes(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private Activity activity;
    private Intent floatingWindowIntent;
    private FloatingWindowService floatingWindowService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            floatingWindowService = ((FloatingWindowService.Binder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            floatingWindowService = null;
        }
    };

    public FloatingWindow(Activity activity) {
        this.activity = activity;
        this.floatingWindowIntent = new Intent(activity, FloatingWindowService.class);
        floatingWindowIntent.putExtra("className", activity.getClass().getName());
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return Settings.canDrawOverlays(activity.getApplicationContext());
        }
    }

    public void getPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    public void startService() {
        if (checkPermission()) {
            activity.startService(floatingWindowIntent);
        } else {
            getPermission();
        }
    }

    public void stopService() {
        activity.stopService(floatingWindowIntent);
    }

    public void bindService() {
        activity.bindService(floatingWindowIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        activity.unbindService(serviceConnection);
    }

    public void setText(final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloatingWindowService.setText(text);
            }
        });
    }

    public void setBackground(final byte[] imageBytes) {
        setBackground(bytesToDrawable(imageBytes));
    }

    public void setBackground(final Drawable background) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloatingWindowService.setBackground(background);
            }
        });
    }

    public Bitmap bytesToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public Drawable bytesToDrawable(byte[] imageBytes) {
        return new BitmapDrawable(activity.getResources(), bytesToBitmap(imageBytes));
    }
}
