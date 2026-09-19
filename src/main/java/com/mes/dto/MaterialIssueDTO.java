package com.mes.dto;

import lombok.Data;

@Data
public class MaterialIssueDTO {
    private String orderNo;
    private Long materialId;
    private Integer quantity;
}
