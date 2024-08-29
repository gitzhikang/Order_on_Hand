package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    //void add(DishFlavor dishFlavor)

    void add(List<DishFlavor> flavorList);

    /**
     * 根据菜品id删除口味
     * @param ids
     */
    void deleteByDishId(List<Long> ids);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> findByDishId(Long id);
}
