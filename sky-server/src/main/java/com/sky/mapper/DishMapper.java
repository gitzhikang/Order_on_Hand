package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    @Options(keyProperty = "id",useGeneratedKeys = true)
    @AutoFill
    @Insert("insert into dish values (null,#{name},#{categoryId},#{price},#{image},#{description},#{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void add(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查dish
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish findDishById(Long id);

    void delete(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    @Update("update dish set name = #{name}, category_id = #{categoryId}, price = #{price},image = #{image}, description = #{description}"+
            ", status = #{status}, update_time = #{updateTime},update_user = #{updateUser} where id = #{id}")
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> list(Dish dish);

    @Update("update dish set status = #{status} where id = #{id}")
    void setStatus(Integer status,Long id);
}
