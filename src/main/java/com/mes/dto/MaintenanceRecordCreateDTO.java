package com.mes.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaintenanceRecordCreateDTO {
    private String equipmentCode;
    private String maintenanceType;
    private String faultDescription;
    private BigDecimal cost;
    private String technician;
    private String result;
}
