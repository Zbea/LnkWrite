package com.bll.lnkwrite.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageList {
    public int total;
    public List<MessageBean> list;

    public static class MessageBean {

        public int id;
        public String teacherName;
        @SerializedName("title")
        public String content;
        public long date;
        public int sendType;
        public int msgId;

    }
}
