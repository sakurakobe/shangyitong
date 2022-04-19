package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.repository.DepartmentRepository;
import com.atguigu.yygh.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;

import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;



    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        String map = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(map, Department.class);

        Department existDepart = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        if(existDepart != null){
            existDepart.setUpdateTime(new Date());
            existDepart.setIsDeleted(0);
            departmentRepository.save(existDepart);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建pageable对象 设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page-1, limit);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        //创建example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                .withIgnoreCase(true);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }
    //删除医院科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            departmentRepository.deleteById(department.getId());
        }
    }
    /*
    医院排班显示 命名规范性可能带来的问题
     */
    @Override
    public List<DepartmentVo> findDepTree(String hoscode) {
        //创建list集合
        List<DepartmentVo> result = new ArrayList<>();
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室bigcode分组 获取每个大科室里面下级子科室
        Map<String, List<Department>> collect =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for(Map.Entry<String,List<Department>> entry:collect.entrySet()){
            String bigcode = entry.getKey();
            //大科室编号对应的全部的数据
            List<Department> departments = entry.getValue();
            //封装
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(departments.get(0).getBigname());
            //小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department1:departments){
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department1.getDepcode());
                departmentVo2.setDepname(department1.getDepname());
                children.add(departmentVo2);
            }
            //小科室的list集合放到大可是中
            departmentVo1.setChildren(children);
            //放到最终的result中
            result.add(departmentVo1);
        }
        return result;
    }
}
