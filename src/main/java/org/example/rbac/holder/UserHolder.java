package org.example.rbac.holder;

/**
 * @author bgmyangzhu
 * @date 2025/2/1 21:13
 */
public class UserHolder {
    
    private static ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();
    
    // 添加
    public static void set(Object id) { userThreadLocal.set(Long.valueOf(id.toString())); }
    
    // 获取
    public static Long get() { return userThreadLocal.get(); }
    
    // 删除
    public static void clear() { userThreadLocal.remove(); }
}
