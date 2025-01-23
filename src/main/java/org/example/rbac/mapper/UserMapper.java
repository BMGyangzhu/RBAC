package org.example.rbac.mapper;

import org.example.rbac.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author bgmyangzhu
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-12-31 15:54:19
* @Entity org.example.rbac.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    void deleteBatch(List<Integer> userIds);
}




