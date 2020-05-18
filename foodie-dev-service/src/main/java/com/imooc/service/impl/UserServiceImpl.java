package com.imooc.service.impl;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBo;
import com.imooc.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.enums.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE="https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%BE%AE%E4%BF%A1%E5%A4%B4%E5%83%8F%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=11&spn=0&di=67760&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3302532285%2C1110653466&os=3500073870%2C1204996328&simid=3512568382%2C78602173&adpicid=0&lpn=0&ln=941&fr=&fmq=1588002273663_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fpic.soutu123.com%2Felement_origin_min_pic%2F16%2F10%2F21%2F09580975a96c462.jpg!%2Ffw%2F700%2Fquality%2F90%2Funsharp%2Ftrue%2Fcompress%2Ftrue&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bf57p78dn_z%26e3Bv54AzdH3Frg2AzdH3Fdb899nd_z%26e3Bip4s&gsm=c&rpstart=0&rpnum=0&islist=&querylist=&force=undefined";
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExit(String username) {
        Example userExample=new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        Users users = usersMapper.selectOneByExample(userExample);
        return users==null?false:true;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBo userBo) {

        Users users=new Users();
       users.setUsername(userBo.getUsername());
        try {
            users.setPassword(userBo.getPassword());
//            users.setPassword(MD5Utils.getMD5Str(userBo.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        users.setNickname(userBo.getUsername());
        users.setFace(USER_FACE);
        users.setBirthday(DateUtil.stringToDate("1999-11-11"));
        users.setSex(Sex.secret.type);
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());
        users.setId(UUID.randomUUID().toString());
        usersMapper.insert(users);
        return users;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample=new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);


        return result;
    }
}
