package com.bonny.springbootmall.service;

import com.bonny.springbootmall.dto.UserLoginRequest;
import com.bonny.springbootmall.dto.UserRegisterRequest;
import com.bonny.springbootmall.model.User;

public interface UserService {

    // 查詢user數據
    User getUserById(Integer userId);

    // 註冊新帳號
    Integer register(UserRegisterRequest userRegisterRequest);

    // 登入功能
    User login(UserLoginRequest userLoginRequest);
}
