package com.atguigu.yygh.controller.api;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.service.DepartmentService;
import com.atguigu.yygh.service.HospitalService;
import com.atguigu.yygh.service.HospitalSetService;
import com.atguigu.yygh.service.ScheduleService;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;

import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;


    //上传排班
    @PostMapping("saveSchedule")
    public Result saveShedule(HttpServletRequest request){
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        scheduleService.save(paramMap);
        return Result.ok();
    }

    //查询排班
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取科室信息
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //当前页
        int page = StringUtils.isEmpty(paramMap.get("page"))
                ? 1 : Integer.parseInt((String) paramMap.get("page"));
        //每页记录数
        int limit = StringUtils.isEmpty(paramMap.get("limit"))
                ? 1 : Integer.parseInt((String) paramMap.get("limit"));
        //TODO 签名校验
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel =scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }

    //删除排班
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        //医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String scheduleId = (String) paramMap.get("hosScheduleId");

        scheduleService.remove(hoscode,scheduleId);
        return Result.ok();

    }

    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取科室信息
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        //医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }


    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取科室信息
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        String hoscode = (String) paramMap.get("hoscode");
        //当前页
        int page = StringUtils.isEmpty(paramMap.get("page"))
                ? 1 : Integer.parseInt((String) paramMap.get("page"));
        //每页记录数
        int limit = StringUtils.isEmpty(paramMap.get("limit"))
                ? 1 : Integer.parseInt((String) paramMap.get("limit"));
        //TODO 签名校验
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> pageModel = departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);

    }

    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取科室信息
        Map<String,String[]> resultMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(resultMap);

        //验证数字签名
        String  hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");
        //得到医院系统传递过来的签名

//        String signKey = hospitalSetService.getSignKey1(hoscode);
//        //进行MD5加密
//        String signKeyMD5 = MD5.encrypt(signKey);
//        //判断
//        if(!hospSign.equals(signKeyMD5)){
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        departmentService.save(paramMap);
        return Result.ok();
    }

    //查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String  hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");
        //得到医院系统传递过来的签名

        String signKey = hospitalSetService.getSignKey1(hoscode);
        //进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);
        //判断
        if(!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //查询 根据医院编号进行查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);


    }

    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(requestMap);

        //得到医院系统传递过来的签名
        String hospSign = (String) stringObjectMap.get("sign");
        //查询数据库中的签名
        String hoscode = (String) stringObjectMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey1(hoscode);
        //进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);
        //判断
        if(!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //处理图片
        String logoData = (String) stringObjectMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        stringObjectMap.put("logoData",logoData);
        hospitalService.save(stringObjectMap);
        return Result.ok();
    }
}
