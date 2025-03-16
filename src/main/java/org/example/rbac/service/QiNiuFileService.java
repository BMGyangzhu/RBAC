package org.example.rbac.service;

import com.qiniu.storage.model.FileInfo;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author bgmyangzhu
 * @date 2025/2/1 20:45
 */
public interface QiNiuFileService{

    Map<String, String> getToken();

    /**
     * 获取签名
     * @return
     */
//    String getToken();

    String uploadFile(InputStream inputStream, String fileName);

    /**
     * 删除文件
     * @param url
     */
//    void deleteFile(String url);

    /**
     * 获取文件信息
     * @param url
     * @return
     */
    FileInfo getFileInfo(String url);
}
