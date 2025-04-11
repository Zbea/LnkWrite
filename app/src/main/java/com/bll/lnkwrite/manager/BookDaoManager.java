package com.bll.lnkwrite.manager;


import com.bll.lnkwrite.Constants;
import com.bll.lnkwrite.MethodManager;
import com.bll.lnkwrite.MyApplication;
import com.bll.lnkwrite.greendao.BookDao;
import com.bll.lnkwrite.greendao.DaoSession;
import com.bll.lnkwrite.mvp.model.book.Book;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;


public class BookDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static BookDaoManager mDbController;
    private final BookDao bookDao;  //book表
    private static WhereCondition whereUser;


    /**
     * 构造初始化
     */
    public BookDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        bookDao = mDaoSession.getBookDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static BookDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (BookDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BookDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= BookDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(Book bean) {
        bookDao.insertOrReplace(bean);
    }


    public Book queryByBookID(int bookID) {
        WhereCondition  whereCondition1= BookDao.Properties.BookId.eq(bookID);
        return bookDao.queryBuilder().where(whereUser,whereCondition1).build().unique();
    }

    /**
     * 获取半年以前的书籍n
     */
    public List<Book> queryBookByHalfYear(){
        long time=System.currentTimeMillis()- Constants.halfYear;
        WhereCondition whereCondition1= BookDao.Properties.Time.le(time);
        return bookDao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    //查询所有书籍
    public List<Book> queryAllBook() {
        return bookDao.queryBuilder().where(whereUser).orderDesc(BookDao.Properties.Time).build().list();
    }

    /**
     * 获取打开过的书籍
     * @param isLook
     * @return
     */
    public List<Book> queryAllBook(boolean isLook,int count) {
        WhereCondition whereCondition1=BookDao.Properties.IsLook.eq(isLook);
        return bookDao.queryBuilder().where(whereUser,whereCondition1).orderDesc(BookDao.Properties.Time).limit(count).build().list();
    }

    //根据类别 细分子类
    public List<Book> queryAllBook(String type) {
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
    }

    //根据类别 细分子类 分页处理
    public List<Book> queryAllBook(String type, int page, int pageSize) {
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

    public void clear(){
        bookDao.deleteAll();
    }
}
