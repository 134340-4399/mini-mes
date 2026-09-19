package com.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_order")  // 对应数据库表名
public class WorkOrder {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;
    private String orderNo;       // 工单号
    private String productModel;  // 产品型号
    private Integer quantity;     // 计划数量
    private String lineName;      // 产线
    private String status;        // 状态：待生产/生产中/已完成
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}