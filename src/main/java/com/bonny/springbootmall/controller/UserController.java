package com.bonny.springbootmall.controller;

import com.bonny.springbootmall.dto.UserLoginRequest;
import com.bonny.springbootmall.dto.UserRegisterRequest;
import com.bonny.springbootmall.model.User;
import com.bonny.springbootmall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 創建 : 對應到 POST 方法，隱密的東西需要用 "RequestBody" 傳遞參數 (POST)
    // 註冊新帳號，類似於新增商品 (createProduct)
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        // register方法會在資料庫創建一筆新的 user 數據，會返回資料庫生成的 userId 給我們
        Integer userId = userService.register(userRegisterRequest);

        // 預期 userService 會提供一個getUserById方法，可以根據傳進去userId的參數去資料庫中查詢這一筆user數據
        User user = userService.getUserById(userId);

        // 回傳給前端201
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // 登入功能，會將帳密回傳到系統，所以適合用 POST、(PUT強烈表達更新已存在數據)
    // 創建新的 class 來實作登入功能
    @PostMapping("/users/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
