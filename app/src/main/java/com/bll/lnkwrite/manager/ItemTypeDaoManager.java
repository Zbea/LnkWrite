package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.greendao.ItemTypeBeanDao;
import com.bll.lnkwrite.greendao.WallpaperBeanDao;
import com.bll.lnkwrite.mvp.model.ItemTypeBean;
import com.bll.lnkwrite.mvp.model.WallpaperBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class ItemTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static ItemTypeDaoManager mDbController;
    private final ItemTypeBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public ItemTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getItemTypeBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static ItemTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (ItemTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new ItemTypeDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= ItemTypeBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(ItemTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public ItemTypeBean queryBean(int type,String title) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Title.eq(title);
        return dao.queryBuilder().where(whereUser,whereUser1,whereUser2).build().unique();
    }

    public List<ItemTypeBean> queryAll(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderAsc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public List<ItemTypeBean> queryAllOrderDesc(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderDesc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public List<ItemTypeBean> queryAllOrderDesc(int type, int index,int size) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1)
                .offset((index-1)*size).limit(size)
                .orderDesc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public Boolean isExist(String title,int type){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Title.eq(title);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1,whereUser2).unique()!=null;
    }

    /**
     * 查看日记分类是否已经下载
     * @param typeId
     * @return
     */
    public Boolean isExistDiaryType(int typeId){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.TypeId.eq(typeId);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(4);
        return !dao.queryBuilder().where(whereUser, whereUser1, whereUser2).build().list().isEmpty();
    }

    public void deleteBean(ItemTypeBean bean){
        dao.delete(bean);
    }

    public void clear(int type){
        dao.deleteInTx(queryAll(type));
    }
    public void clear(){
        dao.deleteAll();
    }
}
