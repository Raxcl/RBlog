package cn.raxcl.dto;

/**
 * 图表数据点
 * @author raxcl
 */
public class ChartDataPoint {
    private String label;
    private Double value;

    public ChartDataPoint() {}

    public ChartDataPoint(String label, Double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
} 