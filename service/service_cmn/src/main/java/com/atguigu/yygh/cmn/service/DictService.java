package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;

import java.util.List;

public interface DictService {
    List<Dict> findChildData(long id);
}
