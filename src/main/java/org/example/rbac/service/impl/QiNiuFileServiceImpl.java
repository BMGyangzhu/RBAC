package org.example.rbac.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.example.rbac.config.QiNiuConfig;
import org.example.rbac.service.QiNiuFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bgmyangzhu
 * @date 2025/2/1 20:48
 */
@Service
public class QiNiuFileServiceImpl implements QiNiuFileService {
    
    @Autowired
    private QiNiuConfig qiNiuConfig;
    
    @Override
    public Map<String, String> getToken(){
        Auth auth = qiNiuConfig.buildAuth();
        String upToken = auth.uploadToken(qiNiuConfig.getBucketName());
        
        Map<String, String> result = new HashMap<>();
        result.put("token",upToken);
        result.put("domain", qiNiuConfig.getDomain());
        return result;
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        try {
            Configuration cfg = new Configuration(Region.beimei());
            UploadManager uploadManager = new UploadManager(cfg);
            
            Auth auth = qiNiuConfig.buildAuth();
            String upToken = auth.uploadToken(qiNiuConfig.getBucketName());
            
            // 上传文件流
            Response response = uploadManager.put(inputStream, fileName, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(),DefaultPutRet.class);
            
            return "http://200022.top" + "/" + putRet.key;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public FileInfo getFileInfo(String url) {
        Configuration cfg = new Configuration(Region.region2());
        final Auth auth = qiNiuConfig.buildAuth();
        final String bucket = qiNiuConfig.getBucketName();

        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            FileInfo fileInfo = bucketManager.stat(bucket, url);
            return fileInfo;
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return null;
    }
}
