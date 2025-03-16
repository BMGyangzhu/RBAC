package org.example.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.Permission;
import org.example.rbac.domain.User;
import org.example.rbac.domain.dto.PasswordDTO;
import org.example.rbac.domain.dto.UserLoginDTO;
import org.example.rbac.exception.ServiceException;
import org.example.rbac.service.UserService;
import org.example.rbac.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.checkerframework.checker.units.qual.Prefix.one;

/**
 * @author bgmyangzhu
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-12-31 15:54:19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final Log LOG = Log.get();

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean changePassword(PasswordDTO passwordDTO) {
        String username = passwordDTO.getUsername();
        String oldPassword = passwordDTO.getOldPassword();
        String newPassword = passwordDTO.getNewPassword();
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, username)
                     .eq(User::getPassword, oldPassword)
                     .set(User::getPassword, newPassword);
        boolean result = update(updateWrapper);
        return result;
    }

    @Override
    public User register(UserLoginDTO userLoginDTO) {
        User one = getUserInfo(userLoginDTO);
        if (one != null) throw new ServiceException(Constants.CODE_600, "用户已存在");
        one = new User();
        BeanUtil.copyProperties(userLoginDTO, one, true);
        save(one);
        return one;
    }

    public User getUserInfo(UserLoginDTO userLoginDTO) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery(User.class);
        lambdaQueryWrapper.eq(User::getUsername, userLoginDTO.getUsername());
        lambdaQueryWrapper.eq(User::getPassword, userLoginDTO.getPassword());
        User one;
        try {
            one = getOne(lambdaQueryWrapper);
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    @Override
    public void delete(Integer id) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(User::getId, id)
                .set(User::getDeleted, 1)
                .set(User::getDeletedTime, System.currentTimeMillis());
        update(updateWrapper);
    }

    @Override
    public void deleteBatch(List<Integer> userIds) {
        // 设置删除时间戳
        List<User> users = listByIds(userIds);
        users.forEach(permission -> {
            permission.setDeleted(1);
            permission.setDeletedTime(System.currentTimeMillis());
        });
        // 使用update加wrapper的方法是因为 MyBatis-Plus 的批量更新方法updateBatchId不会修改deleted字段
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(User::getId, userIds)
                     .set(User::getDeleted, 1)
                     .set(User::getDeletedTime, System.currentTimeMillis());
        update(updateWrapper);
    }
}




