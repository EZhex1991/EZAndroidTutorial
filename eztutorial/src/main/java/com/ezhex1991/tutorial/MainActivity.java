package com.ezhex1991.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ezhex1991.ezfloatingwindow.FloatingWindow;
import com.ezhex1991.ezlauncher.Launcher;

public class MainActivity extends AppCompatActivity {
    private static final String PREFERENCE_KEY_FLOATING_WINDOW = "floating_window_enabled";

    private FloatingWindow floatingWindow;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;

    private TextView testText;
    private Button testButton;

    private TextView text_Resolution;
    private Button button_GetResolution;

    private TextView text_CheckBoxSelections;
    private CheckBox[] checkboxList;

    private TextView text_RadioSelection;
    private RadioButton[] radioButtonList;

    private boolean floatingWindowEnabled = true;
    private ToggleButton toggle_FloatingWindow;

    private Button button_SetForeground;
    private Button button_SetBackground;

    private Button button_Submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("prefer", MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();

        testText = findViewById(R.id.text_test);
        testButton = findViewById(R.id.button_test);
        testButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Clicked", Toast.LENGTH_LONG).show();
                testText.setText("Text Changed");
            }
        });

        text_Resolution = findViewById(R.id.text_resolution);
        button_GetResolution = findViewById(R.id.button_get_resolution);
        text_Resolution.setText("Resolution: ");
        button_GetResolution.setText(R.string.button_get_resolution);
        button_GetResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                text_Resolution.setText(getString(R.string.resolution, dm.widthPixels, dm.heightPixels));
            }
        });

        text_CheckBoxSelections = findViewById(R.id.checkbox_selections);
        checkboxList = new CheckBox[2];
        checkboxList[0] = findViewById(R.id.checkbox_1);
        checkboxList[1] = findViewById(R.id.checkbox_2);
        CheckBox.OnCheckedChangeListener checkboxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = getText(R.string.checkbox_selections).toString();
                for (CheckBox cb : checkboxList) {
                    if (cb.isChecked()) {
                        str += cb.getText() + ", ";
                    }
                }
                text_CheckBoxSelections.setText(str);
            }
        };
        for (CheckBox cb : checkboxList) {
            cb.setOnCheckedChangeListener(checkboxListener);
        }

        text_RadioSelection = findViewById(R.id.radio_selection);
        radioButtonList = new RadioButton[2];
        radioButtonList[0] = findViewById(R.id.radiobutton_1);
        radioButtonList[1] = findViewById(R.id.radiobutton_2);
        RadioButton.OnClickListener radiobuttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                text_RadioSelection.setText(getString(R.string.radio_selection) + rb.getText());
            }
        };
        for (RadioButton rb : radioButtonList) {
            rb.setOnClickListener(radiobuttonListener);
        }

        floatingWindow = new FloatingWindow(this);
        toggle_FloatingWindow = findViewById(R.id.toggle_floating_window);
        toggle_FloatingWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!floatingWindow.checkPermission()) {
                    floatingWindowEnabled = false;
                    floatingWindow.getPermission();
                } else {
                    floatingWindowEnabled = isChecked;
                }
                preferenceEditor.putBoolean(PREFERENCE_KEY_FLOATING_WINDOW, floatingWindowEnabled).apply();
                if (floatingWindowEnabled) {
                    floatingWindow.bindService();
                } else {
                    floatingWindow.unbindService();
                }
            }
        });
        toggle_FloatingWindow.setChecked(sharedPreferences.getBoolean(PREFERENCE_KEY_FLOATING_WINDOW, floatingWindowEnabled));

        button_SetForeground = findViewById(R.id.button_set_floating_window_foreground);
        button_SetForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingWindow.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_foreground, null));
            }
        });
        button_SetBackground = findViewById(R.id.button_set_floating_window_background);
        button_SetBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingWindow.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null));
            }
        });

        button_Submit = findViewById(R.id.button_submit);
        button_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("testText", testText.getText().toString());
                bundle.putString("resolution", text_Resolution.getText().toString());
                bundle.putString("checkboxSelections", text_CheckBoxSelections.getText().toString());
                bundle.putString("radioSelection", text_RadioSelection.getText().toString());
                Intent intent = new Intent(MainActivity.this, BundleTestActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Launcher launcher = new Launcher(this);
        launcher.showNavigator();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        floatingWindow.setText(String.format("%1d, %2d", (int) event.getRawX(), (int) event.getRawY()));
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatingWindowEnabled) {
            floatingWindow.bindService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatingWindowEnabled) {
            floatingWindow.unbindService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FloatingWindow.OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!floatingWindow.checkPermission()) {
                Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION is necessary for floating window service", Toast.LENGTH_SHORT).show();
                toggle_FloatingWindow.setChecked(false);
            } else {
                floatingWindow.bindService();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Toast Test");
        menu.add(0, 1, 1, "Quit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this, ToastTestActivity.class);
                startActivity(intent);
                break;
            case 1:
                this.finish();
                break;
        }
        return true;
    }
}
