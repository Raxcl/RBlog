package cn.raxcl.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
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

import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
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
     * 生成地区洞察散点图PDF
     */
    @GetMapping("/pdf/scatter")
    public ResponseEntity<byte[]> generateScatterPdf() {
        try {
            // 创建PDF文档
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // 设置中文字体
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font subtitleFont = new com.itextpdf.text.Font(baseFont, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);
            
            // 添加标题
            Paragraph title = new Paragraph("地区洞察分析报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // 添加子标题
            Paragraph subtitle = new Paragraph("01 地区洞察", subtitleFont);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);
            
            // 添加描述
            Paragraph description = new Paragraph("本图表展示了各省市在市场饱和度与销售坪效增长率方面的表现分布情况", contentFont);
            description.setSpacingAfter(20);
            document.add(description);
            
            // 添加生成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Paragraph generateTime = new Paragraph("生成时间: " + sdf.format(new Date()), contentFont);
            generateTime.setAlignment(Element.ALIGN_RIGHT);
            generateTime.setSpacingAfter(30);
            document.add(generateTime);
            
            // 生成散点图
            byte[] scatterImageBytes = generateScatterChart();
            Image scatterImage = Image.getInstance(scatterImageBytes);
            
            // 调整图片大小
            float scalePercent = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / scatterImage.getWidth();
            if (scalePercent < 1.0f) {
                scatterImage.scalePercent(scalePercent * 100);
            }
            
            scatterImage.setAlignment(Element.ALIGN_CENTER);
            document.add(scatterImage);
            
            // 添加说明文字
            Paragraph explanation = new Paragraph("想查看该品类在更多省份、城市的地区洞察，请联系业务人员", contentFont);
            explanation.setAlignment(Element.ALIGN_CENTER);
            explanation.setSpacingBefore(20);
            document.add(explanation);
            
            document.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=region-analysis.pdf");
            headers.setContentLength(outputStream.size());
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 生成地区洞察散点图
     */
    private byte[] generateScatterChart() throws Exception {
        // 创建数据集
        DefaultXYDataset dataset = new DefaultXYDataset();
        
        // 机会区域数据 (紫色)
        double[][] opportunityData = {
            {25, 30}, // x坐标（市场饱和度）, y坐标（销售坪效增长率）
            {65, 55}  // 山东省、重庆市的大致位置
        };
        dataset.addSeries("机会区域", opportunityData);
        
        // 热门区域数据 (橙色)
        double[][] hotData = {
            {75, 80, 85}, 
            {65, 45, 35}  // 吉林省、辽宁省等
        };
        dataset.addSeries("热门区域", hotData);
        
        // 观望评估数据 (绿色)
        double[][] waitData = {
            {35}, 
            {25}   // 河北省
        };
        dataset.addSeries("观望评估", waitData);
        
        // 趋于饱和数据 (蓝色)
        double[][] saturatedData = {
            {70, 85}, 
            {15, 35}   // 广东省、上海市
        };
        dataset.addSeries("趋于饱和", saturatedData);
        
        // 创建散点图
        JFreeChart chart = ChartFactory.createScatterPlot(
            "地区洞察",
            "市场饱和度",
            "销售坪效增长率",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // 不显示图例，我们用颜色区分
            true,
            false
        );
        
        // 设置中文字体
        Font font = new Font("宋体", Font.PLAIN, 12);
        Font titleFont = new Font("宋体", Font.BOLD, 16);
        Font axisFont = new Font("宋体", Font.PLAIN, 14);
        
        chart.getTitle().setFont(titleFont);
        
        // 获取绘图对象
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLabelFont(axisFont);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(axisFont);
        plot.getRangeAxis().setTickLabelFont(font);
        
        // 设置背景色
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // 添加分割线（象限分割）
        plot.addDomainMarker(new org.jfree.chart.plot.ValueMarker(50.0, Color.GRAY, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f, 5.0f}, 0.0f)));
        plot.addRangeMarker(new org.jfree.chart.plot.ValueMarker(40.0, Color.GRAY, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f, 5.0f}, 0.0f)));
        
        // 设置渲染器和颜色
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        
        // 设置各系列的颜色和形状
        renderer.setSeriesPaint(0, new Color(168, 133, 200)); // 机会区域 - 紫色
        renderer.setSeriesPaint(1, new Color(255, 165, 79));  // 热门区域 - 橙色  
        renderer.setSeriesPaint(2, new Color(119, 221, 187)); // 观望评估 - 绿色
        renderer.setSeriesPaint(3, new Color(115, 148, 255)); // 趋于饱和 - 蓝色
        
        // 设置数据点大小
        for (int i = 0; i < 4; i++) {
            renderer.setSeriesShape(i, new Ellipse2D.Double(-8, -8, 16, 16));
        }
        
        plot.setRenderer(renderer);
        
        // 设置坐标轴范围
        plot.getDomainAxis().setRange(0, 100);
        plot.getRangeAxis().setRange(0, 100);
        
        // 生成图片
        BufferedImage bufferedImage = chart.createBufferedImage(900, 700);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(outputStream, bufferedImage);
        
        return outputStream.toByteArray();
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