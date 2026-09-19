package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.common.BusinessException;
import com.mes.common.UserContext;
import com.mes.dto.MaterialIssueDTO;
import com.mes.entity.Material;
import com.mes.entity.MaterialIssue;
import com.mes.entity.WorkOrder;
import com.mes.mapper.MaterialIssueMapper;
import com.mes.mapper.MaterialMapper;
import com.mes.mapper.WorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class MaterialIssueService {
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private MaterialIssueMapper materialIssueMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Transactional
    public void issue(MaterialIssueDTO materialIssueDTO) {
        if (!StringUtils.hasText(materialIssueDTO.getOrderNo())) {
            throw new BusinessException("工单号不能为空");
        }
        if (materialIssueDTO.getQuantity() == null || materialIssueDTO.getQuantity() <= 0) {
            throw new BusinessException("数量必须为正整数");
        }

        WorkOrder workOrder = workOrderMapper.selectOne(new QueryWrapper<WorkOrder>().eq("order_no", materialIssueDTO.getOrderNo()));
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }

        Material material = materialMapper.selectById(materialIssueDTO.getMaterialId());
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        int result = materialIssueMapper.deductStock(materialIssueDTO.getMaterialId(), materialIssueDTO.getQuantity());
        if (result == 0) {
            throw new BusinessException("库存不足");
        }
        MaterialIssue materialIssue = new MaterialIssue();
        materialIssue.setWorkOrderId(workOrder.getId());
        materialIssue.setMaterialId(materialIssueDTO.getMaterialId());
        materialIssue.setQuantity(materialIssueDTO.getQuantity());
        String username = UserContext.get();
        if (username == null) {
            throw new BusinessException("未登录，无法获取当前用户");
        }
        materialIssue.setUsername(username);
        materialIssue.setIssueTime(LocalDateTime.now());
        materialIssueMapper.insert(materialIssue);
    }
}
