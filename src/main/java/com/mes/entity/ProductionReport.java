package com.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("production_report")
public class ProductionReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workOrderId;
    private Integer totalProducts;
    private Integer qualifiedQuantity;
    private Integer scrappedQuantity;
    private String reason;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;

}
