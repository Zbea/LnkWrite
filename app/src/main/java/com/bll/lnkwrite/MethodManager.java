package com.bll.lnkwrite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bll.lnkwrite.dialog.ImageDialog;
import com.bll.lnkwrite.manager.AppDaoManager;
import com.bll.lnkwrite.manager.BookDaoManager;
import com.bll.lnkwrite.manager.NoteDaoManager;
import com.bll.lnkwrite.manager.TextbookGreenDaoManager;
import com.bll.lnkwrite.mvp.model.AppBean;
import com.bll.lnkwrite.mvp.model.AreaBean;
import com.bll.lnkwrite.mvp.model.ItemTypeBean;
import com.bll.lnkwrite.mvp.model.Note;
import com.bll.lnkwrite.mvp.model.PrivacyPassword;
import com.bll.lnkwrite.mvp.model.User;
import com.bll.lnkwrite.mvp.model.book.Book;
import com.bll.lnkwrite.mvp.model.book.TextbookBean;
import com.bll.lnkwrite.ui.activity.AccountLoginActivity;
import com.bll.lnkwrite.ui.activity.drawing.FileDrawingActivity;
import com.bll.lnkwrite.ui.activity.drawing.NoteDrawingActivity;
import com.bll.lnkwrite.utils.ActivityManager;
import com.bll.lnkwrite.utils.AppUtils;
import com.bll.lnkwrite.utils.FileUtils;
import com.bll.lnkwrite.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MethodManager {

    public static User getUser(){
        return SPUtil.INSTANCE.getObj("user", User.class);
    }

    public static boolean isLogin(){
        String tokenStr=SPUtil.INSTANCE.getString("token");
        return !TextUtils.isEmpty(tokenStr) && getUser()!=null;
    }

    public static long getAccountId(){
        User user=SPUtil.INSTANCE.getObj("user", User.class);
        if (user==null){
            return 0L;
        }
        else {
            return user.accountId;
        }
    }

    public static boolean isCN(){
        // 获取当前系统默认的Locale
        Locale currentLocale = Locale.getDefault();
        // 获取语言代码
        String languageCode = currentLocale.getLanguage();
        return "zh".equals(languageCode);
    }

    /**
     * 退出登录
     * @param context
     */
    public static void logout(Context context){
        SPUtil.INSTANCE.putString("token", "");
        SPUtil.INSTANCE.removeObj("user");
        DataBeanManager.INSTANCE.getStudents().clear();
        EventBus.getDefault().post(Constants.STUDENT_EVENT);

        Intent i = new Intent(context, AccountLoginActivity.class);
        i.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL);
        context.startActivity(i);
        ActivityManager.getInstance().finishOthers(AccountLoginActivity.class);
        //发出退出登录广播
        Intent intent = new Intent();
        intent.putExtra("token", "");
        intent.putExtra("userId", 0);
        intent.setAction(Constants.LOGOUT_BROADCAST_EVENT);
        context.sendBroadcast(intent);
    }

    /**
     * 跳转阅读器
     * @param context
     * @param bookBean key_book_type 0普通书籍 1pdf书籍 2pdf课本 3文档
     */
    public static void gotoBookDetails(Context context,int type, Book bookBean)  {
        AppUtils.stopApp(context,Constants.PACKAGE_READER);

        bookBean.isLook=true;
        bookBean.time=System.currentTimeMillis();
        BookDaoManager.getInstance().insertOrReplaceBook(bookBean);

        String format = FileUtils.getUrlFormat(bookBean.bookPath);
        int key_type = 0;
        if (type==1){
            if (format.contains("pdf")) {
                key_type = 1;
            }
        }
        else {
            key_type=2;
        }

        Intent intent = new Intent();
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id",bookBean.bookId+"");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("zh/tool",getJsonArray().toString());
        intent.putExtra("userId",getUser()!=null?getUser().accountId:0);
        intent.putExtra("type", type);
        intent.putExtra("drawPath", bookBean.bookDrawPath);
        intent.putExtra("key_book_type", key_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->
                        EventBus.getDefault().post(Constants.BOOK_EVENT)
                ,3000);
    }

    /**
     * 获取工具
     * @return
     */
    public static @NonNull JSONArray getJsonArray() {
        List<AppBean> toolApps= AppDaoManager.getInstance().queryTool();
        JSONArray result =new JSONArray();
        for (AppBean item : toolApps) {
            if (Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY))
                continue;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("appName", item.appName);
                jsonObject.put("packageName", item.packageName);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            result.put(jsonObject);
        }
        return result;
    }


    public static void deleteBook(Book book){
        BookDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        FileUtils.deleteFile(new File(book.bookDrawPath));
        EventBus.getDefault().post(Constants.BOOK_EVENT) ;
    }

    public static void deleteTextbook(TextbookBean book){
        TextbookGreenDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        FileUtils.deleteFile(new File(book.bookDrawPath));
        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT);
    }

    /**
     * 跳转截图列表
     * @param context
     * @param index
     * @param tabPath
     */
    public static void gotoScreenFile(Context context,int index,String tabPath){
        ActivityManager.getInstance().finishActivity(FileDrawingActivity.class.getName());
        Intent intent=new Intent(context, FileDrawingActivity.class);
        intent.putExtra("pageIndex",index);
        intent.putExtra("pagePath",tabPath);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    public static void gotoDocument(Context context,File file){
        if (FileUtils.getUrlFormat(file.getPath()).equals(".png") || FileUtils.getUrlFormat(file.getPath()).equals(".jpg")){
            List<String> images=new ArrayList<>();
            images.add(file.getPath());
            new ImageDialog(context,1, images).builder();
        }
        else {
            String fileName=FileUtils.getUrlName(file.getPath());
            String drawPath=file.getParent()+"/"+fileName+"draw/";
            Intent intent=new Intent();
            intent.setAction("com.geniatech.reader.action.VIEW_BOOK_PATH");
            intent.setPackage(Constants.PACKAGE_READER);
            intent.putExtra("path", file.getPath());
            intent.putExtra("bookName", fileName);
            intent.putExtra("zh/tool", getJsonArray().toString());
            intent.putExtra("userId", getAccountId());
            intent.putExtra("type", 1);
            intent.putExtra("drawPath", drawPath);
            intent.putExtra("key_book_type", 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转笔记写作
     */
    public static void gotoNote(Context context, Note note) {
        ActivityManager.getInstance().finishActivity(NoteDrawingActivity.class.getName());
        Intent intent = new Intent(context, NoteDrawingActivity.class);
        intent.putExtra("noteId",note.id);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);

        note.date=System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);
    }

    /**
     * 保存私密密码
     * type 0日记1密本
     * @param privacyPassword
     */
    public static void savePrivacyPassword(int type,PrivacyPassword privacyPassword){
        if (type==0){
            SPUtil.INSTANCE.putObj("privacyPasswordDiary",privacyPassword);
        }
        else{
            SPUtil.INSTANCE.putObj("privacyPasswordNote",privacyPassword);
        }
    }

    /**
     * 获取私密密码
     * type 0日记1密本
     * @return
     */
    public static PrivacyPassword getPrivacyPassword(int type){
         if (type==0){
             return SPUtil.INSTANCE.getObj("privacyPasswordDiary", PrivacyPassword.class);
        }
        else{
             return SPUtil.INSTANCE.getObj("privacyPasswordNote", PrivacyPassword.class);
        }
    }

    /**
     * 获取状态栏的值
     * @return
     */
    public static int getStatusBarValue(){
        return Settings.System.getInt(MyApplication.Companion.getMContext().getContentResolver(), "statusbar_hide_time", 0);
    }

    /**
     * 设置状态栏的值
     *
     * @return
     */
    public static void setStatusBarValue(int value){
        Settings.System.putInt(MyApplication.Companion.getMContext().getContentResolver(),"statusbar_hide_time", value);
    }

    /**
     * 加载不失真背景
     * @param context
     * @param resId
     * @param imageView
     */
    public static void setImageResource(Context context, int resId, ImageView imageView){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 防止自动缩放
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 获取省
     * @param context
     * @return
     * @throws IOException
     */
    public static List<AreaBean> getProvinces(Context context) throws IOException {
        String areaJson = FileUtils.readFileContent(context.getResources().getAssets().open("city.json"));
        return new Gson().fromJson(areaJson, new TypeToken<List<AreaBean>>(){}.getType());
    }

    public static ItemTypeBean getDefaultItemTypeScreenshot(){
        String title=MyApplication.Companion.getMContext().getString(R.string.untype);
        ItemTypeBean itemTypeBean=new ItemTypeBean();
        itemTypeBean.type=3;
        itemTypeBean.path=new FileAddress().getPathScreen(title);
        itemTypeBean.title=title;
        return itemTypeBean;
    }

    public static ItemTypeBean getDefaultItemTypeDocument(){
        String title=MyApplication.Companion.getMContext().getString(R.string.default_str);
        ItemTypeBean itemTypeBean=new ItemTypeBean();
        itemTypeBean.type=6;
        itemTypeBean.path=new FileAddress().getPathDocument(title);
        itemTypeBean.title=title;
        return itemTypeBean;
    }
    /**
     * 初始化不选中 指定位置选中
     * @param list
     * @param position
     * @return
     */
    public static List<ItemTypeBean> setItemTypeBeanCheck(List<ItemTypeBean> list,int position){
        if (list.size()>position){
            for (ItemTypeBean item:list) {
                item.isCheck=false;
            }
            list.get(position).isCheck=true;
        }
        return list;
    }

    public static void createFileScan(Context context,String path){
        if (!FileUtils.isExist(path)){
            File file=new File(path+"/1");
            file.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},null, null);
            new Handler().postDelayed(() -> {
                FileUtils.deleteFile(file);
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},null, null);
            },10*1000);
        }
    }

}
