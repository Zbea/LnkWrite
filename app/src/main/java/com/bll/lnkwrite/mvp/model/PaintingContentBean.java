package com.bll.lnkwrite.mvp.model;

import com.bll.lnkwrite.MethodManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class PaintingContentBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String typeStr;//分类
    public long date;//创建时间
    public String title;
    public String path;//文件路径

    @Generated(hash = 1788159887)
    public PaintingContentBean(Long id, long userId, String typeStr, long date,
            String title, String path) {
        this.id = id;
        this.userId = userId;
        this.typeStr = typeStr;
        this.date = date;
        this.title = title;
        this.path = path;
    }
    @Generated(hash = 138997914)
    public PaintingContentBean() {
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
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }

}
