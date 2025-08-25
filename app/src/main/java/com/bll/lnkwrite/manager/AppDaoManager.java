package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.AppBeanDao;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.mvp.model.AppBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class AppDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static AppDaoManager mDbController;
    private final AppBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public AppDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getAppBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static AppDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (AppDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new AppDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= AppBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(AppBean bean) {
        dao.insertOrReplace(bean);
    }

    public AppBean queryBeanByPackageName(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }
    public List<AppBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public List<AppBean> queryToolAll() {
        WhereCondition whereCondition=AppBeanDao.Properties.IsTool.eq(true);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public void delete(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        AppBean appBean=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        if (appBean!=null)
            delete(appBean);
    }


    public void delete(AppBean item) {
        dao.delete(item);
    }

    public void clear(){
        dao.deleteAll();
    }

}
