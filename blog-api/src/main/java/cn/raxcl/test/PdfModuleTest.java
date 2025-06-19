package cn.raxcl.test;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.ChartDataPoint;
import cn.raxcl.dto.PdfGenerateRequest;
import cn.raxcl.service.ChartService;
import cn.raxcl.service.PdfReportService;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * PDF模块测试类
 * 
 * @author raxcl
 * @date 2024-01-01
 */
public class PdfModuleTest {
    
    public static void main(String[] args) {
        try {
            // 创建服务实例
            ChartService chartService = new ChartService();
            PdfReportService pdfReportService = new PdfReportService(chartService);
            
            // 创建测试数据
            PdfGenerateRequest request = createTestRequest();
            
            // 生成PDF
            byte[] pdfBytes = pdfReportService.generatePdfReport(request);
            
            // 保存到文件
            try (FileOutputStream fos = new FileOutputStream("test-report.pdf")) {
                fos.write(pdfBytes);
                System.out.println("PDF生成成功！文件保存为: test-report.pdf");
                System.out.println("文件大小: " + pdfBytes.length + " 字节");
            }
            
        } catch (Exception e) {
            System.err.println("PDF生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static PdfGenerateRequest createTestRequest() {
        PdfGenerateRequest request = new PdfGenerateRequest();
        request.setTitle("测试报告");
        request.setDescription("这是一个PDF生成功能的测试报告，包含了多种类型的图表展示。");
        request.setFileName("test-report.pdf");
        request.setIncludeWatermark(true);
        request.setWatermarkText("测试水印");
        
        // 创建图表数据
        ChartData barChart = new ChartData();
        barChart.setTitle("销售数据对比");
        barChart.setChartType("bar");
        barChart.setXAxisLabel("产品");
        barChart.setYAxisLabel("销售额(万元)");
        barChart.setDataPoints(Arrays.asList(
            new ChartDataPoint("产品A", 120.0),
            new ChartDataPoint("产品B", 85.0),
            new ChartDataPoint("产品C", 95.0),
            new ChartDataPoint("产品D", 150.0)
        ));
        
        ChartData lineChart = new ChartData();
        lineChart.setTitle("用户增长趋势");
        lineChart.setChartType("line");
        lineChart.setXAxisLabel("月份");
        lineChart.setYAxisLabel("用户数(万)");
        lineChart.setDataPoints(Arrays.asList(
            new ChartDataPoint("1月", 10.0),
            new ChartDataPoint("2月", 15.0),
            new ChartDataPoint("3月", 22.0),
            new ChartDataPoint("4月", 28.0),
            new ChartDataPoint("5月", 35.0),
            new ChartDataPoint("6月", 42.0)
        ));
        
        ChartData pieChart = new ChartData();
        pieChart.setTitle("流量来源分布");
        pieChart.setChartType("pie");
        pieChart.setDataPoints(Arrays.asList(
            new ChartDataPoint("搜索引擎", 45.0),
            new ChartDataPoint("直接访问", 30.0),
            new ChartDataPoint("社交媒体", 15.0),
            new ChartDataPoint("其他", 10.0)
        ));
        
        request.setCharts(Arrays.asList(barChart, lineChart, pieChart));
        
        return request;
    }
} 