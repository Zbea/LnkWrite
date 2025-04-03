package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.greendao.WallpaperBeanDao;
import com.bll.lnkwrite.mvp.model.WallpaperBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class WallpaperDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static WallpaperDaoManager mDbController;
    private final WallpaperBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public WallpaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getWallpaperBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static WallpaperDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (WallpaperDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new WallpaperDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= WallpaperBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(WallpaperBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<WallpaperBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(WallpaperBeanDao.Properties.Date).build().list();
    }

    public List<WallpaperBean> queryList(int size,int index) {
        return dao.queryBuilder().where(whereUser)
                .limit(size).offset(index-1)
                .orderDesc(WallpaperBeanDao.Properties.Date).build().list();
    }

    public WallpaperBean queryBean(int id){
        WhereCondition whereCondition=WallpaperBeanDao.Properties.ContentId.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(WallpaperBeanDao.Properties.Date).build().unique();
    }

    public void deleteBean(WallpaperBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
