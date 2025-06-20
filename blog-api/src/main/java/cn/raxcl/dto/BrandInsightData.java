package cn.raxcl.dto;

import java.util.List;

/**
 * 品牌洞察数据
 * @author raxcl
 */
public class BrandInsightData {
    private String merchantName;
    private String logoUrl;
    private String backgroundImageUrl;
    private String reportDate;
    private String brandValue;
    private double marketShare;
    private List<PerformanceMetric> performanceMetrics;
    private List<String> marketChartLabels;
    private List<Double> marketChartData;

    public BrandInsightData() {}

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getBrandValue() {
        return brandValue;
    }

    public void setBrandValue(String brandValue) {
        this.brandValue = brandValue;
    }

    public double getMarketShare() {
        return marketShare;
    }

    public void setMarketShare(double marketShare) {
        this.marketShare = marketShare;
    }

    public List<PerformanceMetric> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(List<PerformanceMetric> performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }

    public List<String> getMarketChartLabels() {
        return marketChartLabels;
    }

    public void setMarketChartLabels(List<String> marketChartLabels) {
        this.marketChartLabels = marketChartLabels;
    }

    public List<Double> getMarketChartData() {
        return marketChartData;
    }

    public void setMarketChartData(List<Double> marketChartData) {
        this.marketChartData = marketChartData;
    }

    /**
     * 性能指标
     */
    public static class PerformanceMetric {
        private String name;
        private String currentValue;
        private String compareValue;
        private String trend;

        public PerformanceMetric() {}

        public PerformanceMetric(String name, String currentValue, String compareValue, String trend) {
            this.name = name;
            this.currentValue = currentValue;
            this.compareValue = compareValue;
            this.trend = trend;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(String currentValue) {
            this.currentValue = currentValue;
        }

        public String getCompareValue() {
            return compareValue;
        }

        public void setCompareValue(String compareValue) {
            this.compareValue = compareValue;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }
} 