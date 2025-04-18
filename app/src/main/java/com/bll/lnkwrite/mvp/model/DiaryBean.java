package com.bll.lnkwrite.mvp.model;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DiaryBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId=MethodManager.getAccountId();
    public String title;
    public long date;
    public int year;
    public int month;
    public String bgRes;
    public int page;
    public int uploadId;
    public boolean isUpload;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths=new ArrayList<>();

    @Generated(hash = 1727781087)
    public DiaryBean(Long id, long userId, String title, long date, int year,
            int month, String bgRes, int page, int uploadId, boolean isUpload,
            List<String> paths) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.year = year;
        this.month = month;
        this.bgRes = bgRes;
        this.page = page;
        this.uploadId = uploadId;
        this.isUpload = isUpload;
        this.paths = paths;
    }
    @Generated(hash = 1749744078)
    public DiaryBean() {
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getYear() {
        return this.year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return this.month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public String getBgRes() {
        return this.bgRes;
    }
    public void setBgRes(String bgRes) {
        this.bgRes = bgRes;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getUploadId() {
        return this.uploadId;
    }
    public void setUploadId(int uploadId) {
        this.uploadId = uploadId;
    }
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }


}
