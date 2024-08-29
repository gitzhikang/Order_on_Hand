package com.sky.controller.user;

import com.sky.constant.RedisKeyConstance;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺接口")
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(RedisKeyConstance.SHOP_STATUS);
        return Result.success(status == null?0:status);
    }
}
