package com.ozomall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ozomall.dao.GoodsMapper;
import com.ozomall.dao.GoodsPicMapper;
import com.ozomall.entity.GoodsDto;
import com.ozomall.entity.GoodsPicDto;
import com.ozomall.entity.Result;
import com.ozomall.service.GoodsService;
import com.ozomall.utils.Oss;
import com.ozomall.utils.ResultGenerate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsPicMapper goodsPicMapper;

    /**
     * 添加商品信息
     *
     * @param form
     */
    @Override
    public Result addGoods(GoodsDto form) {
        int row = goodsMapper.insert(form);
        if (row > 0) {
            return ResultGenerate.genSuccessResult(form);
        } else {
            return ResultGenerate.genErroResult("商品信息添加失败，请重试！");
        }
    }

    /**
     * 获取商品列表
     *
     * @param form
     */
    @Override
    public Result goodsList(GoodsDto form) {
        Page page = new Page();
        page.setCurrent(form.getPage());
        page.setSize(form.getSize());
        IPage<Map> rows = goodsMapper.goodsList(page, form);
        if (rows != null) {
            return ResultGenerate.genSuccessResult(rows);
        } else {
            return ResultGenerate.genErroResult("商品信息添加失败，请重试！");
        }
    }

    /**
     * 根据id获取商品信息
     *
     * @param id
     */
    @Override
    public Result getGoods(int id) {
        GoodsDto rows = goodsMapper.getGoodsById(id);
        if (rows != null) {
            return ResultGenerate.genSuccessResult(rows);
        } else {
            return ResultGenerate.genErroResult("商品信息添加失败，请重试！");
        }
    }

    /**
     * 修改商品信息
     *
     * @param form
     */
    @Override
    public Result putGoods(GoodsDto form) {
        int rows = goodsMapper.updateById(form);
        if (rows > 0) {
            return ResultGenerate.genSuccessResult();
        } else {
            return ResultGenerate.genErroResult("更新失败");
        }
    }

    /**
     * 删除商品信息
     *
     * @param form
     */
    @Override
    public Result delGoods(GoodsDto form) {
        LambdaQueryWrapper<GoodsDto> wrapper = new LambdaQueryWrapper();
        wrapper.eq(GoodsDto::getId, form.getId());
        GoodsDto query = new GoodsDto();
        query.setDel(1);
        int rows = goodsMapper.update(query, wrapper);
        if (rows > 0) {
            return ResultGenerate.genSuccessResult();
        } else {
            return ResultGenerate.genErroResult("删除失败");
        }
    }

    /**
     * 上传商品图片
     *
     * @param file
     */
    @Override
    public Result upload(MultipartFile file, int goodsId) throws IOException {
        Oss ossClient = new Oss();
        ossClient.init("ozomall-goods-pic", "goodsPic/");
        //将文件上传
        String name = ossClient.uploadImg2Oss(file);
        //获取文件的URl地址  以便前台  显示
        String imgUrl = ossClient.getImgUrl(name);
        ossClient.destory();
        GoodsPicDto pic = new GoodsPicDto();
        pic.setGoodsId(goodsId);
        pic.setUrl(imgUrl);
        pic.setName(name);
        // 存储图片链接到数据库
        int rows = goodsPicMapper.insert(pic);
        if (rows > 0) {
            return ResultGenerate.genSuccessResult(pic);
        } else {
            return ResultGenerate.genErroResult("图片上传失败,请重新上传。");
        }
    }

    /**
     * 获取商品图片
     *
     * @param form
     */
    @Override
    public Result getGoodsPic(GoodsPicDto form) {
        LambdaQueryWrapper<GoodsPicDto> wrapper = new LambdaQueryWrapper();
        wrapper.eq(GoodsPicDto::getGoodsId, form.getGoodsId());
        List<GoodsPicDto> rows = goodsPicMapper.selectList(wrapper);
        if (rows != null) {
            return ResultGenerate.genSuccessResult(rows);
        } else {
            return ResultGenerate.genErroResult("获取失败");
        }
    }

    /**
     * 删除商品图片
     *
     * @param form
     */
    @Override
    public Result delGoodsPic(GoodsPicDto form) {
        Oss ossClient = new Oss();
        ossClient.init("ozomall-goods-pic", "goodsPic/");
        int rows = goodsPicMapper.deleteById(form.getId());
        if (rows > 0) {
            ossClient.delPic(form.getName());
            ossClient.destory();
            return ResultGenerate.genSuccessResult();
        } else {
            return ResultGenerate.genErroResult("删除失败");
        }
    }

    /**
     * 上传商品详情图片
     *
     * @param file
     */
    @Override
    public Result detailsUpload(MultipartFile file) throws IOException {
        Oss ossClient = new Oss();
        ossClient.init("ozomall-goods-pic", "goodsDetailsPic/");
        //将文件上传
        String name = ossClient.uploadImg2Oss(file);
        //获取文件的URl地址  以便前台  显示
        String imgUrl = ossClient.getImgUrl(name);
        ossClient.destory();
        if (!StringUtils.isEmpty(imgUrl)) {
            GoodsPicDto pic = new GoodsPicDto();
            pic.setUrl(imgUrl);
            pic.setName(name);
            return ResultGenerate.genSuccessResult(pic);
        } else {
            return ResultGenerate.genErroResult("图片上传失败,请重新上传。");
        }
    }

}