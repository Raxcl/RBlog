package cn.raxcl.controller;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.ChartDataPoint;
import cn.raxcl.dto.PdfGenerateRequest;
import cn.raxcl.service.PdfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF报告生成控制器
 * @author raxcl
 */
@RestController
@RequestMapping("/api/pdf-report")
@CrossOrigin(origins = "*")
public class PdfReportController {

    @Autowired
    private PdfReportService pdfReportService;

    /**
     * 生成PDF报告
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfGenerateRequest request) {
        try {
            byte[] pdfContent = pdfReportService.generatePdfReport(request);
            
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "report.pdf";
            }
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            headers.setContentLength(pdfContent.length);
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 在线预览PDF报告
     */
    @PostMapping("/preview")
    public ResponseEntity<byte[]> previewPdf(@RequestBody PdfGenerateRequest request) {
        try {
            byte[] pdfContent = pdfReportService.generatePdfReport(request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=preview.pdf");
            headers.setContentLength(pdfContent.length);
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 生成示例PDF报告
     */
    @GetMapping("/sample")
    public ResponseEntity<byte[]> generateSamplePdf() {
        try {
            PdfGenerateRequest request = createSampleRequest();
            byte[] pdfContent = pdfReportService.generatePdfReport(request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=sample-report.pdf");
            headers.setContentLength(pdfContent.length);
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 创建示例请求数据
     */
    private PdfGenerateRequest createSampleRequest() {
        PdfGenerateRequest request = new PdfGenerateRequest();
        request.setTitle("数据分析报告");
        request.setDescription("本报告展示了系统的各项关键指标数据统计分析结果，包括用户活跃度、销售业绩和流量来源等重要数据指标。");
        request.setFileName("data-analysis-report");
        request.setIncludeWatermark(true);
        request.setWatermarkText("机密文档");
        
        List<ChartData> charts = new ArrayList<>();
        
        // 用户活跃度折线图
        ChartData userActivityChart = new ChartData();
        userActivityChart.setTitle("用户活跃度趋势");
        userActivityChart.setChartType("line");
        userActivityChart.setXAxisLabel("时间");
        userActivityChart.setYAxisLabel("活跃用户数");
        userActivityChart.setDataPoints(createUserActivityData());
        charts.add(userActivityChart);
        
        // 销售数据柱状图
        ChartData salesChart = new ChartData();
        salesChart.setTitle("各产品销售数据");
        salesChart.setChartType("bar");
        salesChart.setXAxisLabel("产品");
        salesChart.setYAxisLabel("销售额（万元）");
        salesChart.setDataPoints(createSalesData());
        charts.add(salesChart);
        
        // 流量来源饼图
        ChartData trafficChart = new ChartData();
        trafficChart.setTitle("流量来源分布");
        trafficChart.setChartType("pie");
        trafficChart.setDataPoints(createTrafficData());
        charts.add(trafficChart);
        
        request.setCharts(charts);
        return request;
    }

    /**
     * 创建用户活跃度数据
     */
    private List<ChartDataPoint> createUserActivityData() {
        List<ChartDataPoint> dataPoints = new ArrayList<>();
        dataPoints.add(new ChartDataPoint("1月", 1250.0));
        dataPoints.add(new ChartDataPoint("2月", 1580.0));
        dataPoints.add(new ChartDataPoint("3月", 1320.0));
        dataPoints.add(new ChartDataPoint("4月", 1680.0));
        dataPoints.add(new ChartDataPoint("5月", 1920.0));
        dataPoints.add(new ChartDataPoint("6月", 1750.0));
        dataPoints.add(new ChartDataPoint("7月", 2100.0));
        return dataPoints;
    }

    /**
     * 创建销售数据
     */
    private List<ChartDataPoint> createSalesData() {
        List<ChartDataPoint> dataPoints = new ArrayList<>();
        dataPoints.add(new ChartDataPoint("产品A", 320.5));
        dataPoints.add(new ChartDataPoint("产品B", 268.9));
        dataPoints.add(new ChartDataPoint("产品C", 456.2));
        dataPoints.add(new ChartDataPoint("产品D", 189.7));
        dataPoints.add(new ChartDataPoint("产品E", 343.1));
        return dataPoints;
    }

    /**
     * 创建流量来源数据
     */
    private List<ChartDataPoint> createTrafficData() {
        List<ChartDataPoint> dataPoints = new ArrayList<>();
        dataPoints.add(new ChartDataPoint("搜索引擎", 45.2));
        dataPoints.add(new ChartDataPoint("直接访问", 28.7));
        dataPoints.add(new ChartDataPoint("社交媒体", 16.4));
        dataPoints.add(new ChartDataPoint("邮件营销", 9.7));
        return dataPoints;
    }
} 