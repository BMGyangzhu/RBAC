package org.example.rbac.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author bgmyangzhu
 * @date 2025/3/11 1:03
 */
@Data
public class OrderResult {

    /**
     * 订单码
     */
    Long orderCode;

    /**
     * 支付完成时间
     */
    LocalDateTime payedTime;

    /**
     * 订单号
     */
    String orderId;

}
