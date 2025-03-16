package org.example.rbac.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * WebSocket 服务器：用于向商家发送订单提醒
 *
 * @author bgmyangzhu
 * @date 2025/3/11
 */
@Component
@ServerEndpoint("/ws/order/{shopId}")
@Slf4j
public class OrderWebSocketServer {

    /**
     * 存储 shopId 对应的 WebSocket 连接
     */
    private static final Map<String, List<Session>> sessionMap = new HashMap<>();

    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("shopId") String shopId) {
        log.info("WebSocket 连接建立: shopId = {}, sessionId = {}", shopId, session.getId());
        synchronized (sessionMap) {
            sessionMap.computeIfAbsent(shopId, k -> new ArrayList<>()).add(session);
        }
        log.info("当前 sessionMap 状态: {}", sessionMap);
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose(Session session, @PathParam("shopId") String shopId) {
        log.info("WebSocket 连接断开: shopId = {}, sessionId = {}", shopId, session.getId());
        synchronized (sessionMap) {
            List<Session> sessions = sessionMap.get(shopId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionMap.remove(shopId);
                }
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("shopId") String shopId) {
        log.error("WebSocket 错误: shopId = {}, sessionId = {}, 错误信息: {}", shopId, session.getId(), error.getMessage());
        onClose(session, shopId);
    }

    /**
     * 发送订单提醒给指定 shopId 的所有 WebSocket 客户端
     */
    public void sendOrderAlert(Long shopId, String message) {
        log.info("向 shopId={} 发送消息: {}", shopId, message);

        List<Session> sessions;
        synchronized (sessionMap) {
            sessions = sessionMap.get(shopId.toString());
        }

        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                        log.info("消息发送成功, sessionId = {}", session.getId());
                    } catch (IOException e) {
                        log.error("发送消息失败, sessionId = {}, 错误: {}", session.getId(), e.getMessage());
                    }
                }
            }
        } else {
            log.warn("没有找到 shopId={} 的客户端连接, 消息未发送", shopId);
        }
    }
}
