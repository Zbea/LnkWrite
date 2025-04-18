package com.bll.lnkwrite.utils.zip;

import com.bll.lnkwrite.Constants;
import com.bll.lnkwrite.FileAddress;

import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.util.List;


public class ZipUtils {

    /**
     * 压缩
     * @param targetStr 目标文件路径
     * @param fileName 压缩文件名称
     * @param callback
     */
    public static void zip(String targetStr, String fileName, IZipCallback callback){

        if(!new File(targetStr).exists()){
            callback.onError("目标文件不存在");
            return;
        }
        String destinationStr= Constants.INSTANCE.getZIP_PATH()+File.separator + fileName + ".zip";
        ZipManager.zip(targetStr,destinationStr,callback);

    }

    /**
     * 压缩
     * @param targetPaths 目标文件路径集合
     * @param fileName 压缩文件名称
     * @param callback
     */
    public static void zip(List<String> targetPaths, String fileName, IZipCallback callback){
        if(targetPaths.isEmpty()){
            callback.onError("目标文件不存在");
            return;
        }
        String destinationStr=new FileAddress().getPathZip(fileName);
        ZipManager.zip(targetPaths,destinationStr,"",callback);
    }

    /**
     *
     * @param targetZipFilePath  原Zip文件的的绝对文件路径
     * @param fileTargetPath  解压出来地址
     * @param callback
     */
    public static void unzip(String targetZipFilePath, String fileTargetPath, IZipCallback callback){

        if (!Zip4jUtil.isStringNotNullAndNotEmpty(targetZipFilePath) || !Zip4jUtil.isStringNotNullAndNotEmpty(fileTargetPath)) {
            if (callback != null) callback.onError("路径不能为空");
            return;
        }

        if(!new File(targetZipFilePath).exists()){
            if (callback != null) callback.onError("目标Zip不存在");
            return;
        }

        File unZipFile = new File(fileTargetPath);
        if(unZipFile.exists()){
            unZipFile.delete();
        }else {
            unZipFile.mkdirs();
        }
        //开始解压
        ZipManager.unzip(targetZipFilePath,fileTargetPath,callback);
    }

    /**
     *
     * @param targetZipFilePath  原Zip文件的的绝对文件路径
     * @param fileTargetPath  解压出来地址
     * @param callback
     */
    public static void unzip1(String targetZipFilePath, String fileTargetPath, IZipCallback callback){

        if (!Zip4jUtil.isStringNotNullAndNotEmpty(targetZipFilePath) || !Zip4jUtil.isStringNotNullAndNotEmpty(fileTargetPath)) {
            if (callback != null) callback.onError("路径不能为空");
            return;
        }

        if(!new File(targetZipFilePath).exists()){
            if (callback != null) callback.onError("目标Zip不存在");
            return;
        }

        File unZipFile = new File(fileTargetPath);
        if(!unZipFile.exists()){
            unZipFile.mkdirs();
        }
        //开始解压
        ZipManager.unzip(targetZipFilePath,fileTargetPath,callback);
    }
}
