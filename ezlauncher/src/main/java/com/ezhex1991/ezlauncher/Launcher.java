package com.ezhex1991.ezlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class Launcher {
    private Activity activity;
    private LinearLayout navigator;
    private LinearLayout.LayoutParams navigatorParams;
    private Button button_AppList;

    public Launcher(Activity activity) {
        this.activity = activity;
    }

    public void showNavigator() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        navigator = (LinearLayout) inflater.inflate(R.layout.layout_bottom_navigator, null);
        button_AppList = navigator.findViewById(R.id.button_app_list);
        button_AppList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AppListActivity.class);
                activity.startActivity(intent);
            }
        });
        navigatorParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        navigatorParams.bottomMargin = 0;
        navigatorParams.gravity = Gravity.BOTTOM;
        activity.addContentView(navigator, navigatorParams);
    }
}
