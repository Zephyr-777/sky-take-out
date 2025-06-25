package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和口味数据
     * @param dishDTO
     */
    public void  saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    public void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询菜品
     *
     * @param id 菜品ID
     * @return 菜品视图对象
     */
    DishVO getByIdWithFlavor(long id);

    /**
     * 修改菜品基本信息和口味信息
     *
     * @param dishDTO 菜品数据传输对象
     */
    void updateWithFlavor(DishDTO dishDTO);
}
