package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.WorkOrderCreateDTO;
import com.mes.entity.WorkOrder;
import com.mes.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // ============ GET（已有） ============

    @GetMapping("/work-orders")
    public Result<List<WorkOrder>> listOrders(@RequestParam(required = false) String line) {
        List<WorkOrder> list;
        if (line != null) {
            list = workOrderService.listByLine(line);
        } else {
            list = workOrderService.listAll();
        }
        return Result.ok(list);
    }

    // ============ POST — 创建工单 ============

    /**
     * 创建新工单
     * 请求体 JSON 示例：
     * {
     *   "orderNo": "MO-20260725-001",
     *   "productModel": "BSC-720A",
     *   "quantity": 500,
     *   "lineName": "A线"
     * }
     */
    @PostMapping("/work-orders")
    public Result<WorkOrder> createOrder(@RequestBody WorkOrderCreateDTO dto) {
        WorkOrder order = new WorkOrder();
        order.setOrderNo(dto.getOrderNo());
        order.setProductModel(dto.getProductModel());
        order.setQuantity(dto.getQuantity());
        order.setLineName(dto.getLineName());
        WorkOrder created = workOrderService.create(order);
        return Result.ok(created);
    }

    // ============ PUT — 更新工单状态 ============

    /**
     * 更新工单状态
     * PUT /api/work-orders/1/status?status=生产中
     */
    @PutMapping("/work-orders/{id}/status")
    public Result<WorkOrder> updateStatus(@PathVariable Long id, @RequestParam String status) {
        WorkOrder updated = workOrderService.updateStatus(id, status);
        return Result.ok(updated);
    }
}