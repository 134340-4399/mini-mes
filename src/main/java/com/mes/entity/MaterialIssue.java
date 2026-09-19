package com.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material_issue")
public class MaterialIssue {
@TableId(type = IdType.AUTO)
  private Long id;
  private Long workOrderId;
  private Long materialId;
  private Integer quantity;
  private String username;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime issueTime;
}
