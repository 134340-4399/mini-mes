package com.mes.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineSummaryDTO {
    private String lineName;
    private Long reportCount;
    private BigDecimal totalQualified;
    private BigDecimal totalScrapped;
}

