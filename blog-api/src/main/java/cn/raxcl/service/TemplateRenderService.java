package cn.raxcl.service;

import cn.raxcl.dto.BrandInsightData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板渲染服务
 * 使用Spring Thymeleaf模板引擎渲染HTML模板
 */
@Service
public class TemplateRenderService {
    
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    
    /**
     * 渲染品牌洞察模板
     */
    public String renderBrandInsightTemplate(BrandInsightData data) {
        try {
            if (templateEngine != null) {
                // 使用Thymeleaf模板引擎
                Context context = new Context();
                context.setVariables(buildTemplateData(data));
                return templateEngine.process("brand-insight", context);
            } else {
                // 备选方案：生成简单HTML
                return generateSimpleHtml(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发生错误时，返回备选HTML
            return generateSimpleHtml(data);
        }
    }
    
    /**
     * 构建模板数据
     */
    private Map<String, Object> buildTemplateData(BrandInsightData data) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("merchantName", data.getMerchantName());
        templateData.put("logoUrl", data.getLogoUrl());
        templateData.put("backgroundImageUrl", data.getBackgroundImageUrl());
        templateData.put("reportDate", data.getReportDate());
        templateData.put("brandValue", data.getBrandValue());
        templateData.put("marketShare", data.getMarketShare());
        templateData.put("performanceMetrics", data.getPerformanceMetrics());
        templateData.put("marketChartLabels", data.getMarketChartLabels());
        templateData.put("marketChartData", data.getMarketChartData());
        
        return templateData;
    }
    
    /**
     * 生成简单HTML（备选方案）
     */
    private String generateSimpleHtml(BrandInsightData data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>").append(data.getMerchantName()).append(" 品牌洞察报告</title>\n");
        html.append("    <style>\n");
        html.append("        @page { size: A4; margin: 20mm; }\n");
        html.append("        body { font-family: 'Microsoft YaHei', 'SimSun', Arial, sans-serif; }\n");
        html.append("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center; }\n");
        html.append("        .content { padding: 30px; }\n");
        html.append("        .section { margin-bottom: 30px; }\n");
        html.append("        .section-title { font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #e9ecef; padding-bottom: 8px; }\n");
        html.append("        .metrics-table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n");
        html.append("        .metrics-table th, .metrics-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
        html.append("        .metrics-table th { background-color: #667eea; color: white; }\n");
        html.append("        .overview-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin: 20px 0; }\n");
        html.append("        .overview-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; }\n");
        html.append("        .footer { background: #2c3e50; color: white; padding: 20px; text-align: center; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 头部
        html.append("    <div class=\"header\">\n");
        html.append("        <h1>").append(data.getMerchantName()).append("</h1>\n");
        html.append("        <h2>品牌洞察报告</h2>\n");
        html.append("        <p>报告期间: ").append(data.getReportDate()).append("</p>\n");
        html.append("    </div>\n");
        
        // 内容
        html.append("    <div class=\"content\">\n");
        
        // 品牌概览
        html.append("        <div class=\"section\">\n");
        html.append("            <h2 class=\"section-title\">品牌概览</h2>\n");
        html.append("            <div class=\"overview-grid\">\n");
        html.append("                <div class=\"overview-card\">\n");
        html.append("                    <div>品牌价值</div>\n");
        html.append("                    <div style=\"font-size: 24px; font-weight: bold; margin-top: 8px;\">").append(data.getBrandValue()).append("</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"overview-card\">\n");
        html.append("                    <div>市场份额</div>\n");
        html.append("                    <div style=\"font-size: 24px; font-weight: bold; margin-top: 8px;\">").append(data.getMarketShare()).append("%</div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 性能指标
        if (data.getPerformanceMetrics() != null && !data.getPerformanceMetrics().isEmpty()) {
            html.append("        <div class=\"section\">\n");
            html.append("            <h2 class=\"section-title\">关键绩效指标</h2>\n");
            html.append("            <table class=\"metrics-table\">\n");
            html.append("                <thead>\n");
            html.append("                    <tr><th>指标名称</th><th>当前值</th><th>上期对比</th><th>趋势</th></tr>\n");
            html.append("                </thead>\n");
            html.append("                <tbody>\n");
            
            for (BrandInsightData.PerformanceMetric metric : data.getPerformanceMetrics()) {
                html.append("                    <tr>\n");
                html.append("                        <td>").append(metric.getName()).append("</td>\n");
                html.append("                        <td>").append(metric.getCurrentValue()).append("</td>\n");
                html.append("                        <td>").append(metric.getCompareValue()).append("</td>\n");
                html.append("                        <td style=\"color: ").append(metric.getTrend().contains("↗") ? "#28a745" : "#dc3545").append(";\">").append(metric.getTrend()).append("</td>\n");
                html.append("                    </tr>\n");
            }
            
            html.append("                </tbody>\n");
            html.append("            </table>\n");
            html.append("        </div>\n");
        }
        
        html.append("    </div>\n");
        
        // 页脚
        html.append("    <div class=\"footer\">\n");
        html.append("        <p>© 2024 RBlog 企业版. 保留所有权利.</p>\n");
        html.append("        <p style=\"font-size: 11px; opacity: 0.8;\">生成时间: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
} 