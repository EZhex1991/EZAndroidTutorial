package com.ezhex1991.tutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastTestActivity extends AppCompatActivity {
    private Button button_GeneralToast;
    private Button button_GeneralToastWithCoordinates;
    private Button button_CustomViewToast;
    private Button button_CustomLayoutToast;
    private Button button_Quit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_test);

        button_GeneralToast = findViewById(R.id.toast_general);
        button_GeneralToastWithCoordinates = findViewById(R.id.toast_coordinated_general);
        button_CustomViewToast = findViewById(R.id.toast_custom_view);
        button_CustomLayoutToast = findViewById(R.id.toast_custom_layout);
        button_Quit = findViewById(R.id.button_quit_toast_test);

        button_GeneralToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("General Toast").show();
            }
        });
        button_GeneralToastWithCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("General Toast With Coordinates", 0, -100).show();
            }
        });
        button_CustomViewToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast(R.drawable.ic_launcher_foreground, "Custom Toast").show();
            }
        });
        button_CustomLayoutToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View toastLayout = inflater.inflate(R.layout.custom_toast, null);
                ImageView image = toastLayout.findViewById(R.id.custom_toast_image);
                TextView title = toastLayout.findViewById(R.id.custom_toast_title);
                TextView text = toastLayout.findViewById(R.id.custom_toast_text);
                image.setImageResource(R.drawable.ic_launcher_foreground);
                title.setText(R.string.toast);
                text.setText(R.string.custom_layout_toast);
                makeToast(toastLayout).show();
            }
        });
        button_Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Toast makeToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        return toast;
    }

    public Toast makeToast(String text, int x, int y) {
        Toast toast = makeToast(text);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, x, y);
        return toast;
    }

    public Toast makeToast(int resourceId, String text) {
        Toast toast = makeToast(text);
        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageResource(resourceId);
        LinearLayout toastView = (LinearLayout) toast.getView();
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        toastView.addView(iv, 0);
        return toast;
    }

    public Toast makeToast(View v) {
        Toast toast = new Toast(getApplicationContext());
        toast.setView(v);
        return toast;
    }
}
