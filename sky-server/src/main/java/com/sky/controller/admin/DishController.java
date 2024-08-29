package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Api(tags = "菜品接口")
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){
        dishService.add(dishDTO);

        //delete the cache
        redisTemplate.delete("dish:"+dishDTO.getCategoryId());
        return Result.success();
    }

    @ApiOperation("dish page query")
    @GetMapping("/page")
    public Result page(DishPageQueryDTO pageQueryDTO){
        PageResult pageResult = dishService.page(pageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("delete dish")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.delete(ids);
        Set keys = redisTemplate.keys("dish:*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    @ApiOperation("query dish by id")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id){
        return Result.success(dishService.findById(id));
    }

    @ApiOperation("update dish")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        Set keys = redisTemplate.keys("dish:*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result setStatus(@PathVariable Integer status,
                            @RequestParam Long id){
        dishService.setStatus(status,id);
        Set keys = redisTemplate.keys("dish:*");
        redisTemplate.delete(keys);
        return Result.success();
    }
}
