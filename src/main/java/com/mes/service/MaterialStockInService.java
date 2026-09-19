package com.mes.service;

import com.mes.common.BusinessException;
import com.mes.common.UserContext;
import com.mes.entity.Material;
import com.mes.entity.MaterialStockIn;
import com.mes.mapper.MaterialMapper;
import com.mes.mapper.MaterialStockInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MaterialStockInService {
    @Autowired
    private MaterialStockInMapper materialStockInMapper;
    @Autowired
    private MaterialMapper materialMapper;
    @Transactional
    public void stockIn(Long materialId, Integer quantity){
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("数量必须为正整数");
        }
        material.setStock(material.getStock() + quantity);
        materialMapper.updateById(material);
        MaterialStockIn materialStockIn = new MaterialStockIn();
        materialStockIn.setMaterialId(materialId);
        materialStockIn.setQuantity(quantity);
        String username = UserContext.get();
        if (username == null) {
            throw new BusinessException("未登录，无法获取当前用户");
        }
        materialStockIn.setUsername(username);
        materialStockIn.setStockInTime(LocalDateTime.now());
        materialStockInMapper.insert(materialStockIn);
    }
}
