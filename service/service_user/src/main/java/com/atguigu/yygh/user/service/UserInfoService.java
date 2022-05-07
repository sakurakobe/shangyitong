package com.atguigu.yygh.user.service;

import com.atguigu.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);
}
