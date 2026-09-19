package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.LineSummaryDTO;
import com.mes.entity.ProductionReport;
import com.mes.service.ProductionReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production-reports")
public class ProductionReportController {

    private final ProductionReportService productionReportService;
    public ProductionReportController(ProductionReportService productionReportService) {
        this.productionReportService = productionReportService;
    }

    @PostMapping
    public Result<ProductionReport> create(@RequestParam String orderNo,
                                           @RequestParam Integer totalProducts,
                                           @RequestParam Integer qualifiedQuantity,
                                           @RequestParam Integer scrappedQuantity,
                                           @RequestParam(required = false) String reason) {
        ProductionReport report = productionReportService.create(orderNo, totalProducts,
                qualifiedQuantity, scrappedQuantity, reason);
        return Result.ok(report);
    }
    @GetMapping
    public Result<List<ProductionReport>> listByOrderNo(@RequestParam String orderNo){
        List<ProductionReport> report = productionReportService.listByOrderNo(orderNo);
        return Result.ok(report);
    }
    @GetMapping("/line-summary")
    public Result<List<LineSummaryDTO>> getLineSummary(){
        List<LineSummaryDTO> reports = productionReportService.selectLineSummary();
        return Result.ok(reports);
    }
}