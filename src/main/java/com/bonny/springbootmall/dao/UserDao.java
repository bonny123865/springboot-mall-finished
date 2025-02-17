package com.bonny.springbootmall.dao;

import com.bonny.springbootmall.dto.UserRegisterRequest;
import com.bonny.springbootmall.model.User;

public interface UserDao {

    // 查詢user數據
    User getUserById(Integer userId);

    // 用前端的值去查詢資料庫是否有這筆user數據存在
    User getUserByEmail(String email);


    // 註冊新帳號
    Integer createUser(UserRegisterRequest userRegisterRequest);
}
