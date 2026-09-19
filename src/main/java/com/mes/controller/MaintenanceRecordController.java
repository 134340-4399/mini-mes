package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.MaintenanceRecordCreateDTO;
import com.mes.entity.MaintenanceRecord;
import com.mes.service.MaintenanceRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-records")
public class MaintenanceRecordController {
    private final MaintenanceRecordService maintenanceRecordService;

    public MaintenanceRecordController(MaintenanceRecordService maintenanceRecordService) {
        this.maintenanceRecordService = maintenanceRecordService;
    }
    @PostMapping
    public Result<MaintenanceRecord> create(@RequestBody MaintenanceRecordCreateDTO maintenanceRecordCreateDTO){
        MaintenanceRecord maintenanceRecord = new MaintenanceRecord();
        maintenanceRecord.setMaintenanceType(maintenanceRecordCreateDTO.getMaintenanceType());
        maintenanceRecord.setResult(maintenanceRecordCreateDTO.getResult());
        maintenanceRecord.setCost(maintenanceRecordCreateDTO.getCost());
        maintenanceRecord.setEquipmentCode(maintenanceRecordCreateDTO.getEquipmentCode());
        maintenanceRecord.setFaultDescription(maintenanceRecordCreateDTO.getFaultDescription());
        maintenanceRecord.setTechnician(maintenanceRecordCreateDTO.getTechnician());
        MaintenanceRecord record = maintenanceRecordService.create(maintenanceRecord);
        return Result.ok(record);
    }
    @GetMapping
    public Result<List<MaintenanceRecord>> listAll(){
        List<MaintenanceRecord> record = maintenanceRecordService.listAll();
        return Result.ok(record);
    }
}
