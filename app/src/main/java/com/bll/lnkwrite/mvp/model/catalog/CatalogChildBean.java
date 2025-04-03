package com.bll.lnkwrite.mvp.model.catalog;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChildBean implements Serializable, MultiItemEntity {

    public String title;
    public int parentPosition;
    public int pageNumber;
    public String picName;

    @Override
    public int getItemType() {
        return 1;
    }
}
