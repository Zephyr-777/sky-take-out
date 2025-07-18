package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

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


    /**
     * 插入菜品数据
     * @param dish 菜品实体
     */
    @AutoFill(value = OperationType.INSERT)  //  自动填充注解，指定操作类型为插入
    void insert(Dish dish);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品数据
     *
     * @param id 菜品ID
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品
     *
     * @param id 菜品ID
     * @return 菜品实体
     */
    @Delete("delete from dish where id =#{id}")
    void deleteById(Long id);

    /**
     * 根据ids集合批量删除菜品
     * @param ids 菜品实体
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id动态修改基本信息和口味信息
     *
     * @param dish 菜品数据传输对象
     */
    @AutoFill(value = OperationType.UPDATE) //公共字段自动填充注解，指定操作类型为更新
    void update(Dish dish);
}
