package com.bll.lnkwrite.utils;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;


public class FileMultitaskDownManager {

    private static FileMultitaskDownManager incetance;
    private static Context mContext;
    private List<String> urls; //下载的url链接
    private List<String> paths;//文件的绝对路径
    private String auth = "";
    private String token = "";
    private int num=0;


    public static FileMultitaskDownManager with(Context context) {
        if (incetance == null) {
            synchronized (FileMultitaskDownManager.class) {
                if (incetance == null) {
                    incetance = new FileMultitaskDownManager();
                }
            }
        }
        mContext = context;
        return incetance;
    }

    //创建下载链接
    public FileMultitaskDownManager create(List<String> urls) {
        this.urls = urls;
        num=0;
        return this;
    }

    public FileMultitaskDownManager setPath(List<String> paths) {
        this.paths = paths;
        return this;
    }

    //单任务下载
    public FileDownloadQueueSet startMultiTaskDownLoad(final MultiTaskCallBack multitaskCallBack) {

        auth = "Authorization";
        token = SPUtil.INSTANCE.getString("token");

        FileDownloadListener fileDownloadListener=new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                multitaskCallBack.progress(task, soFarBytes, totalBytes);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                num+=1;
                if (num==urls.size()){
                    multitaskCallBack.completed(task);
                }
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                multitaskCallBack.paused(task, soFarBytes, totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                multitaskCallBack.error(task, e);
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        };

        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(fileDownloadListener);

        final List<BaseDownloadTask> tasks = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            tasks.add(FileDownloader.getImpl().create(urls.get(i))
                    .setPath(paths.get(i))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader(auth, token));
        }
        queueSet.disableCallbackProgressTimes();
        queueSet.setAutoRetryTimes(1);
        queueSet.downloadTogether(tasks);//并行下载
        queueSet.setForceReDownload(true);
        queueSet.start();

        return queueSet;
    }

    public interface MultiTaskCallBack {

        void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void completed(BaseDownloadTask task);

        void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void error(BaseDownloadTask task, Throwable e);
    }

}
