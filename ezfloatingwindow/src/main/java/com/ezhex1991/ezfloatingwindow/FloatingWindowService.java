package com.ezhex1991.ezfloatingwindow;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingWindowService extends Service {
    public static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "FloatingWindowService";

    private WindowManager m_WindowManager;
    private LinearLayout m_FloatingWindow;
    private WindowManager.LayoutParams m_FloatingWindowParams;

    private Button m_Button;
    private Intent m_ActivityIntent;

    private int startX = 0, startY = 500;

    @Override
    public void onCreate() {
        super.onCreate();
        // get WindowManager to add floating window
        m_WindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

        // create layout params to show floating window
        m_FloatingWindowParams = new WindowManager.LayoutParams(); // different to ViewGroup.LayoutParams
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            m_FloatingWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            m_FloatingWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; // TYPE_PHONE is deprecated
        }
        m_FloatingWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // can not focus to this window
        m_FloatingWindowParams.format = PixelFormat.RGBA_8888; // set background to transparent
        // set window size to content size
        m_FloatingWindowParams.width = m_FloatingWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // set start position
        m_FloatingWindowParams.x = startX;
        m_FloatingWindowParams.y = startY;

        // load layout dynamically
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        m_FloatingWindow = (LinearLayout) inflater.inflate(R.layout.floating_window, null);

        // add view
        m_WindowManager.addView(m_FloatingWindow, m_FloatingWindowParams);
        // get added view
        m_Button = m_FloatingWindow.findViewById(R.id.button);
        View.OnTouchListener buttonListener = new View.OnTouchListener() {
            private int currentX, currentY;
            private int eventX, eventY;
            private int deltaX, deltaY;
            private int moveThreshold = 10;
            private boolean isMoved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                eventX = (int) event.getRawX();
                eventY = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentX = startX = eventX;
                        currentY = startY = eventY;
                        deltaX = deltaY = 0;
                        isMoved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        deltaX = eventX - currentX;
                        deltaY = eventY - currentY;
                        Log.d(TAG, String.format("onTouch: %1d, %2d", deltaX, deltaY));
                        if (Math.abs(currentX - startX) > moveThreshold || Math.abs(currentY - startY) > moveThreshold) {
                            m_FloatingWindowParams.x += deltaX;
                            m_FloatingWindowParams.y += deltaY;
                            m_WindowManager.updateViewLayout(m_FloatingWindow, m_FloatingWindowParams);
                            isMoved = true;
                        }
                        currentX = eventX;
                        currentY = eventY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMoved) {
                            if (m_ActivityIntent != null) {
                                startActivity(m_ActivityIntent);
                                Toast.makeText(FloatingWindowService.this, "Jump to " + m_ActivityIntent.getComponent().getClassName(), Toast.LENGTH_SHORT).show();
                            }
                            v.performClick();
                        }
                        break;
                }
                return false;
            }
        };
        m_Button.setOnTouchListener(buttonListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        m_Button.setText(intent.getExtras().getString("text"));
        String className = intent.getExtras().getString("className");
        Log.d(TAG, "onStartCommand: " + className);
        try {
            m_ActivityIntent = new Intent(this, Class.forName(className));
            m_ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        } catch (ClassNotFoundException e) {
            m_ActivityIntent = null;
            Log.e(TAG, "onStartCommand: class name from intent extra is invalid");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (m_FloatingWindow != null)
            m_WindowManager.removeView(m_FloatingWindow);
        super.onDestroy();
    }


    public static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return Settings.canDrawOverlays(activity.getApplicationContext());
        }
    }

    public static void getPermission(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    public static Intent getServiceIntent(Activity activity, String text) {
        Intent floatingWindowService = new Intent(activity, FloatingWindowService.class);
        floatingWindowService.putExtra("text", text);
        floatingWindowService.putExtra("className", activity.getClass().getName());
        return floatingWindowService;
    }

    public static void startService(Activity activity, String text) {
        activity.startService(getServiceIntent(activity, text));
    }

    public static void stopService(Activity activity) {
        activity.stopService(getServiceIntent(activity, ""));
    }
}
