package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openId}")
    User findByOpenId(String openId);

    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("insert into user (openid, create_time) values (#{openid},#{createTime})")
    void add(User user);
}
