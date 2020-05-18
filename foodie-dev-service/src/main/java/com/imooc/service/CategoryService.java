package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {
    //查询一级分类
    public List<Category> queryAllRootLevelCat();

    //根据一级分类查询子分类
    public List<CategoryVO> getSubCatList(Integer rootId);

    //查询首页一级分类的六条最新数据
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootId);

}
