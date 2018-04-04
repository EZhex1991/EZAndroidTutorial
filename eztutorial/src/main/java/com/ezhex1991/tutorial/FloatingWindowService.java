package com.ezhex1991.tutorial;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingWindowService extends Service {
    private static final String TAG = "FloatingWindowService";

    private WindowManager m_WindowManager;
    private LinearLayout m_FloatingWindow;
    private WindowManager.LayoutParams m_FloatingWindowParams;

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
        Button button = m_FloatingWindow.findViewById(R.id.floating_window_button);
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
                            Intent intent = new Intent(FloatingWindowService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            startActivity(intent);
                            Toast.makeText(FloatingWindowService.this, "Jump to EZTutorial", Toast.LENGTH_SHORT).show();
                            v.performClick();
                        }
                        break;
                }
                return false;
            }
        };
        button.setOnTouchListener(buttonListener);
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
}
