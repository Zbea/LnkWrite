package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.CalenderItemBeanDao;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.mvp.model.CalenderItemBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class CalenderDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static CalenderDaoManager mDbController;
    private final CalenderItemBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public CalenderDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getCalenderItemBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static CalenderDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (CalenderDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new CalenderDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= CalenderItemBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(CalenderItemBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<CalenderItemBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(CalenderItemBeanDao.Properties.Date).build().list();
    }

    public List<CalenderItemBean> queryList(int index,int size) {
        return dao.queryBuilder().where(whereUser).orderDesc(CalenderItemBeanDao.Properties.Date)
                .offset(index-1).limit(size)
                .build().list();
    }

    public CalenderItemBean queryCalenderBean() {
        WhereCondition whereCondition= CalenderItemBeanDao.Properties.IsSet.eq(true);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    public boolean isExist(int pid){
        WhereCondition whereCondition= CalenderItemBeanDao.Properties.Pid.eq(pid);
        CalenderItemBean item=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return item!=null;
    }

    public void setSetFalse(){
        WhereCondition whereCondition= CalenderItemBeanDao.Properties.IsSet.eq(true);
        CalenderItemBean item=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        if (item!=null){
            item.isSet=false;
            insertOrReplace(item);
        }
    }

    public void deleteBean(CalenderItemBean bean){
        dao.delete(bean);
    }


    public void deleteBeans(List<CalenderItemBean> items){
        dao.deleteInTx(items);
    }

    public void clear(){
        dao.deleteAll();
    }
}
