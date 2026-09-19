package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.common.BusinessException;
import com.mes.entity.WorkOrder;
import com.mes.dto.WorkOrderCreateDTO;
import com.mes.mapper.WorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    // ============ 查询 ============

    public List<WorkOrder> listAll() {
        return workOrderMapper.selectList(null);
    }

    public List<WorkOrder> listByLine(String lineName) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("line_name", lineName);
        return workOrderMapper.selectList(wrapper);
    }

    // ============ 创建 ============

    /**
     * 创建新工单。自动设创建时间、更新时间，状态默认"待生产"
     */
    public WorkOrder create(WorkOrder order) {
        order.setCreateTime(LocalDateTime.now());
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            throw new BusinessException("工单号不能为空");
        }
        order.setUpdateTime(LocalDateTime.now());
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("待生产");
        }
        workOrderMapper.insert(order);
        // insert 后 MyBatis-Plus 自动回填自增 ID 到 order.id
        return order;
    }

    // ============ 更新 ============

    /**
     * 更新工单状态（待生产 → 生产中 → 已完成）
     */
    public WorkOrder updateStatus(Long id, String status) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在，id=" + id);
        }
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        workOrderMapper.updateById(order);
        return order;
    }
}
