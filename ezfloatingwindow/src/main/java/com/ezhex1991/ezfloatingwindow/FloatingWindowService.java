package com.ezhex1991.ezfloatingwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    private Intent m_ActivityIntent;

    private WindowManager m_WindowManager;

    private LinearLayout m_FloatingWindow;
    private WindowManager.LayoutParams m_FloatingWindowParams;
    private Button button;

    private int startX = 0, startY = 500;

    public class Binder extends android.os.Binder {
        public FloatingWindowService getService() {
            return FloatingWindowService.this;
        }
    }

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
        // do not block touch event
        m_FloatingWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        m_FloatingWindowParams.format = PixelFormat.TRANSLUCENT; // set background to transparent
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

        button = m_FloatingWindow.findViewById(R.id.button);
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
                            } else {
                                Toast.makeText(FloatingWindowService.this, "Null", Toast.LENGTH_SHORT).show();
                            }
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        getIntentData(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        getIntentData(intent);
        return new Binder();
    }

    @Override
    public void onDestroy() {
        if (m_FloatingWindow != null)
            m_WindowManager.removeView(m_FloatingWindow);
        super.onDestroy();
    }

    public boolean getIntentData(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            this.stopSelf();
            return false;
        } else {
            String className = intent.getExtras().getString("className");

            String text = intent.getExtras().getString("text");
            if (text != null && !text.isEmpty()) {
                setText(text);
            }

            byte[] imageBytes = intent.getExtras().getByteArray("imageBytes");
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                setBackground(drawable);
            }

            try {
                m_ActivityIntent = new Intent(this, Class.forName(className));
                m_ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            } catch (ClassNotFoundException e) {
                m_ActivityIntent = null;
                Log.e(TAG, "Class name from intent extra is invalid");
            }
            return true;
        }
    }

    public void setText(String text) {
        if (button != null)
            button.setText(text);
    }

    public void setBackground(Drawable background) {
        if (button != null)
            button.setBackground(background);
    }
}
