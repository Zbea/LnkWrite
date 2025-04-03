package com.bll.lnkwrite.manager;

import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.greendao.NoteContentBeanDao;
import com.bll.lnkwrite.mvp.model.NoteContentBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NoteContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static NoteContentDaoManager mDbController;
    private final NoteContentBeanDao dao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public NoteContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNoteContentBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteContentDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= NoteContentBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplaceNote(NoteContentBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<NoteContentBean> queryAll(String type, String notebookTitle) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookTitle.eq(notebookTitle);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderAsc(NoteContentBeanDao.Properties.Date).build().list();
    }

    public boolean isExistContents(String type, String notebookTitle){
        List<NoteContentBean> noteContentBeans =queryAll(type,notebookTitle);
        return !noteContentBeans.isEmpty();
    }

    public void editNoteTitles(String type, String notebookTitle,String editTitle){
        List<NoteContentBean> noteContentBeans =queryAll(type,notebookTitle);
        for (NoteContentBean noteContentBean : noteContentBeans) {
            noteContentBean.notebookTitle=editTitle;
        }
        dao.insertOrReplaceInTx(noteContentBeans);
    }

    public void editNoteTypes(String type, String notebookTitle,String editType){
        List<NoteContentBean> noteContentBeans =queryAll(type,notebookTitle);
        for (NoteContentBean noteContentBean : noteContentBeans) {
            noteContentBean.typeStr=editType;
        }
        dao.insertOrReplaceInTx(noteContentBeans);
    }

    public void deleteNote(NoteContentBean noteContentBean){
        dao.delete(noteContentBean);
    }

    public void deleteType(String type,String notebookTitle){
        WhereCondition whereCondition=NoteContentBeanDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookTitle.eq(notebookTitle);
        List<NoteContentBean> list = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        dao.deleteInTx(list);
    }

    public void clear(){
        dao.deleteAll();
    }
}
