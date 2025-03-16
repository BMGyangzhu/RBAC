package org.example.rbac.controller.shop;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.shop.ShopApplication;
import org.example.rbac.service.shop.ShopApplicationService;
import org.example.rbac.service.shop.ShopBusinessLogicService;
import org.example.rbac.util.Result;
import org.example.rbac.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bgmyangzhu
 * @date 2025/2/22 17:56
 */
@RestController
@CrossOrigin
@RequestMapping("/application")
public class ShopApplicationController {
    
    @Autowired
    private ShopApplicationService shopApplicationService;
    @Autowired
    private ShopBusinessLogicService shopBusinessLogicService;
    @Autowired
    private WebSocketServer webSocketServer;
    
    @PutMapping("/{id}")
    public Result approveApplication(@PathVariable Integer id,
                                     @RequestParam Integer userId,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String comment,
                                     @RequestParam Integer adminUserId) {
        boolean result = shopBusinessLogicService.approveApplication(id, userId,status, comment, adminUserId);
        return Result.success(result);
    }
    
    @PutMapping("/rejectApplication/{id}")
    public Result rejectApplication(@PathVariable Integer id,
                                    @RequestParam Integer userId,
                                    @RequestParam String status,
                                    @RequestParam(required = false) String comment,
                                    @RequestParam Integer adminUserId){
        boolean result = shopBusinessLogicService.rejectApplication(id, userId, status, comment, adminUserId);
        if (result){
            String rejectResult = "1";
            Map<String, String> map = new HashMap<>();
            map.put("result",rejectResult);
            map.put("userId", userId.toString());
            String json = JSON.toJSONString(map);
            webSocketServer.sendToAllClient(json);
            return Result.success(result);
        }
        return Result.error();
    }
    
    @PostMapping("/apply")
    public Result applyForShop(@RequestBody ShopApplication application){
        application.setStatus(Constants.PENDING);
        boolean result = shopApplicationService.applyForShop(application);
        
        if(result) {
            Long count = shopApplicationService.checkPendingApplication();
            
            Map<String, Long> map = new HashMap<>();
            map.put("badgeValue", count);
            String json = JSON.toJSONString(map);

            webSocketServer.sendToAllClient(json);
            
            return Result.success(result);
            
        }else{
            return Result.error();            
        }
        
    }
    
    @GetMapping("{userId}")
    public Result checkApplication(@PathVariable Integer userId){
        boolean result = shopApplicationService.checkApplication(userId);
        return Result.success(result);
    }
    
    @GetMapping("/checkPendingApplication")
    public Result checkPendingApplication(){
        Long count = shopApplicationService.checkPendingApplication();
//        Map map = new HashMap();
//        map.put("badgeValue", count);
//        String json = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(json);
        return Result.success(count);
    }
    
    @GetMapping("/checkRejectedApplication/{userId}")
    public Result checkRejectedApplication(@PathVariable Integer userId){
        boolean result = shopApplicationService.checkRejectedApplication(userId);
        return Result.success(result);
    }
    
    @GetMapping("/listApplicationInfo")
    public Result listApplicationInfo(){
        List<ShopApplication> shopApplications = shopBusinessLogicService.listApplicationInfo();
        return Result.success(shopApplications);
    }
}
