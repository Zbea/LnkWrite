package com.bll.lnkwrite.mvp.model;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.greendao.StringConverter;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import kotlin.jvm.Transient;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

@Entity
public class WallpaperBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    @SerializedName("fontDrawId")
    public int contentId;//内容id
    @SerializedName("drawName")
    public String title;
    @SerializedName("drawDesc")
    public String info;
    public int price;
    public String imageUrl;
    public String bodyUrl;
    public int supply;//1官方2第三方
    public String author;//作者
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;//图片保存地址
    public long date;//下载时间
    @Transient
    public int buyStatus;
    @Transient
    public boolean isCheck;

    @Generated(hash = 187288249)
    public WallpaperBean(Long id, long userId, int contentId, String title, String info, int price,
            String imageUrl, String bodyUrl, int supply, String author, List<String> paths, long date,
            int buyStatus, boolean isCheck) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.title = title;
        this.info = info;
        this.price = price;
        this.imageUrl = imageUrl;
        this.bodyUrl = bodyUrl;
        this.supply = supply;
        this.author = author;
        this.paths = paths;
        this.date = date;
        this.buyStatus = buyStatus;
        this.isCheck = isCheck;
    }
    @Generated(hash = 915358704)
    public WallpaperBean() {
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
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getInfo() {
        return this.info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBodyUrl() {
        return this.bodyUrl;
    }
    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }
    public int getSupply() {
        return this.supply;
    }
    public void setSupply(int supply) {
        this.supply = supply;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getBuyStatus() {
        return this.buyStatus;
    }
    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }
    public boolean getIsCheck() {
        return this.isCheck;
    }
    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
