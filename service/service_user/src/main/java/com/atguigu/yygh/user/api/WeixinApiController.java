package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.user.utils.ConstantWXPropertiesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
     //生成微信扫描二维码
     @GetMapping("getLoginParam")
     @ResponseBody
     public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
         String redirectUri = URLEncoder.encode(ConstantWXPropertiesUtils.ACCESS_KEY_ID, "UTF-8");
         Map<String, Object> map = new HashMap<>();
         map.put("appid", ConstantWXPropertiesUtils.REGION_Id);
         map.put("redirectUri", redirectUri);
         map.put("scope", "snsapi_login");
         map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
         return Result.ok(map);
     }

    //回调的方法 得到信息
}
