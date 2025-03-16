//package org.example.rbac.controller;
//
//import cn.hutool.http.server.HttpServerRequest;
//import org.apache.ibatis.annotations.Param;
//import org.example.rbac.config.QiNiuConfig;
//import org.example.rbac.domain.File;
//import org.example.rbac.holder.UserHolder;
//import org.example.rbac.service.FileService;
//import org.example.rbac.util.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author bgmyangzhu
// * @date 2025/2/1 20:19
// */
//@RestController
//@CrossOrigin
//@RequestMapping("/file")
//public class FileController {
//    
//    @Autowired
//    FileService fileService;
//    
//    @Autowired
//    QiNiuConfig qiNiuConfig;
//    
//    
//    @PostMapping
//    public Result save(String fileKey){
//        
//        return Result.success(fileService.save(fileKey, UserHolder.get()));
//    }
//    
//    @GetMapping("/getToken")
//    public Result token(String type){
//        
//        return Result.success(qiNiuConfig.uploadToken(type));
//    }
//    
//    @GetMapping("{fileId}")
//    public void getUUid(HttpServletRequest request, HttpServletResponse response, @PathVariable Long fileId) throws IOException{
//
//        File url = fileService.getFileTrustUrl(fileId);
//        response.setContentType(url.getType());
//        response.sendRedirect(url.getFileKey());
//    }
//}
