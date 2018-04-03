package com.ezhex1991.tutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BundleTestActivity extends AppCompatActivity {
    private TextView text_BundleInfo;
    private Button button_Quit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle_test);

        Bundle bundle = this.getIntent().getExtras();
        String testText = bundle.getString("testText");
        String resolution = bundle.getString("resolution");
        String checkboxSelections = bundle.getString("checkboxSelections");
        String radioSelection = bundle.getString("radioSelection");

        text_BundleInfo = findViewById(R.id.text_bundle_info);
        text_BundleInfo.setText(testText + "\n" + resolution + "\n" + checkboxSelections + "\n" + radioSelection);

        button_Quit = findViewById(R.id.button_quit_bundle_test);
        button_Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
