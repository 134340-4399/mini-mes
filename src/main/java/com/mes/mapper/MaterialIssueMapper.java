package com.mes.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mes.entity.MaterialIssue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface MaterialIssueMapper extends BaseMapper<MaterialIssue> {
    @Update("UPDATE material set stock = stock - #{quantity} where id = #{materialId} AND stock >= #{quantity}")
    int deductStock(@Param("materialId") Long materialId, @Param("quantity") Integer quantity);
}
