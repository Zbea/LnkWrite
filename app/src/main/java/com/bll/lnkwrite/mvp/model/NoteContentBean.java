package com.bll.lnkwrite.mvp.model;

import com.bll.lnkwrite.MethodManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class NoteContentBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String typeStr;//分类
    public String notebookTitle;
    public long date;//创建时间
    public String title;
    public String resId;//背景id
    public String filePath;//文件路径

    @Generated(hash = 1614472636)
    public NoteContentBean(Long id, long userId, String typeStr,
            String notebookTitle, long date, String title, String resId,
            String filePath) {
        this.id = id;
        this.userId = userId;
        this.typeStr = typeStr;
        this.notebookTitle = notebookTitle;
        this.date = date;
        this.title = title;
        this.resId = resId;
        this.filePath = filePath;
    }
    @Generated(hash = 112846212)
    public NoteContentBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    public String getNotebookTitle() {
        return this.notebookTitle;
    }
    public void setNotebookTitle(String notebookTitle) {
        this.notebookTitle = notebookTitle;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getResId() {
        return this.resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
