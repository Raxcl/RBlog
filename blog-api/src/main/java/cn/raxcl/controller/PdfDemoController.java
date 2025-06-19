package cn.raxcl.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PDF生成演示控制器
 * @author raxcl
 */
@RestController
@RequestMapping("/api/demo")
public class PdfDemoController {
    
    /**
     * 生成包含图表的示例PDF
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateDemoPdf() {
        try {
            // 创建PDF文档
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // 设置中文字体
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);
            
            // 添加标题
            Paragraph title = new Paragraph("数据分析报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // 添加描述
            Paragraph description = new Paragraph("本报告展示了系统的数据统计分析结果", contentFont);
            description.setSpacingAfter(20);
            document.add(description);
            
            // 添加生成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Paragraph generateTime = new Paragraph("生成时间: " + sdf.format(new Date()), contentFont);
            generateTime.setAlignment(Element.ALIGN_RIGHT);
            generateTime.setSpacingAfter(30);
            document.add(generateTime);
            
            // 生成柱状图
            byte[] chartImageBytes = generateBarChart();
            Image chartImage = Image.getInstance(chartImageBytes);
            
            // 调整图片大小
            float scalePercent = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / chartImage.getWidth();
            if (scalePercent < 1.0f) {
                chartImage.scalePercent(scalePercent * 100);
            }
            
            chartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(chartImage);
            
            document.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=demo-report.pdf");
            headers.setContentLength(outputStream.size());
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 生成柱状图数据
     */
    private byte[] generateBarChart() throws Exception {
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(120, "销售额", "产品A");
        dataset.addValue(98, "销售额", "产品B");
        dataset.addValue(156, "销售额", "产品C");
        dataset.addValue(89, "销售额", "产品D");
        dataset.addValue(143, "销售额", "产品E");
        
        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
            "产品销售数据统计",
            "产品",
            "销售额（万元）",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // 设置中文字体
        Font font = new Font("宋体", Font.PLAIN, 12);
        Font titleFont = new Font("宋体", Font.BOLD, 16);
        
        chart.getTitle().setFont(titleFont);
        chart.getLegend().setItemFont(font);
        chart.getCategoryPlot().getDomainAxis().setLabelFont(font);
        chart.getCategoryPlot().getDomainAxis().setTickLabelFont(font);
        chart.getCategoryPlot().getRangeAxis().setLabelFont(font);
        chart.getCategoryPlot().getRangeAxis().setTickLabelFont(font);
        
        // 生成图片
        BufferedImage bufferedImage = chart.createBufferedImage(800, 600);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(outputStream, bufferedImage);
        
        return outputStream.toByteArray();
    }
} 