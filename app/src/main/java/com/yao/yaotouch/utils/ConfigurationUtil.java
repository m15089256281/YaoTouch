package com.yao.yaotouch.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yao.yaotouch.bean.SettingBean;
import com.yao.yaotouch.TouchService;
import com.yao.yaotouch.bean.Action;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yao on 2016/9/23 0023.
 */

public class ConfigurationUtil {

    public static List<SettingBean> settingList = new ArrayList<>();
    public static List<Action> responseActions;
    public static List<Action> sponsorActions;

    public static void saveConfiguration() {
        String configText;
        try {
            JSONArray configJson = new JSONArray();
            for (SettingBean bean : settingList) {

                setClick(bean);

                JSONObject beanJson = new JSONObject();
                if (bean.getValue() != null) {
                    beanJson.put("action_name", bean.getValue().getName());
                    beanJson.put("action_value", bean.getValue().getAction());
                }
                beanJson.put("key_name", bean.getKey().getName());
                beanJson.put("key_value", bean.getKey().getAction());
                configJson.put(beanJson);
            }
            configText = configJson.toString();
            FileUtil.saveFile("Configuration.txt", configText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readConfiguration() {
        try {
            String configText = FileUtil.readFile("Configuration.txt");
            if (TextUtils.isEmpty(configText)) return;
            JSONArray configJson = new JSONArray(configText);
            settingList.removeAll(settingList);
            for (int i = 0; i < configJson.length(); i++) {
                JSONObject beanJson = configJson.getJSONObject(i);
                SettingBean bean = new SettingBean();
                bean.setKey(new Action(beanJson.getString("key_name"), beanJson.getInt("key_value")));
                if (!TextUtils.isEmpty(beanJson.optString("action_name"))) {
                    bean.setValue(new Action(beanJson.getString("action_name"), beanJson.getInt("action_value")));
                }
                setClick(bean);
                settingList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClick(SettingBean bean) {
        if (bean.getValue() == null) return;
        int action = bean.getKey().getAction();
        if (action == 0) {
            TouchService.onClick = bean.getValue().getAction();
        } else if (action == 1) {
            TouchService.onDoubleClick = bean.getValue().getAction();
        } else if (action == 2) {
            TouchService.onLongClick = bean.getValue().getAction();
        } else if (action == 3) {
            TouchService.onTouchTop = bean.getValue().getAction();
        } else if (action == 4) {
            TouchService.onTouchLeft = bean.getValue().getAction();
        } else if (action == 5) {
            TouchService.onTouchRight = bean.getValue().getAction();
        } else if (action == 6) {
            TouchService.onTouchBottom = bean.getValue().getAction();

        }
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
