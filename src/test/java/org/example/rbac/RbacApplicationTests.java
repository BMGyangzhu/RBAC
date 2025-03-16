package org.example.rbac;

import org.example.rbac.constant.Constants;
import org.example.rbac.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RbacApplicationTests {
    
    @Autowired
    RoleMapper roleMapper;

    @Test
    void contextLoads() {
        System.out.println(Constants.PENDING);
    }

}
