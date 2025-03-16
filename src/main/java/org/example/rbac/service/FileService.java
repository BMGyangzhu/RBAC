package org.example.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.File;

/**
 * @author bgmyangzhu
 * @date 2025/2/1 20:39
 */
public interface FileService extends IService<File> {

    Long saveImage(String fileKey, Long userId);

    /**
     * 获取文件真实URL
     * @param fileId 文件id
     * @return
     */
    File getImageUrl(Long fileId);
}
