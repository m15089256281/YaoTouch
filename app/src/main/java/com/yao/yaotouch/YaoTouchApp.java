package com.yao.yaotouch;

import android.app.Application;

/**
 * Created by Yao on 2016/9/21 0021.
 */
public class YaoTouchApp extends Application {

    private static YaoTouchApp application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        ConfigurationUtil.readConfiguration();
    }

    public static YaoTouchApp getApplication() {
        return application;
    }
}