package com.ezhex1991.ezlauncher;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
        navigatorParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        activity.addContentView(navigator, navigatorParams);

        navigator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                activity.onTouchEvent(event);
                return false;
            }
        });
    }
}
