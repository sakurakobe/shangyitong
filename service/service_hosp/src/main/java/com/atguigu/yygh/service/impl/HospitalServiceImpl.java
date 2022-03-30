package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.repository.HospitalRepository;
import com.atguigu.yygh.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    //上传医院接口
    @Override
    public void save(Map<String, Object> stringObjectMap) {
        //判断是否存在数据。修改添加
        //参数map集合转换成一个对象 fastjson的作用
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        String hoscode = hospital.getHoscode();
        Hospital hospital1 = hospitalRepository.getHospitalByHoscode(hoscode);

        if(hospital1 != null){
            hospital.setStatus(hospital1.getStatus());
            hospital.setCreateTime(hospital1.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }
}
