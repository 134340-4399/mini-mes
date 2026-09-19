package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.common.BusinessException;
import com.mes.entity.Equipment;
import com.mes.entity.MaintenanceRecord;
import com.mes.mapper.EquipmentMapper;
import com.mes.mapper.MaintenanceRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceRecordService {
    @Autowired
    private MaintenanceRecordMapper maintenanceRecordMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    public List<MaintenanceRecord> listAll(){
        QueryWrapper<MaintenanceRecord> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
           return maintenanceRecordMapper.selectList(wrapper);
    }
    public MaintenanceRecord create(MaintenanceRecord maintenanceRecord){
        if(maintenanceRecord.getMaintenanceType() == null ||  maintenanceRecord.getMaintenanceType().trim().isEmpty())
            throw new BusinessException("未选择维护类型");
        if(maintenanceRecord.getEquipmentCode() == null || maintenanceRecord.getEquipmentCode().trim().isEmpty())
            throw new BusinessException("设备编号不能为空");
        if(maintenanceRecord.getCost() == null || maintenanceRecord.getCost().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessException("成本金额错误");
        if (maintenanceRecord.getFaultDescription() == null || maintenanceRecord.getFaultDescription().trim().isEmpty())
            throw new BusinessException("请填写故障描述");
        QueryWrapper<Equipment> wrapper = new QueryWrapper<>();
        wrapper.eq("equipment_code",maintenanceRecord.getEquipmentCode());
        Equipment equipment = equipmentMapper.selectOne(wrapper);
        if (equipment == null)
            throw new BusinessException("机器不存在: " + maintenanceRecord.getEquipmentCode());
        if (maintenanceRecord.getMaintenanceTime() == null)
            maintenanceRecord.setMaintenanceTime(LocalDateTime.now());
        maintenanceRecordMapper.insert(maintenanceRecord);
        return maintenanceRecord;
    }
}
