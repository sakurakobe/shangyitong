package com.atguigu.yygh.common.utils;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

public class AuthContextHolder  {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request){
        String token = request.getHeader("token");
        //jwt获取id
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    //获取当前用户名称
    public static String getUserName(HttpServletRequest request){
        String token = request.getHeader("token");
        //jwt获取id
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
