package com.yao.yaotouch.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yao.yaotouch.bean.Action;
import com.yao.yaotouch.R;
import com.yao.yaotouch.bean.SettingBean;
import com.yao.yaotouch.adapter.ActionAdapter;

import java.util.List;

public class ActionDialog extends Dialog implements ActionAdapter.ActionOnClickListener {

    ActionAdapter adapter;
    SettingBean settingBean;
    ActionAdapter.ActionOnClickListener listener;


    public ActionDialog(Context context, SettingBean settingBean, List<Action> list) {
        super(context, R.style.dialogOptions);
        adapter = new ActionAdapter(list);
        this.settingBean = settingBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_action, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.CENTER;
        // 获取屏幕宽、高用
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        // 高度设置为屏幕的0.8
        lp.width = (int) (d.widthPixels * 0.8);
//        //动画
//        dialogWindow.setWindowAnimations(R.style.dialog_bottom_anim);
        dialogWindow.setAttributes(lp);

    }

    public static void show(Context context, SettingBean settingBean, List<Action> list) {
        new ActionDialog(context, settingBean, list).show();
    }

    @Override
    public void onClick(Action action) {
        settingBean.setValue(action);
        if (listener != null) listener.onClick(action);
        dismiss();
    }

    public ActionDialog setListener(ActionAdapter.ActionOnClickListener listener) {
        this.listener = listener;
        return this;
    }
}