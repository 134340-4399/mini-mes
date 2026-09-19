package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.dto.LineSummaryDTO;
import com.mes.entity.ProductionReport;
import com.mes.entity.WorkOrder;
import com.mes.mapper.ProductionReportMapper;
import com.mes.mapper.WorkOrderMapper;
import com.mes.common.BusinessException;
import com.mes.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductionReportService {
    @Autowired
    private ProductionReportMapper productionReportMapper;

    @Autowired
    private WorkOrderMapper workOrderMapper;   // 查工单要用到别人的 mapper

    // 两个方法：create + listByOrderNo
    public ProductionReport create(String orderNo, Integer totalProducts,
                                          Integer qualifiedQuantity, Integer scrappedQuantity,
                                          String reason){
        // ① 入参非空且合法
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new BusinessException("工单号不能为空");
        }
        if (totalProducts == null || totalProducts < 0) {
            throw new BusinessException("计划总数必须为非负整数");
        }
        if (qualifiedQuantity == null || qualifiedQuantity < 0) {
            throw new BusinessException("合格数必须为非负整数");
        }
        if (scrappedQuantity == null || scrappedQuantity < 0) {
            throw new BusinessException("报废数必须为非负整数");
        }

        // ② 数量守恒
        if (qualifiedQuantity + scrappedQuantity != totalProducts) {
            throw new BusinessException("合格数 + 报废数 必须等于 计划总数");
        }

        // ③ 报废原因校验
        if (scrappedQuantity > 0) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BusinessException("报废数大于0时，必须填写报废原因");
            }
        } else { // scrappedQuantity == 0
            if (reason != null && !reason.trim().isEmpty()) {
                throw new BusinessException("报废数为0时，原因必须为null或空字符串");
            }
        }

        // ④ 根据 orderNo 查询工单，获取主键 id
        QueryWrapper<WorkOrder> woQuery = new QueryWrapper<>();
        woQuery.eq("order_no", orderNo);
        WorkOrder workOrder = workOrderMapper.selectOne(woQuery);
        if (workOrder == null) {
            throw new BusinessException("工单号不存在: " + orderNo);
        }
        Long workOrderId = workOrder.getId();   // 主键，假设字段为 id

        // ⑤ 组装 ProductionReport 实体
        ProductionReport report = new ProductionReport();
        report.setWorkOrderId(workOrderId);
        report.setTotalProducts(totalProducts);
        report.setQualifiedQuantity(qualifiedQuantity);
        report.setScrappedQuantity(scrappedQuantity);
        report.setReason(reason);
        String username = UserContext.get();
        if (username == null) {
            throw new BusinessException("未登录，无法获取当前用户");
        }
        report.setUsername(username);
        report.setReportTime(LocalDateTime.now());
        productionReportMapper.insert(report);
        return report;
    }

    public List<ProductionReport>  listByOrderNo(String orderNo){
        QueryWrapper<WorkOrder> workOrderQuery = new QueryWrapper<>();
        workOrderQuery.eq("order_no",orderNo);
        WorkOrder workOrder = workOrderMapper.selectOne(workOrderQuery);
        if (workOrder  == null) {
            throw new BusinessException("工单不存在: " + orderNo);
        }
        Long workOrderId = workOrder.getId();
        QueryWrapper<ProductionReport>  reportQuery = new QueryWrapper<>();
        reportQuery.eq("work_order_id",workOrderId);
        return productionReportMapper.selectList(reportQuery);
    }

    public List<LineSummaryDTO> selectLineSummary() {
        return productionReportMapper.selectLineSummary();
    }

}
