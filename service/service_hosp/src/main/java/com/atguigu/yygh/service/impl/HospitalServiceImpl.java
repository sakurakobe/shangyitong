package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeighClient;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.repository.HospitalRepository;
import com.atguigu.yygh.service.HospitalService;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeighClient dictFeighClient;

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

    //根据医院编号进行查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable = PageRequest.of(page-1,limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //对象转换
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建
        Example<Hospital> example = Example.of(hospital,matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        //微服务调用
  
        //遍历
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String,Object>getHospById(String id) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = hospitalRepository.findById(id).get();
        Hospital hospital1 = this.setHospitalHosType(hospital);
        result.put("hospital",hospital1);
        result.put("bookingRule",hospital1.getBookingRule());
        hospital1.setBookingRule(null);
        return result;

    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return null;
    }

    private Hospital setHospitalHosType(Hospital item) {
        String hostypeString = dictFeighClient.getName("Hostype", item.getHostype());
        //查询省市地区
        String provinceString = dictFeighClient.getName(item.getProvinceCode());
        String cityString = dictFeighClient.getName(item.getCityCode());
        String districtString = dictFeighClient.getName(item.getDistrictCode());
        item.getParam().put("fullAddress",provinceString+cityString+districtString);
        item.getParam().put("hostypeString",hostypeString);
        return item;
    }
}
