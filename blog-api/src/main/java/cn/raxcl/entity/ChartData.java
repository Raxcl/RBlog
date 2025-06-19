package cn.raxcl.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 图表数据实体类
 * @author raxcl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartData {
    
    /**
     * 图表标题
     */
    private String title;
    
    /**
     * X轴标签
     */
    private String xAxisLabel;
    
    /**
     * Y轴标签
     */
    private String yAxisLabel;
    
    /**
     * 数据集合 - key为数据系列名称，value为数据点列表
     */
    private Map<String, List<DataPoint>> datasets;
    
    /**
     * 图表类型：line（折线图）、bar（柱状图）、pie（饼图）
     */
    private String chartType;
    
    /**
     * 数据点类别（X轴标签）
     */
    private List<String> categories;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPoint {
        private String label;
        private Double value;
    }
} 