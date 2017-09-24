package com.lmachine.mlda.bean;

/**
 * Created by SailFlorve on 2017/9/24 0024.
 * 服务器返回数据json实体
 */

public class Status {
    private boolean status;
    private String message;

    public String getMessage() {
        return message;
    }

    public boolean getStatus() {
        return status;
    }
}
