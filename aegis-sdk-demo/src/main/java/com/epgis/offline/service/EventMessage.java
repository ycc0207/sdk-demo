package com.epgis.offline.service;

/**
 * 消息传递类
 * Created by Lynn on 2018/4/29.
 */
public class EventMessage {

    private int type;
    private Object object;
    public static final int HANDLER_GET_FILE_REAL_LENGTH = 64;
    public static final int HANDLER_GET_FILE_REAL_LENGTH_ERROR = 65;

    public EventMessage(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
