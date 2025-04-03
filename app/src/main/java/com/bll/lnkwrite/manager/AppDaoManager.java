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

    /**
     * 查找工具应用
     * @param packageName
     * @return
     */
    public AppBean queryAllByPackageName(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition where2= AppBeanDao.Properties.Type.eq(2);
        return dao.queryBuilder().where(whereUser,whereCondition,where2).build().unique();
    }

    /**
     * 获取工具应用
     * @return
     */
    public List<AppBean> queryTool() {
        WhereCondition whereCondition1= AppBeanDao.Properties.Type.eq(2);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    /**
     * 获取首页菜单
     * @return
     */
    public List<AppBean> queryMenu() {
        WhereCondition whereCondition1= AppBeanDao.Properties.Type.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition1).orderAsc(AppBeanDao.Properties.Sort).build().list();
    }

    /**
     * 获取工具应用
     * @return
     */
    public List<AppBean> queryAPPTool() {
        WhereCondition whereCondition1= AppBeanDao.Properties.SubType.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    /**
     * 应用是否存储
     * @param packageName
     * @return
     */
    public boolean isExist(String packageName){
        WhereCondition where1= AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition where2= AppBeanDao.Properties.SubType.eq(1);
        AppBean appBean=dao.queryBuilder().where(whereUser,where1,where2).build().unique();
        return appBean!=null;
    }

    /**
     * 应用是否设置为菜单、工具
     * @param packageName
     * @param type
     * @return
     */
    public boolean isExist(String packageName,int type){
        WhereCondition where1= AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition where2= AppBeanDao.Properties.Type.eq(type);
        AppBean appBean=dao.queryBuilder().where(whereUser,where1,where2).build().unique();
        return appBean!=null;
    }

    public AppBean queryByType(String packageName,int type){
        WhereCondition where1= AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition where2= AppBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,where1,where2).build().unique();
    }

    public void deleteBySort(int sort){
        WhereCondition where1=AppBeanDao.Properties.Type.eq(1);
        WhereCondition where2= AppBeanDao.Properties.Sort.eq(sort);
        AppBean bean=dao.queryBuilder().where(whereUser,where1,where2).build().unique();
        if (bean!=null){
            delete(bean);
        }
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
