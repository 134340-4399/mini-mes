package com.mes.mapper;                          // ① 包名 com.mes.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper;   // ② BaseMapper
import com.mes.dto.LineSummaryDTO;
import com.mes.entity.ProductionReport;                  // ③ 你自己的实体类
import org.apache.ibatis.annotations.Mapper;              // ④ @Mapper 注解
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductionReportMapper extends BaseMapper<ProductionReport> {
    @Select("SELECT \n" +
            "    w.line_name AS line_name,\n" +
            "    COUNT(pr.id) AS report_count,\n" +
            "    IFNULL(SUM(pr.qualified_quantity), 0) AS total_qualified,\n" +
            "    IFNULL(SUM(pr.scrapped_quantity), 0) AS total_scrapped\n" +
            "FROM work_order w\n" +
            "LEFT JOIN production_report pr\n" +
            "    ON w.id = pr.work_order_id\n" +
            "GROUP BY w.line_name\n" +
            "ORDER BY total_scrapped desc")
    List<LineSummaryDTO> selectLineSummary();
}