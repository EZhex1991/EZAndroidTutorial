package com.ezhex1991.floatingwindow;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingViewService extends Service implements View.OnTouchListener {
    WindowManager mWindowManager;

    LinearLayout mLayout;
    WindowManager.LayoutParams mLayoutParams;
    Button mView;

    boolean moved = false;

    private static final String TAG = "FloatingViewService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLayout != null) {
            mWindowManager.removeView(mLayout);
        }
    }

    private void createFloatView() {
        // get WindowManager to add floating view
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        // create layout params to show floating view
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888; // set background to transparent
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // can not focus to this view
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP; // not working...

        // start position
        mLayoutParams.x = 0;
        mLayoutParams.y = 500;

        // set view size to content size (equals to the button)
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // load layout dynamically
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mLayout = (LinearLayout) inflater.inflate(R.layout.floating_layout, null);

        // add view
        mWindowManager.addView(mLayout, mLayoutParams);
        // get added view
        mView = (Button) mLayout.findViewById(R.id.floating_id);

        // not understand
        /*
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        */

        // set listener for the view, which is declared as Button above
        mView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // move the view
                mLayoutParams.x = (int) event.getRawX() - v.getMeasuredWidth() / 2;
                mLayoutParams.y = (int) event.getRawY() - v.getMeasuredHeight() / 2 - 25;
                mWindowManager.updateViewLayout(mLayout, mLayoutParams);
                moved = true;
                break;
            case MotionEvent.ACTION_UP:
                // not moved, handle as click
                if (!moved) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    Toast.makeText(FloatingViewService.this, "onClick", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        // not understand
        return false;
    }
}
