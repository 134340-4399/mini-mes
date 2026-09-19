package com.mes.dto;

import lombok.Data;

@Data
public class WorkOrderCreateDTO {
    private String orderNo;
    private String productModel;
    private Integer quantity;
    private String lineName;
}
