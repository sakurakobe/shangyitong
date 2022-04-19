package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DictService extends IService<Dict> {
    List<Dict> findChildData(long id);

    void exportDictData(HttpServletResponse response);

    void importData(MultipartFile file);

    String getDictName(String s, String value);

    List<Dict> findByDictCode(String dictCode);
}
