package com.yao.yaotouch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.yao.yaotouch.R;
import com.yao.yaotouch.TouchService;
import com.yao.yaotouch.YaoTouchApp;
import com.yao.yaotouch.adapter.SettingAdapter;
import com.yao.yaotouch.bean.Action;
import com.yao.yaotouch.bean.SettingBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.yao.yaotouch.utils.ConfigurationUtil.responseActions;
import static com.yao.yaotouch.utils.ConfigurationUtil.settingList;
import static com.yao.yaotouch.utils.ConfigurationUtil.sponsorActions;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    RecyclerView recyclerView;
    Context context = this;
    SettingAdapter adapter;
    SeekBar seekBar;
    TextView sizeTv;
    Switch isAccessibility, isFloat, isMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    protected void initView() {
        readKeys();
        readAction();
        adapter = new SettingAdapter(settingList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(TouchService.size);
        seekBar.setOnSeekBarChangeListener(this);
        sizeTv = (TextView) findViewById(R.id.tv_size);
        sizeTv.setText(TouchService.size + "");

        isAccessibility = (Switch) findViewById(R.id.is_accessibility);
        isFloat = (Switch) findViewById(R.id.is_float);
        isMargin = (Switch) findViewById(R.id.is_margin);
        isAccessibility.setOnClickListener(this);
        isFloat.setOnClickListener(this);
        isMargin.setOnClickListener(this);

        isAccessibility.setChecked(TouchService.isAccessibilitySettingsOn(this));
        isFloat.setChecked(Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this) || Build.VERSION.SDK_INT < 23);
        isMargin.setChecked(TouchService.isMargin);
        if (settingList.size() == 0) {
            for (Action action : responseActions) {
                SettingBean bean = new SettingBean();
                bean.setKey(action);
                settingList.add(bean);
            }
        }
    }

    private void readKeys() {
        responseActions = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(YaoTouchApp.getApplication().getResources().getAssets().open("responseActions.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                String key[] = line.split("&");
                responseActions.add(new Action(key[0], Integer.valueOf(key[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readAction() {
        sponsorActions = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(YaoTouchApp.getApplication().getResources().getAssets().open("sponsorActions.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                String key[] = line.split("&");
                sponsorActions.add(new Action(key[0], Integer.valueOf(key[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TouchService.size = progress;
        SharedPreferences sharedPreferences = getSharedPreferences("Configuration", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("size", progress).commit();
        sizeTv.setText(progress + "");
        TouchService.refreshSize();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.is_accessibility:
                // 引导至辅助功能设置页面
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case R.id.is_float:
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
                break;
            case R.id.is_margin:
//                isMargin.setChecked(!isMargin.isChecked());
                TouchService.isMargin = isMargin.isChecked();
                Log.i("Yao", "------- " + TouchService.isMargin);
                break;
        }
    }
}
