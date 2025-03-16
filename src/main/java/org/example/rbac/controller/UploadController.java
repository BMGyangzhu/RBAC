package org.example.rbac.controller;

import org.example.rbac.service.QiNiuFileService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 16:16
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    @Autowired
    private QiNiuFileService qiNiuFileService;

    /**
     * 获取七牛云上传 Token
     */
    @GetMapping("/getToken")
    public Result getToken() {
        String token = qiNiuFileService.getToken().get("token");
        return token != null ? Result.success(token) : Result.error();
    }

    /**
     * 上传图片到七牛云
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = "uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            String fileUrl = qiNiuFileService.uploadFile(inputStream, fileName);
            return Result.success(fileUrl); // 返回图片 URL
        } catch (IOException e) {
            return Result.error();
        }
    }
}
