package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mes.common.BusinessException;
import com.mes.dto.MaterialCreateDTO;
import com.mes.entity.Material;
import com.mes.mapper.MaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterialService {
    @Autowired
    private MaterialMapper materialMapper;

    public List<Material> listAll(String materialCode){
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(materialCode), "material_code", materialCode);
        queryWrapper.orderByDesc("id");
        return materialMapper.selectList(queryWrapper);
    }

    public int create(MaterialCreateDTO dto){
        if (!StringUtils.hasText(dto.getMaterialCode())) {
            throw new BusinessException("物料编码不能为空");
        }
        Long count = materialMapper.selectCount(
                new QueryWrapper<Material>().eq("material_code", dto.getMaterialCode()));
        if (count > 0) {
            throw new BusinessException("物料编码已存在");
        }
        Material material = new Material();
        material.setMaterialCode(dto.getMaterialCode());
        material.setMaterialName(dto.getMaterialName());
        material.setUnit(dto.getUnit());
        material.setStock(0);
        material.setCreateTime(LocalDateTime.now());
        return materialMapper.insert(material);
    }

    public int update(Material material){
        return materialMapper.updateById(material);
    }

    public int delete(Long id){
        return materialMapper.deleteById(id);
    }
}
