package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    String url = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登陆
     * 1.使用http client 发请求，发生code+appid+secret
     * 2.看返回的openid（string）是否有值
     *  2。1 如果为空，登陆失败
     *  2。2 如果有值，登陆成功
     *  2。2。1根据openid查询用户，如果查询到，则是老用户
     *  2。2。2没有，表示新用户，则创建item在user表
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //1 发送请求
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        //微信的结果 {"session_key":"wbxISH3lGdWv73xc/KUo7g==","openid":"oxw9u7cY7itssC0iLN028ym78HjM"}
        String res = HttpClientUtil.doGet(url, map);
        //判断openid
        JSONObject jsonObject = JSON.parseObject(res);
        String openid = jsonObject.getString("openid");

        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.findByOpenId(openid);
        if(user == null){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.add(user);
        }

        //组装User
        Map<String,Object> claim = new HashMap<>();
        claim.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claim);
        UserLoginVO vo = new UserLoginVO(user.getId(),openid,token);

        return vo;
    }
}
