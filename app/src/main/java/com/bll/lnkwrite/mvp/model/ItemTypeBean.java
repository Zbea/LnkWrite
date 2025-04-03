package com.bll.lnkwrite.mvp.model;

import com.bll.lnkwrite.MethodManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ItemTypeBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String title;
    public int type;//1笔记分类2书籍分类3截图分类4日记下载分类标题5画本分类6文档分类
    public long date;
    public String path;
    public int typeId;
    public int page;
    @Transient
    public boolean isCheck;
    @Transient
    public int cloudId;
    @Transient
    public String downloadUrl;

    @Generated(hash = 484241845)
    public ItemTypeBean(Long id, long userId, String title, int type, long date, String path,
            int typeId, int page) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.path = path;
        this.typeId = typeId;
        this.page = page;
    }
    @Generated(hash = 2077540725)
    public ItemTypeBean() {
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }

}
