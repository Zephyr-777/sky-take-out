package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和口味数据
     * @param dishDTO 菜品数据传输对象
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表插入1条菜品数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        long dishId = dish.getId();

        //向菜品口味表插入多条数据(批量插入)
        List<DishFlavor> flavors =  dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {
                //设置口味对应的菜品id
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询数据传输对象
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());//开启分页
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult()); // 返回分页结果
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional //多张表操作需要开启事务
    public void deleteBatch(List<Long> ids) {

        //判断菜品是否能够被删除---是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()==StatusConstant.ENABLE){
                //如果菜品正在售卖中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否能够被删除---是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            //如果菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //批量删除菜品数据
        dishMapper.deleteByIds(ids);

        //批量删除菜品口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据ID查询菜品
     *
     * @param id 菜品ID
     * @return 菜品视图对象
     */
    @Override
    public DishVO getByIdWithFlavor(long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //将查询到的数据封装到DishVO对象中返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品基本信息和口味信息
     *
     * @param dishDTO 菜品数据传输对象
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除原有的口味数据
        if(!dishDTO.getFlavors().isEmpty()) {
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
        }
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                //设置口味对应的菜品id
                dishFlavor.setDishId(dishDTO.getId());  //前端可能不会返回dishId
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
