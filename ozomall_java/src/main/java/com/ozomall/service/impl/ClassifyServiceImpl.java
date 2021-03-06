package com.ozomall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ozomall.dao.ClassifyMapper;
import com.ozomall.entity.ClassifyDto;
import com.ozomall.entity.Result;
import com.ozomall.service.ClassifyService;
import com.ozomall.utils.Oss;
import com.ozomall.utils.ResultGenerate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassifyServiceImpl implements ClassifyService {
    @Resource
    private ClassifyMapper classifyMapper;

    /**
     * 添加分类
     */
    @Override
    public Result addClassify(ClassifyDto form) {
        int row = classifyMapper.insert(form);
        if (row > 0) {
            return ResultGenerate.genSuccessResult();
        } else {
            return ResultGenerate.genErroResult("新建失败，请重试。");
        }
    }


    /**
     * 上传分类图片
     */
    @Override
    public Result classifyUpload(MultipartFile file) throws IOException {
        Oss ossClient = new Oss();
        ossClient.init("ozomall-goods-pic", "classify/");
        String name = ossClient.uploadImg2Oss(file);
        String url = ossClient.getImgUrl(name);
        ossClient.destory();
        if (!StringUtils.isEmpty(url)) {
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return ResultGenerate.genSuccessResult(data);
        } else {
            return ResultGenerate.genErroResult("上传失败");
        }
    }

    /**
     * 查询类别分组
     */
    @Override
    public Result queryClassify(ClassifyDto form) {
        Map<SFunction<ClassifyDto, ?>, Object> params = new HashMap<>();
        LambdaQueryWrapper<ClassifyDto> wrapper = new LambdaQueryWrapper<>();
        params.put(ClassifyDto::getName, form.getName());
        params.put(ClassifyDto::getParentId, form.getParentId());
        params.put(ClassifyDto::getClassifyLevel, form.getClassifyLevel());
        params.put(ClassifyDto::getDel, form.getDel());
        wrapper.allEq(params, false);
        List<ClassifyDto> rows = classifyMapper.selectList(wrapper);
        if (rows != null) {
            return ResultGenerate.genSuccessResult(rows);
        } else {
            return ResultGenerate.genErroResult("数据获取失败");
        }
    }

    /**
     * 查询2、3级类别
     */
    @Override
    public Result queryChildrenList(ClassifyDto form) {
        List<ClassifyDto> rows = classifyMapper.childrenList(form.getId());
        if (rows != null) {
            return ResultGenerate.genSuccessResult(rows);
        } else {
            return ResultGenerate.genErroResult("数据获取失败");
        }
    }

    /**
     * 修改分类
     */
    @Override
    public Result putClassify(ClassifyDto form) {
        int row = classifyMapper.updateById(form);
        if (row > 0) {
            return ResultGenerate.genSuccessResult();
        } else {
            return ResultGenerate.genErroResult("新建失败，请重试。");
        }
    }

}
