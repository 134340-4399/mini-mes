package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.MaterialStockInDTO;
import com.mes.service.MaterialStockInService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MaterialStockInController {
    private final MaterialStockInService materialStockInService;
    public MaterialStockInController(MaterialStockInService materialStockInService) {
        this.materialStockInService = materialStockInService;
    }
    @PostMapping("/material-stock-in")
    public Result<Void> stockIn(@RequestBody MaterialStockInDTO materialStockInDTO) {
        materialStockInService.stockIn(materialStockInDTO.getMaterialId(),materialStockInDTO.getQuantity());
        return Result.ok();
    }

}
