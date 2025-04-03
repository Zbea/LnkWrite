package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.greendao.PaintingContentBeanDao;
import com.bll.lnkwrite.mvp.model.PaintingContentBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaintingContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaintingContentDaoManager mDbController;
    private final PaintingContentBeanDao dao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public PaintingContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaintingContentBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaintingContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaintingContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingContentDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= PaintingContentBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaintingContentBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<PaintingContentBean> queryAll(String type) {
        WhereCondition whereCondition=PaintingContentBeanDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderAsc(PaintingContentBeanDao.Properties.Date).build().list();
    }

    public void deleteType(String type){
        WhereCondition whereCondition=PaintingContentBeanDao.Properties.TypeStr.eq(type);
        List<PaintingContentBean> list = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        dao.deleteInTx(list);
    }

}
