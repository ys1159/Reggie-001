package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    //新增菜品，菜品口味，两张表
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();

        //保存菜品口味

        List<DishFlavor> flavors = dishDto.getFlavors();
//        flavors=flavors.stream().map((item)->{
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());
        flavors.forEach(item->item.setDishId(dishId));

        dishFlavorService.saveBatch(dishDto.getFlavors());


    }

    //id 查询菜品与口味
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);
        //口味查询
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    //更新菜品
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish
        this.updateById(dishDto);
        //清理菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.forEach(item->item.setDishId(dishDto.getId()));

        dishFlavorService.saveBatch(flavors);
    }
    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void batchDeleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = this.list(queryWrapper);

        if (list != null) {
            for (Dish dish : list) {
                //要先判断该菜品是否为停售状态,否则无法删除并且抛出异常处理
                if (dish.getStatus() == 0) {
                    this.removeByIds(ids);
                } else {
                    throw new CustomException("有菜品正在售卖，无法全部删除！");
                }
            }
        }
    }
        @Override
        public boolean batchUpdateStatusByIds (Integer status, List < Long > ids){
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

//对应的sql语句:SELECT id,name,category_id,price,code,image,description,status,sort,create_time,update_time,create_user,update_user,is_deleted FROM dish WHERE (id IN (?,?))
            queryWrapper.in(ids != null, Dish::getId, ids);
            List<Dish> list = this.list(queryWrapper);
            if (list != null) {
                for (Dish dish : list) {
                    dish.setStatus(status);//修改菜品的售卖状态
                    this.updateById(dish);
                }
                return true;
            } else {
                return false;
            }
        }
    }


