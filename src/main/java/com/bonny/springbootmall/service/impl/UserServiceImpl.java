package com.bonny.springbootmall.service.impl;

import com.bonny.springbootmall.dao.UserDao;
import com.bonny.springbootmall.dto.UserLoginRequest;
import com.bonny.springbootmall.dto.UserRegisterRequest;
import com.bonny.springbootmall.model.User;
import com.bonny.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserServiceImpl implements UserService {

    // 利用 "log" 來紀錄檢查資訊
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    // 查詢user數據
    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    // 註冊新帳號
    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {

        // 檢查註冊的 email
        // 先對前端的email做檢測，如果已存在就回400給前端
        // 用前端的值去查詢資料庫是否有這筆user數據存在
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        if (user != null) {
            // 參數值會塞入{}內
            log.warn("該 email: {} 已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用 MD5 生成密碼的雜湊值
        // .getBytes()將字串轉換成byte類型
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());

        // 將密碼轉成雜湊值後存入userRegisterRequest
        userRegisterRequest.setPassword(hashedPassword);

        // 創建帳號
        // 如果user不為空，就不會執行這行
        return userDao.createUser(userRegisterRequest);
    }

    // 登入功能
    // 在Service層當中，多增加 if-else 的判斷，不要加在 Dao層
    @Override
    public User login(UserLoginRequest userLoginRequest) {

        // 檢查 user 是否存在
        // 先到資料庫查有沒有相同email
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        // 沒有註冊過
        if (user == null) {
            log.warn("該 email: {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用 MD5 生成密碼的雜湊值
        // .getBytes()將字串轉換成byte類型
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());


        // 要用 "equals" 比對密碼(資料庫與前端值比較)
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        } else {
            log.warn("email: {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
