package com.yao.yaotouch;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yao on 2016/9/24 0024.
 */

public class Utils {
    public static void showToast(String msg) {
        Toast.makeText(YaoTouchApp.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String msg) {
        Log.i("Yao", msg);
    }
}
