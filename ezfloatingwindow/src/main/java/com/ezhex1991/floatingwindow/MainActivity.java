package com.ezhex1991.floatingwindow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    public static Intent floatingViewService;
    public boolean floatingEnabled = true;

    private boolean needPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request overlay permission
        // necessary for targetSdkVersion 23 or higher and device Android version 6.0 or higher
        if (needPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        }

        Button start = (Button) findViewById(R.id.start_id);
        Button remove = (Button) findViewById(R.id.remove_id);

        floatingViewService = new Intent(MainActivity.this, FloatingViewService.class);
        // onClick for start button
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingEnabled = true;
                startService(floatingViewService);
                // floating window is running by service, destroy activity won't remove the floating window
                // finish();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingEnabled = false;
                stopService(floatingViewService);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatingEnabled)
            startService(floatingViewService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatingEnabled)
            startService(floatingViewService);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // raise a toast if permission has been denied
        if (needPermission()) {
            Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION has been denied", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
