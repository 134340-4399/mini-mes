package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.EquipmentCreateDTO;
import com.mes.entity.Equipment;
import com.mes.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 查询设备列表
     * 有line参数：根据产线筛选；无参数：查询全部
     * 地址示例：
     * /api/equipment
     * /api/equipment?line=一号产线
     */
    @GetMapping
    public Result<List<Equipment>> getList(@RequestParam(required = false) String line) {
        List<Equipment> list;
        if (line != null) {
            list = equipmentService.listByLine(line);
        } else {
            list = equipmentService.listAll();
        }
        return Result.ok(list);
    }

    /**
     * 根据主键id查询单个设备
     * 地址示例：/api/equipment/1
     */
    @GetMapping("/{id}")
    public Result<Equipment> getOne(@PathVariable Long id) {
        return Result.ok(equipmentService.getById(id));
    }

    /**
     * 新增设备
     * POST 请求，body传json设备信息
     */
    @PostMapping
    public Result<Equipment> add(@RequestBody EquipmentCreateDTO dto) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setEquipmentModel(dto.getEquipmentModel());
        equipment.setProductionLine(dto.getProductionLine());
        return equipmentService.add(equipment);
    }

    /**
     * 修改设备
     * PUT 请求
     */
    @PutMapping("/{id}")
    public Result<Equipment> update(@PathVariable Long id, @RequestBody EquipmentCreateDTO dto) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setEquipmentModel(dto.getEquipmentModel());
        equipment.setProductionLine(dto.getProductionLine());
        return equipmentService.update(equipment);
    }

    /**
     * 根据id删除设备
     * DELETE 请求
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return equipmentService.delete(id);
    }
}