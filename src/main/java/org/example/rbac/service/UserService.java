package org.example.rbac.service;

import org.example.rbac.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.dto.PasswordDTO;
import org.example.rbac.domain.dto.UserLoginDTO;
import org.example.rbac.domain.vo.UserVO;

import java.util.List;

/**
* @author bgmyangzhu
* @description 针对表【user】的数据库操作Service
* @createDate 2024-12-31 15:54:19
*/
public interface UserService extends IService<User> {

    User register(UserLoginDTO userLoginDTO);

    void delete(Integer id);

    void deleteBatch(List<Integer> userIds);

    boolean changePassword(PasswordDTO passwordDTO);
    
}
