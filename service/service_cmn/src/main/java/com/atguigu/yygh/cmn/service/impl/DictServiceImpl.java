package com.atguigu.yygh.cmn.service.impl;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
        implements DictService {
    @Override
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

    //判断id下面是否有子节点
    private boolean isChildren(long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer integer = baseMapper.selectCount(wrapper);
        return integer>0;
    }
}
