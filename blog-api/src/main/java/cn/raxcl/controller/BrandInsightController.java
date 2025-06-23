package cn.raxcl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 品牌洞察控制器
 * 基于Thymeleaf模板生成品牌洞察报告HTML页面
 */
@Controller
@RequestMapping("/brand")
public class BrandInsightController {

    private static final Logger log = LoggerFactory.getLogger(BrandInsightController.class);

    /**
     * 显示品牌洞察报告页面
     */
    @GetMapping("/insight")
    public String showBrandInsight(Model model) {
        // 添加示例数据用于展示
        model.addAttribute("merchantName", "示例企业科技有限公司");
        model.addAttribute("brandValue", "1.2亿元");
        model.addAttribute("marketShare", "15.8");
        model.addAttribute("reportDate", "2024年1月");
        model.addAttribute("logoUrl", "https://via.placeholder.com/80x80/667eea/ffffff?text=LOGO");
        
        // 性能指标数据
        List<PerformanceMetric> performanceMetrics = Arrays.asList(
            new PerformanceMetric("品牌知名度", "78.5%", "上升 5.2%", "↗ 上升"),
            new PerformanceMetric("客户满意度", "4.6分", "上升 0.3分", "↗ 上升"),
            new PerformanceMetric("市场渗透率", "23.4%", "上升 2.1%", "↗ 上升"),
            new PerformanceMetric("复购率", "45.8%", "下降 1.2%", "↘ 下降"),
            new PerformanceMetric("净推荐值", "72分", "持平", "→ 持平")
        );
        model.addAttribute("performanceMetrics", performanceMetrics);
        
        // 市场趋势数据
        List<String> marketChartLabels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月");
        List<String> marketChartData = Arrays.asList("120", "135", "142", "158", "167", "175");
        model.addAttribute("marketChartLabels", marketChartLabels);
        model.addAttribute("marketChartData", marketChartData);
        
        log.info("显示品牌洞察报告页面");
        return "brand-insight";
    }

    /**
     * 基于POST请求生成品牌洞察报告
     */
    @PostMapping("/insight")
    public String generateBrandInsight(@ModelAttribute BrandInsightRequest request, Model model) {
        try {
            // 设置模板变量
            model.addAttribute("merchantName", request.getMerchantName());
            model.addAttribute("brandValue", request.getBrandValue());
            model.addAttribute("marketShare", request.getMarketShare());
            model.addAttribute("reportDate", request.getReportDate());
            model.addAttribute("logoUrl", request.getLogoUrl());
            model.addAttribute("performanceMetrics", request.getPerformanceMetrics());
            model.addAttribute("marketChartLabels", request.getMarketChartLabels());
            model.addAttribute("marketChartData", request.getMarketChartData());
            
            log.info("生成品牌洞察报告: {}", request.getMerchantName());
            return "brand-insight";
            
        } catch (Exception e) {
            log.error("生成品牌洞察报告失败", e);
            model.addAttribute("error", "生成报告失败，请重试");
            return "error";
        }
    }

    /**
     * 显示品牌洞察表单页面
     */
    @GetMapping("/insight/form")
    public String showBrandInsightForm(Model model) {
        model.addAttribute("request", new BrandInsightRequest());
        return "brand-insight-form";
    }

    /**
     * 品牌洞察报告请求数据模型
     */
    public static class BrandInsightRequest {
        private String merchantName = "示例企业科技有限公司";
        private String brandValue = "1.2亿元";
        private String marketShare = "15.8";
        private String reportDate = "2024年1月";
        private String logoUrl = "https://via.placeholder.com/80x80/667eea/ffffff?text=LOGO";
        private List<PerformanceMetric> performanceMetrics = new ArrayList<>();
        private List<String> marketChartLabels = new ArrayList<>();
        private List<String> marketChartData = new ArrayList<>();

        // 构造函数初始化默认数据
        public BrandInsightRequest() {
            initDefaultData();
        }

        private void initDefaultData() {
            // 初始化性能指标
            performanceMetrics = Arrays.asList(
                new PerformanceMetric("品牌知名度", "78.5%", "上升 5.2%", "↗ 上升"),
                new PerformanceMetric("客户满意度", "4.6分", "上升 0.3分", "↗ 上升"),
                new PerformanceMetric("市场渗透率", "23.4%", "上升 2.1%", "↗ 上升"),
                new PerformanceMetric("复购率", "45.8%", "下降 1.2%", "↘ 下降"),
                new PerformanceMetric("净推荐值", "72分", "持平", "→ 持平")
            );
            
            // 初始化图表数据
            marketChartLabels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月");
            marketChartData = Arrays.asList("120", "135", "142", "158", "167", "175");
        }

        // Getters and Setters
        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
        
        public String getBrandValue() { return brandValue; }
        public void setBrandValue(String brandValue) { this.brandValue = brandValue; }
        
        public String getMarketShare() { return marketShare; }
        public void setMarketShare(String marketShare) { this.marketShare = marketShare; }
        
        public String getReportDate() { return reportDate; }
        public void setReportDate(String reportDate) { this.reportDate = reportDate; }
        
        public String getLogoUrl() { return logoUrl; }
        public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
        
        public List<PerformanceMetric> getPerformanceMetrics() { return performanceMetrics; }
        public void setPerformanceMetrics(List<PerformanceMetric> performanceMetrics) { this.performanceMetrics = performanceMetrics; }
        
        public List<String> getMarketChartLabels() { return marketChartLabels; }
        public void setMarketChartLabels(List<String> marketChartLabels) { this.marketChartLabels = marketChartLabels; }
        
        public List<String> getMarketChartData() { return marketChartData; }
        public void setMarketChartData(List<String> marketChartData) { this.marketChartData = marketChartData; }
    }

    /**
     * 性能指标数据模型
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

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCurrentValue() { return currentValue; }
        public void setCurrentValue(String currentValue) { this.currentValue = currentValue; }
        
        public String getCompareValue() { return compareValue; }
        public void setCompareValue(String compareValue) { this.compareValue = compareValue; }
        
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }
} 