package cn.raxcl.dto;

import java.util.List;

/**
 * 图表数据传输对象
 * 
 * @author raxcl
 * @date 2024-01-01
 */
public class ChartData {
    
    /** 图表标题 */
    private String title;
    
    /** 图表类型(bar/line/pie) */
    private String chartType;
    
    /** X轴标签 */
    private String xAxisLabel;
    
    /** Y轴标签 */
    private String yAxisLabel;
    
    /** 数据点列表 */
    private List<ChartDataPoint> dataPoints;
    
    public ChartData() {}
    
    public ChartData(String title, String chartType, String xAxisLabel, String yAxisLabel, List<ChartDataPoint> dataPoints) {
        this.title = title;
        this.chartType = chartType;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.dataPoints = dataPoints;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getChartType() {
        return chartType;
    }
    
    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
    
    public String getXAxisLabel() {
        return xAxisLabel;
    }
    
    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }
    
    public String getYAxisLabel() {
        return yAxisLabel;
    }
    
    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }
    
    public List<ChartDataPoint> getDataPoints() {
        return dataPoints;
    }
    
    public void setDataPoints(List<ChartDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
} 