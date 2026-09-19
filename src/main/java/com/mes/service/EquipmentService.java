package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.common.BusinessException;
import com.mes.common.Result;
import com.mes.entity.Equipment;
import com.mes.mapper.EquipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.mes.common.Result.ok;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;
    // 查询所有设备
    public List<Equipment> listAll() {
        QueryWrapper<Equipment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return equipmentMapper.selectList(wrapper);
    }

    // 根据产线查询设备
    public List<Equipment> listByLine(String line) {
        QueryWrapper<Equipment> wrapper = new QueryWrapper<>();
        wrapper.eq("production_line", line);
        wrapper.orderByDesc("create_time");
        return equipmentMapper.selectList(wrapper);
    }

    // 根据主键id查询单条设备
    public Equipment getById(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在，id=" + id);
        }return equipment;
    }

    // 新增设备
    public Result<Equipment> add(Equipment equipment) {
        if (equipment.getEquipmentStatus() == null || equipment.getEquipmentStatus().isEmpty())
            equipment.setEquipmentStatus("停机");
        equipment.setCreateTime(LocalDateTime.now());
        boolean success = equipmentMapper.insert(equipment) > 0;
        if (success) {
            return Result.ok(equipment);
        } else {
            return Result.fail("添加失败");
        }
    }
    //修改设备
    public Result<Equipment> update(Equipment equipment) {
        boolean update = equipmentMapper.updateById(equipment)>0;
        if (update) {
            Equipment updated = equipmentMapper.selectById(equipment.getId());
            return Result.ok(updated);
        } else {
            return Result.fail("修改失败");
        }
    }
    // 根据id删除设备
    public Result<Void> delete(Long id) {
        boolean delete = equipmentMapper.deleteById(id)>0;
        if (delete) {
            return Result.ok();
        } else {
            return Result.fail("删除失败");
        }
    }

}
