package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController extends BaseController{

    @Autowired
    private RedisOperator redisOperator;
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        // TODO 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //需要判断当前购物车中是否已经包含已经存在商品   若存在则累加购买数量
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartList=new ArrayList<>();
        if (StringUtil.isNotEmpty(shopcartJson)){
            //购物车已有数据
            shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            //判断购物车是否存在已有商品
            boolean isHaving=false;
            for(ShopcartBO sc:shopcartList){
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())){
                    sc.setBuyCounts(sc.getBuyCounts()+shopcartBO.getBuyCounts());
                    isHaving=true;
                }
            }
            if (!isHaving){
                shopcartList.add(shopcartBO);
            }
        }else {
            //购物车没有数据
            shopcartList=new ArrayList<>();
            //直接添加购物车
            shopcartList.add(shopcartBO);
        }
        //覆盖现有购物车
        redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(shopcartList));
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }

        // TODO 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除后端REDIS购物车中的商品
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if(StringUtil.isNotEmpty(shopcartJson)){
            List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            for (ShopcartBO sc:shopcartBOList){
                if (sc.getSpecId().equals(itemSpecId)){
                    shopcartBOList.remove(sc);
                    break;
                }
            }
            //覆盖现有购物车
            redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(shopcartBOList));
        }


        return IMOOCJSONResult.ok();
    }

}
