package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo loginVo);

    void userAuth(Long userId, UserAuthVo userAuthVo);
}
