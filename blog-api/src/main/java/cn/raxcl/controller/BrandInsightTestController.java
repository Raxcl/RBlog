package cn.raxcl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 品牌洞察测试控制器
 * 生成包含图片和图标的完整HTML文件
 */
@Controller
@RequestMapping("/api/brand")
public class BrandInsightTestController {

    private static final Logger log = LoggerFactory.getLogger(BrandInsightTestController.class);

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 生成品牌洞察HTML文件的测试接口
     * GET /api/brand/insight/generate
     */
    @GetMapping("/insight/generate")
    public ResponseEntity<String> generateBrandInsightHtml() {
        try {
            // 创建模板上下文
            Context context = new Context();
            
            // 设置基本信息
            context.setVariable("merchantName", "阿里巴巴集团控股有限公司");
            context.setVariable("brandValue", "2,680亿美元");
            context.setVariable("marketShare", "28.5");
            context.setVariable("reportDate", "2024年第一季度");
            context.setVariable("logoUrl", "https://img.alicdn.com/tfs/TB1_uT8a5ERMeJjSspiXXbZLFXa-143-59.png");
            
            // 设置性能指标数据
            List<PerformanceMetric> performanceMetrics = Arrays.asList(
                new PerformanceMetric("品牌知名度", "92.3%", "上升 3.2%", "↗ 上升"),
                new PerformanceMetric("客户满意度", "4.8分", "上升 0.2分", "↗ 上升"),
                new PerformanceMetric("市场渗透率", "35.7%", "上升 4.1%", "↗ 上升"),
                new PerformanceMetric("复购率", "68.4%", "上升 2.3%", "↗ 上升"),
                new PerformanceMetric("净推荐值", "85分", "上升 5分", "↗ 上升"),
                new PerformanceMetric("品牌忠诚度", "76.2%", "持平", "→ 持平")
            );
            context.setVariable("performanceMetrics", performanceMetrics);
            
            // 设置市场趋势数据
            List<String> marketChartLabels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月");
            List<String> marketChartData = Arrays.asList("2450", "2680", "2890", "3120", "3350", "3580", "3720", "3950");
            context.setVariable("marketChartLabels", marketChartLabels);
            context.setVariable("marketChartData", marketChartData);
            
            // 渲染模板
            String htmlContent = templateEngine.process("brand-insight", context);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set("Content-Disposition", "inline; filename=brand-insight-report.html");
            headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
            
            log.info("成功生成品牌洞察HTML文件，企业: {}", "阿里巴巴集团控股有限公司");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(htmlContent);
                    
        } catch (Exception e) {
            log.error("生成品牌洞察HTML文件失败", e);
            return ResponseEntity.internalServerError()
                    .body("<html><body><h1>生成失败</h1><p>错误信息: " + e.getMessage() + "</p></body></html>");
        }
    }

    /**
     * 生成自定义品牌洞察HTML文件
     * POST /api/brand/insight/generate
     */
    @PostMapping("/insight/generate")
    public ResponseEntity<String> generateCustomBrandInsightHtml(@RequestBody BrandInsightRequest request) {
        try {
            // 创建模板上下文
            Context context = new Context();
            
            // 设置请求数据
            context.setVariable("merchantName", request.getMerchantName());
            context.setVariable("brandValue", request.getBrandValue());
            context.setVariable("marketShare", request.getMarketShare());
            context.setVariable("reportDate", request.getReportDate());
            context.setVariable("logoUrl", request.getLogoUrl());
            context.setVariable("performanceMetrics", request.getPerformanceMetrics());
            context.setVariable("marketChartLabels", request.getMarketChartLabels());
            context.setVariable("marketChartData", request.getMarketChartData());
            
            // 渲染模板
            String htmlContent = templateEngine.process("brand-insight", context);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set("Content-Disposition", "inline; filename=" + 
                       request.getMerchantName().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_") + 
                       "_brand_insight.html");
            headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
            
            log.info("成功生成自定义品牌洞察HTML文件，企业: {}", request.getMerchantName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(htmlContent);
                    
        } catch (Exception e) {
            log.error("生成自定义品牌洞察HTML文件失败", e);
            return ResponseEntity.internalServerError()
                    .body("<html><body><h1>生成失败</h1><p>错误信息: " + e.getMessage() + "</p></body></html>");
        }
    }

    /**
     * 获取品牌洞察示例数据
     * GET /api/brand/insight/sample-data
     */
    @GetMapping("/insight/sample-data")
    @ResponseBody
    public BrandInsightRequest getSampleData() {
        BrandInsightRequest sample = new BrandInsightRequest();
        sample.setMerchantName("腾讯控股有限公司");
        sample.setBrandValue("4,280亿美元");
        sample.setMarketShare("32.1");
        sample.setReportDate("2024年第一季度");
        sample.setLogoUrl("https://mat1.gtimg.com/pingjs/ext2020/qqindex2018/dist/img/qq_logo_2x.png");
        
        // 设置性能指标
        List<PerformanceMetric> metrics = Arrays.asList(
            new PerformanceMetric("品牌知名度", "94.5%", "上升 2.8%", "↗ 上升"),
            new PerformanceMetric("用户活跃度", "89.2%", "上升 1.5%", "↗ 上升"),
            new PerformanceMetric("市场渗透率", "41.3%", "上升 3.7%", "↗ 上升"),
            new PerformanceMetric("用户留存率", "72.8%", "下降 0.8%", "↘ 下降"),
            new PerformanceMetric("净推荐值", "78分", "持平", "→ 持平")
        );
        sample.setPerformanceMetrics(metrics);
        
        // 设置图表数据
        sample.setMarketChartLabels(Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q1'24", "Q2'24"));
        sample.setMarketChartData(Arrays.asList("3200", "3450", "3680", "3920", "4150", "4280"));
        
        return sample;
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

        public BrandInsightRequest() {
            initDefaultData();
        }

        private void initDefaultData() {
            performanceMetrics = Arrays.asList(
                new PerformanceMetric("品牌知名度", "78.5%", "上升 5.2%", "↗ 上升"),
                new PerformanceMetric("客户满意度", "4.6分", "上升 0.3分", "↗ 上升"),
                new PerformanceMetric("市场渗透率", "23.4%", "上升 2.1%", "↗ 上升"),
                new PerformanceMetric("复购率", "45.8%", "下降 1.2%", "↘ 下降"),
                new PerformanceMetric("净推荐值", "72分", "持平", "→ 持平")
            );
            
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