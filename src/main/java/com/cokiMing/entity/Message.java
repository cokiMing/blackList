package com.cokiMing.entity;

/**
 * Created by wuyiming on 2017/11/7.
 */
public class Message {

    public final static int SUCCESS = 200;

    public final static int FAIL = 400;

    public final static int DEFRIEND = 0;

    public final static int CHECK = 1;

    public final static int RESUME = 2;

    private int len;

    private int type;

    private String content;

    public int getLen() {
        return len;
    }

    public void fixLen() {
        this.len = 4 + content.getBytes().length;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
