package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
        implements DictService {


    @Override
//    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置haschildren
        for (Dict dict :dicts){
            long id1 = dict.getId();
            boolean isChild = this.isChildren(id1);
            dict.setHasChildren(isChild);

        }
        return dicts;
    }

    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        List<Dict> dicts = baseMapper.selectList(null);
        //转换
        List<DictEeVo> list = new ArrayList<>();
        for(Dict dict:dicts){
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            list.add(dictEeVo);
        }

        //调用方法进行写入操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
//    @CacheEvict(value = "dict",allEntries = true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet()
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //判断id下面是否有子节点
    private boolean isChildren(long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer integer = baseMapper.selectCount(wrapper);
        return integer>0;
    }
    //根据dictcode和value查询
    @Override
    public String getDictName(String s, String value) {
        if(StringUtils.isEmpty(s)){
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else{
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("dict_code",s);
            Dict dict = baseMapper.selectOne(wrapper);
            Long id = dict.getId();
            //根据id和value值进行查询
            Dict dict1 = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", id)
                    .eq("value", value));
            return dict1.getName();
        }

    }
}
