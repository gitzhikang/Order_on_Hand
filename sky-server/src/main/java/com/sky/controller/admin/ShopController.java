package com.sky.controller.admin;

import com.sky.constant.RedisKeyConstance;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api(tags = "店铺接口")
@RequestMapping("/admin/shop")
@RestController
public class ShopController {


    @Autowired
    RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set(RedisKeyConstance.SHOP_STATUS,status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return
     */
    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(RedisKeyConstance.SHOP_STATUS);
        return Result.success(status == null?0:status);
    }
}
