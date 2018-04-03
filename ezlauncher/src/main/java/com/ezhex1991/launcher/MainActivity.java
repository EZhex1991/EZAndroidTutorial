package com.ezhex1991.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Launcher";

    private GridView appListView;
    private List<ResolveInfo> resolveInfoList;
    private ArrayList<HashMap<String, Object>> appInfoList = new ArrayList<>();

    private AdapterView.OnItemClickListener appListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> appInfo = appInfoList.get(position);
            String packageName = (String) appInfo.get("PackageName");
            String className = (String) appInfo.get("ClassName");
            ComponentName component = new ComponentName(packageName, className);
            Intent intent = new Intent();
            intent.setComponent(component);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadApps();
        appListView = (GridView) findViewById(R.id.app_list);
//        appListView.setAdapter(new SimpleAdapter(
//                this,
//                appInfoList,
//                R.layout.app_item,
//                new String[]{"Icon", "Name"},
//                new int[]{R.id.app_icon, R.id.app_name}));
        appListView.setAdapter(new AppListAdapter());
        appListView.setOnItemClickListener(appListListener);
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveInfoList = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo ri : resolveInfoList) {
            if (ri.activityInfo == null) continue;
            HashMap<String, Object> appInfo = new HashMap<>();
            appInfo.put("Icon", ri.activityInfo.loadIcon(getPackageManager()));
            appInfo.put("Name", ri.activityInfo.loadLabel(getPackageManager()));
            appInfo.put("PackageName", ri.activityInfo.packageName);
            appInfo.put("ClassName", ri.activityInfo.name);
            appInfoList.add(appInfo);
        }
    }

    public class AppListAdapter extends BaseAdapter {
        private LinearLayout itemLayout;
        private ViewGroup.LayoutParams itemViewParams;
        private ViewGroup.LayoutParams iconViewParams;
        private ViewGroup.LayoutParams nameViewParams;

        public AppListAdapter() {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            itemLayout = (LinearLayout) inflater.inflate(R.layout.app_item, appListView, false);
            itemViewParams = itemLayout.getLayoutParams();
            iconViewParams = itemLayout.findViewById(R.id.app_icon).getLayoutParams();
            nameViewParams = itemLayout.findViewById(R.id.app_name).getLayoutParams();
        }

        public View getView(int position, View view, ViewGroup parent) {
            LinearLayout ll;
            ImageView iv;
            TextView tv;

            if (view == null) {
                ll = new LinearLayout(MainActivity.this);
                ll.setLayoutParams(itemViewParams);
                iv = new ImageView(MainActivity.this);
                iv.setLayoutParams(iconViewParams);
                tv = new TextView(MainActivity.this);
                tv.setLayoutParams(nameViewParams);
                ll.addView(iv);
                ll.addView(tv);
//                iv = (ImageView) ll.findViewById(R.id.app_icon);
//                tv = (TextView) ll.findViewById(R.id.app_name);
                iv.setImageDrawable(resolveInfoList.get(position).activityInfo.loadIcon(getPackageManager()));
                tv.setText(resolveInfoList.get(position).activityInfo.loadLabel(getPackageManager()));
            } else {
                ll = (LinearLayout) view;
            }

//            if (view == null) {
//                iv = new ImageView(MainActivity.this);
//                iv.setLayoutParams(appItemLayoutParams);
//            } else {
//                iv = (ImageView) view;
//            }

            return ll;
        }

        public int getCount() {
            return resolveInfoList.size();
        }

        public Object getItem(int position) {
            return resolveInfoList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
