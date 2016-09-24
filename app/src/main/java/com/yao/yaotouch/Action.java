package com.yao.yaotouch;

/**
 * Created by Yao on 2016/9/21 0021.
 */

public class Action {
    private String name;
    private int action;

    public Action(String name, int action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
