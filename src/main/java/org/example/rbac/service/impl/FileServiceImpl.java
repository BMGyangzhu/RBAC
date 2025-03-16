//package org.example.rbac.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.qiniu.storage.model.FileInfo;
//import org.example.rbac.config.LocalCache;
//import org.example.rbac.config.QiNiuConfig;
//import org.example.rbac.domain.File;
//import org.example.rbac.exception.BaseException;
//import org.example.rbac.mapper.FileMapper;
//import org.example.rbac.service.FileService;
//import org.example.rbac.service.QiNiuFileService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//import java.util.UUID;
//
///**
// * 图片文件存储服务实现
// *
// * @author bgmyangzhu
// * @date 2025/2/1 20:43
// */
//@Service
//public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
//
//    @Autowired
//    private QiNiuFileService qiNiuFileService;
//
//    @Override
//    public Long saveImage(String fileKey, Long userId) {
//        // 获取文件信息
//        final FileInfo fileInfo = qiNiuFileService.getFileInfo(fileKey);
//        if (fileInfo == null) {
//            throw new IllegalArgumentException("文件不存在或已失效");
//        }
//
//        // 验证文件是否为图片
//        String mimeType = fileInfo.mimeType;
//        if (!mimeType.startsWith("image/")) {
//            throw new IllegalArgumentException("仅支持存储图片文件");
//        }
//
//        // 创建图片文件记录
//        final File imageFile = new File();
//        imageFile.setFileKey(fileKey);
//        imageFile.setFormat(mimeType);
//        imageFile.setType("图片");
//        imageFile.setUserId(userId);
//        imageFile.setSize(fileInfo.fsize);
//        save(imageFile);
//
//        return imageFile.getId();
//    }
////
////    @Override
////    public File getImageUrl(Long fileId) {
////        File file = getById(fileId);
////        if (Objects.isNull(file)) {
////            throw new BaseException("未找到该图片");
////        }
////
////        // 生成唯一 URL 防止缓存
////        final String uniqueToken = UUID.randomUUID().toString();
////        LocalCache.put(uniqueToken, true);
////        String url = QiNiuConfig.CNAME + "/" + file.getFileKey();
////
////        url = url.contains("?") ? url + "&uuid=" + uniqueToken : url + "?uuid=" + uniqueToken;
////        file.setFileKey(url);
////
////        return file;
////    }
//}
