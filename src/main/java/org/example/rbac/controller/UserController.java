package org.example.rbac.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.Role;
import org.example.rbac.domain.RoleUser;
import org.example.rbac.domain.User;
import org.example.rbac.domain.dto.BaseUserDTO;
import org.example.rbac.domain.dto.PasswordDTO;
import org.example.rbac.domain.dto.UserDTO;
import org.example.rbac.domain.dto.UserLoginDTO;
import org.example.rbac.domain.vo.UserVO;
import org.example.rbac.service.RbacService;
import org.example.rbac.service.RoleService;
import org.example.rbac.service.UserService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2024/12/31 16:06
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
@Transactional
public class UserController {
    
    @Autowired
    RbacService rbacService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    RoleService roleService;

    private boolean validateUserDTO(UserLoginDTO userLoginDTO) {
        return StrUtil.isNotBlank(userLoginDTO.getUsername()) && StrUtil.isNotBlank(userLoginDTO.getPassword());
    }

    @PostMapping("/login")
    public Result rbacLogin(@RequestBody UserLoginDTO userLoginDTO) {
        if (!validateUserDTO(userLoginDTO)) {
            return Result.error(Constants.CODE_400, "用户名或密码不能为空");
        }
        UserVO user = rbacService.login(userLoginDTO);
        if (user == null) return Result.error("401","账号或密码错误");
        return Result.success(user);
    }
//    @GetMapping
//    public Result findAll(){
//        List<User> list = rbacService.listUsers();
//        return Result.success(list);
//    }

    @PostMapping
    @Transactional
    public Result save(@RequestBody UserDTO userDTO) {
        // 新增或者更新
        User user = BeanUtil.copyProperties(userDTO, User.class, "roles");
        rbacService.deleteRoleUserByUserId(userDTO.getId());
        rbacService.updateUserRoles(userDTO.getId(), userDTO.getRoles());
        userService.saveOrUpdate(user);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
         rbacService.deleteRoleUserByUserId(id);
         userService.delete(id);
         return Result.success();
    }
    
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
         rbacService.deleteRoleUserByUserIds(ids);
         userService.deleteBatch(ids);
         return Result.success();
    }
    
   
    
    @PostMapping("/add")
    @Transactional
    public Result addUser(@RequestBody UserDTO userDTO){
        boolean result = rbacService.saveUser(userDTO);
        if (result) return Result.success();
        return Result.error("400","该用户名已存在");
    }
    
    
    @GetMapping("/page")
    public Result  findPage(@RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam(defaultValue = "") String username,
                                       @RequestParam(defaultValue = "") String email,
                                       @RequestParam(defaultValue = "") String address) {
        IPage<User> page = Page.of(pageNum,pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery(User.class);
        if(!"".equals(username)){
            lambdaQueryWrapper.like(User::getUsername,username);
        }
        if(!"".equals(email)){
            lambdaQueryWrapper.like(User::getEmail,email);
        }
        if(!"".equals(address)){
            lambdaQueryWrapper.like(User::getAddress,address);
        }
        lambdaQueryWrapper.orderByAsc(User::getId);
        IPage<User> iPage = userService.page(page, lambdaQueryWrapper);
        List<User> records = iPage.getRecords();
        List<User> users = rbacService.listUsers(records);
        iPage.setRecords(users);
        return Result.success(iPage);
    }
    


    @PostMapping("/register")
    public Result register(@RequestBody UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO userDTO = BeanUtil.copyProperties(userLoginDTO, UserDTO.class);
        String result = rbacService.register(userDTO);
        if (Objects.equals(result, "注册成功")) return Result.success();
        
        return Result.error("400","该账号已被占用");
    }

    @GetMapping("/{username}")
    public Result findByUsername(@PathVariable String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery(User.class);
        lambdaQueryWrapper.eq(User::getUsername,username);
        User one = userService.getOne(lambdaQueryWrapper);
        Integer userId = one.getId();
        List<RoleUser> roleUsers = roleService.listByUserId(userId);
        Set<Integer> roleIds = roleUsers.stream().map(RoleUser::getRoleId).collect(Collectors.toSet());
        List<Role> roles = roleService.listByIds(roleIds);
        Set<Role> roleSet = new HashSet<>(roles);
        one.setRoles(roleSet);
        return Result.success(one);
    }

    @PostMapping("/password")
    public Result changePassword(@RequestBody PasswordDTO passwordDTO) {
        boolean result = userService.changePassword(passwordDTO);
        return result ? Result.success() : Result.error("400","修改失败");
    }
    
    @PostMapping("/person")
    public Result modifyPersonInfo(@RequestBody BaseUserDTO baseUserDTO) {
        User user = BeanUtil.copyProperties(baseUserDTO, User.class);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername,user.getUsername())
                .set(User::getNickname,user.getNickname())
                .set(User::getEmail,user.getEmail())
                .set(User::getAddress,user.getAddress());
        boolean result = userService.update(updateWrapper);
        if (result) return Result.success();
        return Result.error("400","修改个人信息失败");
    }



}
