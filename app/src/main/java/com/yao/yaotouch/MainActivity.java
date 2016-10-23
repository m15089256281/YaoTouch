package com.yao.yaotouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.yao.yaotouch.ConfigurationUtil.responseActions;
import static com.yao.yaotouch.ConfigurationUtil.settingList;
import static com.yao.yaotouch.ConfigurationUtil.sponsorActions;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    RecyclerView recyclerView;
    Context context = this;
    SettingAdapter adapter;
    SeekBar seekBar;
    TextView sizeTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
