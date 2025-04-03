package com.bll.lnkwrite.mvp.view;

import com.bll.lnkwrite.mvp.model.book.TextbookStore;
import com.bll.lnkwrite.mvp.model.AccountOrder;
import com.bll.lnkwrite.mvp.model.AccountQdBean;
import com.bll.lnkwrite.mvp.model.AppList;
import com.bll.lnkwrite.mvp.model.book.BookStore;
import com.bll.lnkwrite.mvp.model.book.BookStoreType;
import com.bll.lnkwrite.mvp.model.CalenderList;
import com.bll.lnkwrite.mvp.model.CloudList;
import com.bll.lnkwrite.mvp.model.CommonData;
import com.bll.lnkwrite.mvp.model.teaching.ExamList;
import com.bll.lnkwrite.mvp.model.teaching.ExamRankList;
import com.bll.lnkwrite.mvp.model.FriendList;
import com.bll.lnkwrite.mvp.model.teaching.HomeworkCorrectList;
import com.bll.lnkwrite.mvp.model.teaching.HomeworkTypeList;
import com.bll.lnkwrite.mvp.model.MessageList;
import com.bll.lnkwrite.mvp.model.SchoolBean;
import com.bll.lnkwrite.mvp.model.teaching.Score;
import com.bll.lnkwrite.mvp.model.ShareNoteList;
import com.bll.lnkwrite.mvp.model.StudentBean;
import com.bll.lnkwrite.mvp.model.TeacherHomeworkList;
import com.bll.lnkwrite.mvp.model.User;
import com.bll.lnkwrite.mvp.model.WallpaperList;
import com.bll.lnkwrite.net.IBaseView;

import java.util.List;

public interface IContractView {

    //登录
    interface ILoginView extends IBaseView {
        void getLogin(User user);
        void getAccount(User user);
    }

    //注册 找回密码
    interface IRegisterView extends IBaseView {
        void onSms();
        void onRegister();
        void onFindPsd();
    }

    interface IAccountInfoView extends IBaseView {
        void onEditNameSuccess();
        void onBind();
        void onUnbind();
        void onListStudent(List<StudentBean> beans);
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void onXdList(List<AccountQdBean> list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
        void transferSuccess();
        void getAccount(User user);
    }

    interface ISchoolView extends IBaseView{
        void onListSchools(List<SchoolBean> list);
    }

    //主页
    interface ICommonView extends IBaseView {
        void onCommon(CommonData commonData);
    }

    interface IBookStoreView extends IBaseView {
        default void onBook(BookStore bookStore){};
        default void onTextbook(TextbookStore bookStore){};
        default void onType(BookStoreType bookStoreType){};
        default void buyBookSuccess(){};
    }

    //应用
    interface IAPPView extends IBaseView {
        void onType(CommonData commonData);
        void onAppList(AppList appBean);
        void buySuccess();
    }

    interface ICalenderView extends IBaseView {
        void onList(CalenderList list);
        void buySuccess();
    }

    interface IWallpaperView extends IBaseView {
        void onList(WallpaperList list);
        void buySuccess();
    }

    interface IHomeworkView extends IBaseView{
        void onList(TeacherHomeworkList item);
        void onDeleteSuccess();
    }

    interface IMyHomeworkView extends IBaseView{
        default void onList(HomeworkTypeList homeworkTypeList){};
        default void onCreateSuccess(){};
        default void onEditSuccess(){};
        default void onDelete(){};
        default void onSendSuccess(){};
    }

    interface IScoreRankView extends IBaseView{
        void onScore(List<Score> scores);
        void onExamScore(ExamRankList list);
    }

    interface IRelationView extends IBaseView{
        void onListStudents(List<StudentBean> list);
    }
    interface IHomeworkCorrectView extends IBaseView{
        default void onList(HomeworkCorrectList list){};
        default void onUpdateSuccess(){};
        default void onDeleteSuccess(){};
    }

    interface IFreeNoteView extends IBaseView{
        void onReceiveList(ShareNoteList list);
        void onShareList(ShareNoteList list);
        void onToken(String token);
        void onDeleteSuccess();
        void onShare();
        void onBind();
        void onUnbind();
        void onListFriend(FriendList list);
    }

    interface IQiniuView extends IBaseView {
        void onToken(String token);
    }

    /**
     * 云书库上传
     */
    interface ICloudUploadView extends IBaseView{
        void onSuccess(List<Integer> cloudIds);
        default void onDeleteSuccess(){};
    }

    interface ICloudView extends IBaseView {
        void onList(CloudList item);
        void onType(List<String> types);
        void onDelete();
    }

    interface IMessageView extends IBaseView{
        void onList(MessageList message);
        default void onCommitSuccess(){};
    }

    interface IExamView extends IBaseView {
        void onList(ExamList list);
        void onDeleteSuccess();
    }

    interface IPermissionSettingView extends IBaseView {
        void onStudent(StudentBean studentBean);
        void onSuccess();
        void onChangeSuccess();
        void onEditSuccess();
    }

}
