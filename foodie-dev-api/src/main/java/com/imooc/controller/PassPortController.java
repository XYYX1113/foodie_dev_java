package com.imooc.controller;



import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBo;
import com.imooc.service.UserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "登录注册",tags = "用于注册登录接口")
@RestController
@RequestMapping("passport")
public class PassPortController {

    @Autowired
    private UserService userService;
    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @RequestMapping("/usernameIsExit")
    public IMOOCJSONResult usernameIsExit(@RequestParam String username){
                            //RequestParam  解释：请求类型参数

       if(StringUtil.isEmpty(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
       }
        boolean nameIsExit = userService.queryUserNameIsExit(username);
       if (nameIsExit){
           return IMOOCJSONResult.errorMsg("用户名已存在");
       }

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @RequestMapping("/regist")
    public IMOOCJSONResult registUser(@RequestBody UserBo userBo,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        String password = userBo.getPassword();
        String username = userBo.getUsername();
        String confirmPassword = userBo.getConfirmPassword();
        //判断用户密码不为空
        if(StringUtils.isEmpty(password)||StringUtils.isEmpty(username)||StringUtils.isEmpty(confirmPassword)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空11");
        }
        //判断长度大于6
        if(password.length()<=6){
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }

        //判断两次密码是否一致
        if (!password.equals(confirmPassword)){
            return IMOOCJSONResult.errorMsg("两次密码不一致");
        }
        //查询用户是否存在
        boolean nameIsExit = userService.queryUserNameIsExit(username);
        if (nameIsExit){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        //注册
        Users userResult=userService.createUser(userBo);
         userResult= setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",JsonUtils.objectToJson(userResult),true);


        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")
    @RequestMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBo userBo,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String password = userBo.getPassword();
        String username = userBo.getUsername();
        //判断用户密码不为空
        if(StringUtils.isEmpty(password)||StringUtils.isEmpty(username)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空11");
        }

        //登录
        String md5Str = MD5Utils.getMD5Str(password);
        Users userResult = userService.queryUserForLogin(username,
                    password);
        if (userResult==null){
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }

        userResult= setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",JsonUtils.objectToJson(userResult),true);

        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据


        return IMOOCJSONResult.ok(userResult);
    }
    private Users setNullProperty(Users userResult){
        userResult.setPassword(null);
//        userResult.setUsername(null);
        return userResult;
    }










    @ApiOperation(value = "用户退出",notes = "用户退出",httpMethod = "POST")
    @RequestMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        //用户退出需清空购物车
        //分布式会话清空cookie
        CookieUtils.deleteCookie(request,response,"user");
        return IMOOCJSONResult.ok();
    }



}

