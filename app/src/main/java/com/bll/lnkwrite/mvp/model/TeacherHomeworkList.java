package com.bll.lnkwrite.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TeacherHomeworkList {

    public int total;
    public List<TeacherHomeworkBean> list;

    public static class TeacherHomeworkBean implements Serializable {
        public int id;
        public String subject;
        public int type;
        public String homeworkName;
        public long submitTime;//学生提交时间
        public long time;//学生需要提交时间
        public String createTime;//布置时间
        public String correctTime;//老师批改时间
        public String submitContent;
        public String homeworkContent;
        public String correctContent;
        public String title;
        @SerializedName("msgType")
        public int status;
        public int studentTaskId;
        public String score;
        public int questionType;
        public int questionMode;
        public String question;
        public int selfBatchStatus;//1自批
        public String answerUrl;
    }
}
