package org.example.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.domain.Permission;
import org.example.rbac.domain.RolePermission;
import org.example.rbac.service.PermissionService;
import org.example.rbac.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @description 针对表【permission】的数据库操作Service实现
 * @createDate 2024-12-31 15:54:19
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public List<RolePermission> listByRoleIds(List<Integer> roleIds) {

        return permissionMapper.listByRoleIds(roleIds);
    }

    @Override
    public Set<Permission> buildPermissionTree(Set<Permission> permissions) {
        // 查询整张权限表，构建完整的父子关系
        List<Permission> allPermissions = this.list();

        // 使用 Map 缓存所有父Id -> 子节点列表，分组依据是 parentId
        Map<Integer, List<Permission>> childrenMap = allPermissions.stream()
                                                                   .collect(Collectors.groupingBy(Permission::getParentId));

        // 创建一个新的集合用于存储处理后的权限树
        Set<Permission> processedPermissions = permissions.stream()
                                                          .map(Permission::deepCopyPermission) // 深拷贝每个 Permission
                                                          .collect(Collectors.toSet());
        
        Set<Permission> permissionSet = processedPermissions.stream()
                                              .filter(permission -> permission.getParentId() == 0)
                                              .collect(Collectors.toSet());
        // 遍历输入的权限集合，为每个权限递归设置子权限
        permissionSet.forEach(permission -> doSetChildren(permission, childrenMap, permissions, processedPermissions));

        return permissionSet;
    }

    /**
     * 递归设置子权限
     *
     * @param parent               当前父权限
     * @param childrenMap          父Id -> 子权限列表的映射
     * @param originalPermissions  输入的权限集合，用于判断哪些节点需要构建
     * @param processedPermissions 动态构建的结果集合
     */
    private void doSetChildren(Permission parent, Map<Integer, List<Permission>> childrenMap,
                               Set<Permission> originalPermissions, Set<Permission> processedPermissions) {
        // 获取当前节点的子节点
        List<Permission> children = childrenMap.getOrDefault(parent.getId(), Collections.emptyList());

        // 仅保留输入集合 originalPermissions 中存在的子节点
        List<Permission> filteredChildren = children.stream()
                                                    .filter(originalPermissions::contains)
                                                    .collect(Collectors.toList());

        // 转换为 Set 以保证唯一性，并设置到父节点
        Set<Permission> childrenSet = new HashSet<>(filteredChildren);
        parent.setChildren(childrenSet);


        // 递归处理每个子节点
        childrenSet.forEach(child -> doSetChildren(child, childrenMap, originalPermissions, processedPermissions));
    }


    @Override
    public Set<Permission> buildAllPermissionTree(Set<Permission> permissions) {
        // 查询整张权限表，构建完整的父子关系
        List<Permission> allPermissions = this.list();

        // 使用 Map 缓存所有父Id -> 子节点列表，分组依据是 parentId
        Map<Integer, List<Permission>> childrenMap = allPermissions.stream()
                                                                   .collect(Collectors.groupingBy(Permission::getParentId));

        // 遍历输入的权限集合
        permissions.forEach(permission -> doSetChildren(permission, childrenMap));


        return permissions;
    }

    private void doSetChildren(Permission parent, Map<Integer, List<Permission>> childrenMap) {
        // 获取当前节点的子节点
        List<Permission> children = childrenMap.getOrDefault(parent.getId(), Collections.emptyList());

        Set<Permission> childrenSet = new HashSet<>(children);
        parent.setChildren(childrenSet);

        //递归处理每个子节点
        childrenSet.forEach(child -> doSetChildren(child, childrenMap));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
//        最简单的做法，即性能最差的做法        
//        方法 1（简单递归删除）：适用于数据量小、层级较浅的简单场景。
//        Permission permission = getById(id);
//        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Permission::getParentId,id);
//        List<Integer> childrenIds = list(lambdaQueryWrapper)
//                .stream()
//                .map(Permission::getId)
//                .collect(Collectors.toList());
//        childrenIds.forEach(this::delete);
//        removeById(permission.getId());
        
//        递归收集子节点， 减少访问数据库的次数，解决栈溢出的问题，提高性能
//        方法 2（递归收集子节点）：适用于层级较浅、数据量适中的场景，避免了频繁的数据库访问。
//        Permission permission = getById(id);
//
//        // 查询所有子节点
//        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Permission::getParentId, id);
//        List<Permission> children = list(lambdaQueryWrapper);
//
//        // 递归删除子节点
//        List<Integer> childrenIds = new ArrayList<>();
//        for (Permission child : children) {
//            childrenIds.add(child.getId());
//            // 递归查询并删除子节点
//            childrenIds.addAll(getChildrenIds(child.getId()));
//        }
//
//        // 批量删除所有子节点
//        if (!childrenIds.isEmpty()) {
//            removeByIds(childrenIds);
//        }
//
//        // 删除当前权限项
//        removeById(permission.getId());


//        使用队列来存储需要删除的权限项，最优性能
//        方法 3（队列遍历删除）：适用于数据量大、层级深的复杂场景，性能最好且适应性最强。
//        方法 3 使用队列（广度优先搜索，BFS）替代递归（深度优先搜索，DFS）
        Queue<Integer> permissionQueue = new LinkedList<>();
        permissionQueue.offer(id);

        // 查询所有子权限项
        List<Integer> allChildrenIds = getChildrenIds(id);  // 获取所有子节点ID

        // 将所有子权限项加入队列
        permissionQueue.addAll(allChildrenIds);

        // 使用一个集合来存储所有需要删除的ID
        List<Integer> idsToDelete = new ArrayList<>();

        // 遍历队列，收集需要删除的所有ID
        while (!permissionQueue.isEmpty()) {
            Integer currentId = permissionQueue.poll();
            idsToDelete.add(currentId);
        }

        // 批量删除所有权限项
        if (!idsToDelete.isEmpty()) {
            deleteBatch(idsToDelete);
            permissionMapper.deleteRolePermissionByPermissionId(idsToDelete);
        }
    }

    // 递归获取所有子节点ID
    private List<Integer> getChildrenIds(Integer parentId) {
        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Permission::getParentId, parentId);
        List<Permission> children = list(lambdaQueryWrapper);

        List<Integer> childrenIds = new ArrayList<>();
        for (Permission child : children) {
            childrenIds.add(child.getId());
            // 递归获取该节点的所有子节点ID
            childrenIds.addAll(getChildrenIds(child.getId()));
        }

        return childrenIds;
    }

    @Transactional
    public void deleteBatch(List<Integer> permissionIds) {
        // 设置删除时间戳
        List<Permission> permissions = listByIds(permissionIds);
        permissions.forEach(permission -> {
            permission.setDeleted(1);
            permission.setDeletedTime(System.currentTimeMillis());
        });
        // 使用update加wrapper的方法是因为 MyBatis-Plus 的批量更新方法updateBatchId不会修改deleted字段
        LambdaUpdateWrapper<Permission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Permission::getId, permissionIds)
                     .set(Permission::getDeleted, 1)
                     .set(Permission::getDeletedTime, System.currentTimeMillis());
        update(updateWrapper);

    }
    
    @Override
    public boolean existsByComment(String comment) {
        // 根据权限的 comment 查询是否存在重复项
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment", comment);
        return permissionMapper.selectCount(queryWrapper) > 0;
    }

}




