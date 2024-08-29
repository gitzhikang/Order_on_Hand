package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    /**
     * add new dishes
     * 1. dish data contains basic data and favor data
     * 2. basic data saved in dish table, favor data saved in dish_favor table
     * 3. add data to the two tables
     * 4. Firstly, add data to dish(get dish id)
     * @param dishDTO
     */
    @Override
    public void add(DishDTO dishDTO) {
        // add data to dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.add(dish);

        // add data to dish_flavor
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();

        //judge if dish has flavor
        if(dishFlavorList != null && dishFlavorList.size()>0){
            //set dish_id
            dishFlavorList.forEach(dishFlavor -> {
                // return the id, when Mybatis add the dish
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.add(dishFlavorList);
        }

    }

    /**
     * dish page
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.page(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 1.可以删除一个菜品，也可以多个
     * 2.启售的不能删
     * 3。套餐中含有的不能删
     * 4。删菜也要删口味数据
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        //看是否有起售的
        ids.forEach(id->{
            Dish dish = dishMapper.findDishById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //看是否在套餐中
        List<SetmealDish> setmealDishList = setMealDishMapper.findByDishId(ids);
        if(setmealDishList!=null && setmealDishList.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除dish和flavor
        dishMapper.delete(ids);
        dishFlavorMapper.deleteByDishId(ids);

    }

    /**
     * 根据id返回菜品
     *  1.既要菜品，又要口味
     *  2.一会去查询两个表，在service进行打包
     * @param id
     * @return
     */
    @Override
    public DishVO findById(Long id) {
        Dish dish = dishMapper.findDishById(id);
        List<DishFlavor> flavorList = dishFlavorMapper.findByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavorList);
        return dishVO;
    }

    /**
     * 更新菜品
     * 1.有可能只更新dish，也有可能要更新flavor
     * 2.更新dish表
     * 3.更新口味表
     *  3.1 因为要判定的东西很多
     *  3.2 先删除口味数据，然后讲页面传递的数据添加到数据表中
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //删除口味数组
        dishFlavorMapper.deleteByDishId(Arrays.asList(dish.getId()));

        if (dishDTO.getFlavors() !=null && dishDTO.getFlavors().size()>0) {
            //对flavor添加dishid
            dishDTO.getFlavors().forEach(flavor-> {
                flavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.add(dishDTO.getFlavors());
        }

    }

    @Override
    public void setStatus(Integer status,Long id) {
        dishMapper.setStatus(status,id);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.findByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
