package com.bonny.springbootmall.service;

import com.bonny.springbootmall.dto.CreateOrderRequest;
import com.bonny.springbootmall.dto.OrderQueryParams;
import com.bonny.springbootmall.model.Order;

import java.util.List;

public interface OrderService {

    // 取得 order list
    List<Order> getOrders(OrderQueryParams orderQueryParams);

    // 取得 order 總數
    Integer countOrder(OrderQueryParams orderQueryParams);

    // 查詢訂單資訊
    Order getOrderById(Integer orderId);

    // 創建訂單
    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);


}
