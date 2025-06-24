package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询多个套餐id
     * @param dishIds
     * @return
     */
    //动态sql填写在xml映射文件中
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

}
