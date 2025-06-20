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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;

import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;

import cn.raxcl.service.FopPdfService;
import cn.raxcl.dto.PdfGenerateRequest;
import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.BrandInsightData;
import cn.raxcl.service.TemplateRenderService;
import cn.raxcl.service.HtmlToPdfService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PDF生成演示控制器
 * @author raxcl
 */
@RestController
@RequestMapping("/api/demo")
public class PdfDemoController {
    
    @Autowired
    private FopPdfService fopPdfService;
    
    // 添加模板渲染服务的注入
    @Autowired(required = false)
    private TemplateRenderService templateRenderService;
    
    // 添加HTML转PDF服务的注入
    @Autowired(required = false)  
    private HtmlToPdfService htmlToPdfService;
    
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
     * 使用ECharts生成地区洞察散点图PDF（推荐方案）
     */
    @GetMapping("/pdf/scatter-echarts")
    public ResponseEntity<byte[]> generateEChartsScatterPdf() {
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
            Paragraph title = new Paragraph("地区洞察分析报告（ECharts版本）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // 添加子标题
            Paragraph subtitle = new Paragraph("01 地区洞察", subtitleFont);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);
            
            // 添加描述
            Paragraph description = new Paragraph("本图表使用ECharts生成，展示了各省市在市场饱和度与销售坪效增长率方面的表现分布情况", contentFont);
            description.setSpacingAfter(20);
            document.add(description);
            
            // 添加生成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Paragraph generateTime = new Paragraph("生成时间: " + sdf.format(new Date()), contentFont);
            generateTime.setAlignment(Element.ALIGN_RIGHT);
            generateTime.setSpacingAfter(30);
            document.add(generateTime);
            
            // 生成ECharts气泡图
            byte[] echartsImageBytes = generateEChartsImage();
            Image echartsImage = Image.getInstance(echartsImageBytes);
            
            // 调整图片大小
            float scalePercent = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / echartsImage.getWidth();
            if (scalePercent < 1.0f) {
                echartsImage.scalePercent(scalePercent * 100);
            }
            
            echartsImage.setAlignment(Element.ALIGN_CENTER);
            document.add(echartsImage);
            
            // 添加说明文字
            Paragraph explanation = new Paragraph("想查看该品类在更多省份、城市的地区洞察，请联系业务人员", contentFont);
            explanation.setAlignment(Element.ALIGN_CENTER);
            explanation.setSpacingBefore(20);
            document.add(explanation);
            
            document.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=region-analysis-echarts.pdf");
            headers.setContentLength(outputStream.size());
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 使用ECharts生成双图表PDF（地区洞察 + GDP生命期望）
     */
    @GetMapping("/pdf/dual-charts")
    public ResponseEntity<byte[]> generateDualChartsPdf() {
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
            
            // 添加主标题
            Paragraph mainTitle = new Paragraph("数据分析综合报告", titleFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(20);
            document.add(mainTitle);
            
            // 添加生成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Paragraph generateTime = new Paragraph("生成时间: " + sdf.format(new Date()), contentFont);
            generateTime.setAlignment(Element.ALIGN_RIGHT);
            generateTime.setSpacingAfter(30);
            document.add(generateTime);
            
            // 第一张图：地区洞察
            Paragraph subtitle1 = new Paragraph("01 地区洞察分析", subtitleFont);
            subtitle1.setSpacingAfter(15);
            document.add(subtitle1);
            
            Paragraph description1 = new Paragraph("本图表展示了各省市在市场饱和度与销售坪效增长率方面的表现分布情况", contentFont);
            description1.setSpacingAfter(20);
            document.add(description1);
            
            // 生成第一张图
            byte[] chart1ImageBytes = generateRegionInsightImage();
            Image chart1Image = Image.getInstance(chart1ImageBytes);
            
            // 调整图片大小
            float scalePercent1 = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / chart1Image.getWidth();
            if (scalePercent1 < 1.0f) {
                chart1Image.scalePercent(scalePercent1 * 100);
            }
            chart1Image.setAlignment(Element.ALIGN_CENTER);
            document.add(chart1Image);
            
            // 添加页面分隔
            document.newPage();
            
            // 第二张图：GDP与生命期望
            Paragraph subtitle2 = new Paragraph("02 GDP与生命期望关系分析", subtitleFont);
            subtitle2.setSpacingAfter(15);
            document.add(subtitle2);
            
            Paragraph description2 = new Paragraph("本图表展示了各国GDP与生命期望的关系变化（1990年 vs 2015年）", contentFont);
            description2.setSpacingAfter(20);
            document.add(description2);
            
            // 生成第二张图
            byte[] chart2ImageBytes = generateGdpLifeExpectancyImage();
            Image chart2Image = Image.getInstance(chart2ImageBytes);
            
            // 调整图片大小
            float scalePercent2 = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / chart2Image.getWidth();
            if (scalePercent2 < 1.0f) {
                chart2Image.scalePercent(scalePercent2 * 100);
            }
            chart2Image.setAlignment(Element.ALIGN_CENTER);
            document.add(chart2Image);
            
            // 添加总结说明
            Paragraph conclusion = new Paragraph("以上图表展示了不同维度的数据分析结果，为决策提供参考依据。", contentFont);
            conclusion.setAlignment(Element.ALIGN_CENTER);
            conclusion.setSpacingBefore(20);
            document.add(conclusion);
            
            document.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=dual-charts-analysis.pdf");
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
        
        // 机会区域数据 (紫色) - 大气泡表示高潜力
        double[][] opportunityData = {
            {25, 30}, // x坐标（市场饱和度）, y坐标（销售坪效增长率）
            {65, 55}  // 山东省、重庆市的大致位置
        };
        dataset.addSeries("机会区域", opportunityData);
        
        // 热门区域数据 (橙色) - 中大气泡表示活跃市场
        double[][] hotData = {
            {75, 80, 85}, 
            {65, 45, 35}  // 吉林省、辽宁省等
        };
        dataset.addSeries("热门区域", hotData);
        
        // 观望评估数据 (绿色) - 中等气泡
        double[][] waitData = {
            {35}, 
            {25}   // 河北省
        };
        dataset.addSeries("观望评估", waitData);
        
        // 趋于饱和数据 (蓝色) - 较大气泡表示成熟市场
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
        
        // 创建自定义渲染器实现气泡效果
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {
            @Override
            public java.awt.Paint getItemPaint(int series, int item) {
                Color baseColor;
                Color lightColor;
                
                switch (series) {
                    case 0: // 机会区域 - 紫色渐变
                        baseColor = new Color(138, 103, 180);
                        lightColor = new Color(208, 183, 240, 200);
                        break;
                    case 1: // 热门区域 - 橙色渐变  
                        baseColor = new Color(235, 135, 49);
                        lightColor = new Color(255, 195, 129, 200);
                        break;
                    case 2: // 观望评估 - 绿色渐变
                        baseColor = new Color(89, 201, 167);
                        lightColor = new Color(149, 231, 207, 200);
                        break;
                    case 3: // 趋于饱和 - 蓝色渐变
                        baseColor = new Color(85, 128, 235);
                        lightColor = new Color(145, 178, 255, 200);
                        break;
                    default:
                        baseColor = Color.GRAY;
                        lightColor = Color.LIGHT_GRAY;
                }
                
                // 创建更大范围的径向渐变效果模拟气泡
                return new GradientPaint(-15, -15, lightColor, 25, 25, baseColor, true);
            }
        };
        
        // 设置更大的气泡大小
        double[] bubbleSizes = {45, 42, 38, 48}; // 显著增大气泡尺寸
        
        for (int i = 0; i < 4; i++) {
            double size = bubbleSizes[i];
            // 创建气泡形状 - 使用椭圆
            renderer.setSeriesShape(i, new Ellipse2D.Double(-size/2, -size/2, size, size));
            
            // 设置描边效果增加立体感
            renderer.setSeriesOutlineStroke(i, new BasicStroke(2.0f));
            
            // 设置描边颜色（比填充色稍深）
            Color outlineColor;
            switch (i) {
                case 0: outlineColor = new Color(108, 73, 150, 180); break;  // 机会区域描边
                case 1: outlineColor = new Color(205, 105, 19, 180); break;  // 热门区域描边
                case 2: outlineColor = new Color(59, 171, 137, 180); break;  // 观望评估描边
                case 3: outlineColor = new Color(55, 98, 205, 180); break;   // 趋于饱和描边
                default: outlineColor = Color.DARK_GRAY;
            }
            renderer.setSeriesOutlinePaint(i, outlineColor);
            renderer.setSeriesShapesFilled(i, true);
            renderer.setSeriesShapesVisible(i, true);
        }
        
        plot.setRenderer(renderer);
        
        // 设置坐标轴范围
        plot.getDomainAxis().setRange(0, 100);
        plot.getRangeAxis().setRange(0, 100);
        
        // 生成图片 - 增加分辨率获得更好的气泡效果
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
    
    /**
     * 生成ECharts气泡图图片
     * 注意：此方法需要添加Selenium WebDriver依赖和Chrome Driver
     */
    private byte[] generateEChartsImage() throws Exception {
        // 创建ECharts配置的HTML页面
        String htmlContent = generateEChartsHtml();
        
        // TODO: 使用Selenium WebDriver渲染ECharts
        // 这里返回模拟的图片数据，实际项目中需要实现WebDriver渲染
        // 可以参考以下步骤：
        // 1. 创建临时HTML文件
        // 2. 使用Chrome WebDriver加载HTML
        // 3. 等待ECharts渲染完成
        // 4. 截屏并保存为PNG
        // 5. 返回图片字节数组
        
        // 暂时返回JFreeChart生成的图片作为示例
        return generateScatterChart();
    }
    
    /**
     * 生成ECharts配置的HTML页面
     */
    private String generateEChartsHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"utf-8\">\n");
        html.append("    <title>地区洞察气泡图</title>\n");
        html.append("    <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div id=\"chart\" style=\"width: 900px; height: 700px;\"></div>\n");
        html.append("    <script>\n");
        html.append("        var chart = echarts.init(document.getElementById('chart'));\n");
        html.append("        \n");
        html.append("        // 地区洞察数据：[市场饱和度, 销售坪效增长率, 市场规模(影响气泡大小), 地区名称, 分类]\n");
        html.append("        const data = [\n");
        html.append("          [\n");
        html.append("            // 机会区域数据\n");
        html.append("            [25, 65, 80000, '山东省', '机会区域'],\n");
        html.append("            [30, 55, 65000, '重庆市', '机会区域']\n");
        html.append("          ],\n");
        html.append("          [\n");
        html.append("            // 热门区域数据\n");
        html.append("            [75, 65, 75000, '辽宁省', '热门区域'],\n");
        html.append("            [80, 45, 85000, '吉林省', '热门区域'],\n");
        html.append("            [85, 35, 70000, '黑龙江省', '热门区域']\n");
        html.append("          ],\n");
        html.append("          [\n");
        html.append("            // 观望评估数据\n");
        html.append("            [35, 25, 60000, '河北省', '观望评估']\n");
        html.append("          ],\n");
        html.append("          [\n");
        html.append("            // 趋于饱和数据\n");
        html.append("            [70, 15, 120000, '广东省', '趋于饱和'],\n");
        html.append("            [85, 35, 95000, '上海市', '趋于饱和']\n");
        html.append("          ]\n");
        html.append("        ];\n");
        html.append("        \n");
        html.append("        var option = {\n");
        html.append("          backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [\n");
        html.append("            {\n");
        html.append("              offset: 0,\n");
        html.append("              color: '#f7f8fa'\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              offset: 1,\n");
        html.append("              color: '#cdd0d5'\n");
        html.append("            }\n");
        html.append("          ]),\n");
        html.append("          title: {\n");
        html.append("            text: '01 地区洞察',\n");
        html.append("            left: '5%',\n");
        html.append("            top: '3%',\n");
        html.append("            textStyle: {\n");
        html.append("              fontSize: 20,\n");
        html.append("              fontWeight: 'bold',\n");
        html.append("              color: '#2c3e50'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          legend: {\n");
        html.append("            right: '10%',\n");
        html.append("            top: '3%',\n");
        html.append("            data: ['机会区域', '热门区域', '观望评估', '趋于饱和'],\n");
        html.append("            textStyle: {\n");
        html.append("              color: '#34495e'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          grid: {\n");
        html.append("            left: '8%',\n");
        html.append("            top: '15%',\n");
        html.append("            right: '8%',\n");
        html.append("            bottom: '15%'\n");
        html.append("          },\n");
        html.append("          xAxis: {\n");
        html.append("            name: '市场饱和度',\n");
        html.append("            nameLocation: 'middle',\n");
        html.append("            nameGap: 25,\n");
        html.append("            type: 'value',\n");
        html.append("            min: 0,\n");
        html.append("            max: 100,\n");
        html.append("            splitLine: {\n");
        html.append("              lineStyle: {\n");
        html.append("                type: 'dashed',\n");
        html.append("                color: '#bdc3c7'\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            axisLabel: {\n");
        html.append("              color: '#7f8c8d'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          yAxis: {\n");
        html.append("            name: '销售坪效增长率',\n");
        html.append("            nameLocation: 'middle',\n");
        html.append("            nameGap: 35,\n");
        html.append("            type: 'value',\n");
        html.append("            min: 0,\n");
        html.append("            max: 100,\n");
        html.append("            splitLine: {\n");
        html.append("              lineStyle: {\n");
        html.append("                type: 'dashed',\n");
        html.append("                color: '#bdc3c7'\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            scale: true,\n");
        html.append("            axisLabel: {\n");
        html.append("              color: '#7f8c8d'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          series: [\n");
        html.append("            {\n");
        html.append("              name: '机会区域',\n");
        html.append("              data: data[0],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 8;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(138, 103, 180, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: 'rgb(251, 118, 123)'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: 'rgb(204, 46, 72)'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              name: '热门区域',\n");
        html.append("              data: data[1],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 8;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(235, 135, 49, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: '#ffc381'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: '#eb8731'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              name: '观望评估',\n");
        html.append("              data: data[2],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 8;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(89, 201, 167, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: '#95e7cf'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: '#59c9a7'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              name: '趋于饱和',\n");
        html.append("              data: data[3],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 8;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(85, 128, 235, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: '#91b2ff'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: '#5580eb'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            }\n");
        html.append("          ],\n");
        html.append("          tooltip: {\n");
        html.append("            trigger: 'item',\n");
        html.append("            backgroundColor: 'rgba(50, 50, 50, 0.9)',\n");
        html.append("            borderColor: '#ccc',\n");
        html.append("            borderWidth: 1,\n");
        html.append("            textStyle: {\n");
        html.append("              color: '#fff'\n");
        html.append("            },\n");
        html.append("            formatter: function(params) {\n");
        html.append("              return '<strong>' + params.data[3] + '</strong><br/>' +\n");
        html.append("                     '类别: ' + params.data[4] + '<br/>' +\n");
        html.append("                     '市场饱和度: ' + params.data[0] + '%<br/>' +\n");
        html.append("                     '销售坪效增长率: ' + params.data[1] + '%<br/>' +\n");
        html.append("                     '市场规模: ' + (params.data[2]/1000).toFixed(0) + '千';\n");
        html.append("            }\n");
        html.append("          }\n");
        html.append("        };\n");
        html.append("        \n");
        html.append("        chart.setOption(option);\n");
        html.append("        \n");
        html.append("        // 标记渲染完成\n");
        html.append("        window.chartReady = true;\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 生成地区洞察图图片
     */
    private byte[] generateRegionInsightImage() throws Exception {
        // 创建地区洞察配置的HTML页面
        String htmlContent = generateEChartsHtml();
        
        // TODO: 使用Selenium WebDriver渲染ECharts
        // 暂时返回JFreeChart生成的图片作为示例
        return generateScatterChart();
    }
    
    /**
     * 生成GDP与生命期望关系图图片
     */
    private byte[] generateGdpLifeExpectancyImage() throws Exception {
        // 创建GDP生命期望配置的HTML页面
        String htmlContent = generateGdpLifeExpectancyHtml();
        
        // TODO: 使用Selenium WebDriver渲染ECharts
        // 使用不同的JFreeChart图表作为示例
        return generateGdpLifeExpectancyChart();
    }
    
    /**
     * 生成GDP与生命期望关系图表
     */
    private byte[] generateGdpLifeExpectancyChart() throws Exception {
        // 创建数据集
        DefaultXYDataset dataset = new DefaultXYDataset();
        
        // 1990年数据 (红色系) - GDP per capita vs Life Expectancy
        double[][] data1990 = {
            {28604, 31163, 1516, 13670, 28599, 29476, 31476, 28666, 1777, 29550}, // GDP
            {77, 77.4, 68, 74.7, 75, 77.1, 75.4, 78.1, 57.7, 79.1}  // Life Expectancy
        };
        dataset.addSeries("1990年", data1990);
        
        // 2015年数据 (蓝色系) - GDP per capita vs Life Expectancy  
        double[][] data2015 = {
            {44056, 43294, 13334, 21291, 38923, 37599, 44053, 42182, 5903, 36162}, // GDP
            {81.8, 81.7, 76.9, 78.5, 80.8, 81.9, 81.1, 82.8, 66.8, 83.5}  // Life Expectancy
        };
        dataset.addSeries("2015年", data2015);
        
        // 创建散点图
        JFreeChart chart = ChartFactory.createScatterPlot(
            "GDP与生命期望关系分析",
            "GDP per capita (US$)",
            "Life Expectancy (years)",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // 显示图例
            true,
            false
        );
        
        // 设置中文字体
        Font font = new Font("宋体", Font.PLAIN, 12);
        Font titleFont = new Font("宋体", Font.BOLD, 16);
        Font axisFont = new Font("宋体", Font.PLAIN, 14);
        
        chart.getTitle().setFont(titleFont);
        chart.getLegend().setItemFont(font);
        
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
        
        // 创建自定义渲染器实现气泡效果
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {
            @Override
            public java.awt.Paint getItemPaint(int series, int item) {
                Color baseColor;
                Color lightColor;
                
                switch (series) {
                    case 0: // 1990年 - 红色渐变
                        baseColor = new Color(204, 46, 72);
                        lightColor = new Color(251, 118, 123, 200);
                        break;
                    case 1: // 2015年 - 蓝色渐变  
                        baseColor = new Color(25, 183, 207);
                        lightColor = new Color(129, 227, 238, 200);
                        break;
                    default:
                        baseColor = Color.GRAY;
                        lightColor = Color.LIGHT_GRAY;
                }
                
                // 创建径向渐变效果
                return new GradientPaint(-15, -15, lightColor, 25, 25, baseColor, true);
            }
        };
        
        // 设置气泡大小 - 根据人口数量模拟
        double[] bubbleSizes = {35, 40}; // 1990年和2015年的气泡大小
        
        for (int i = 0; i < 2; i++) {
            double size = bubbleSizes[i];
            // 创建气泡形状
            renderer.setSeriesShape(i, new Ellipse2D.Double(-size/2, -size/2, size, size));
            
            // 设置描边效果
            renderer.setSeriesOutlineStroke(i, new BasicStroke(2.0f));
            
            // 设置描边颜色
            Color outlineColor = (i == 0) ? 
                new Color(180, 30, 50, 180) :  // 1990年描边
                new Color(15, 150, 180, 180);  // 2015年描边
            
            renderer.setSeriesOutlinePaint(i, outlineColor);
            renderer.setSeriesShapesFilled(i, true);
            renderer.setSeriesShapesVisible(i, true);
        }
        
        plot.setRenderer(renderer);
        
        // 设置坐标轴范围
        plot.getDomainAxis().setRange(0, 50000);  // GDP范围
        plot.getRangeAxis().setRange(50, 90);     // 生命期望范围
        
        // 生成图片
        BufferedImage bufferedImage = chart.createBufferedImage(900, 700);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(outputStream, bufferedImage);
        
        return outputStream.toByteArray();
    }
    
    /**
     * 生成GDP与生命期望ECharts配置的HTML页面
     */
    private String generateGdpLifeExpectancyHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"utf-8\">\n");
        html.append("    <title>GDP与生命期望关系图</title>\n");
        html.append("    <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div id=\"chart\" style=\"width: 900px; height: 700px;\"></div>\n");
        html.append("    <script>\n");
        html.append("        var chart = echarts.init(document.getElementById('chart'));\n");
        html.append("        \n");
        html.append("        const data = [\n");
        html.append("          [\n");
        html.append("            [28604, 77, 17096869, 'Australia', 1990],\n");
        html.append("            [31163, 77.4, 27662440, 'Canada', 1990],\n");
        html.append("            [1516, 68, 1154605773, 'China', 1990],\n");
        html.append("            [13670, 74.7, 10582082, 'Cuba', 1990],\n");
        html.append("            [28599, 75, 4986705, 'Finland', 1990],\n");
        html.append("            [29476, 77.1, 56943299, 'France', 1990],\n");
        html.append("            [31476, 75.4, 78958237, 'Germany', 1990],\n");
        html.append("            [28666, 78.1, 254830, 'Iceland', 1990],\n");
        html.append("            [1777, 57.7, 870601776, 'India', 1990],\n");
        html.append("            [29550, 79.1, 122249285, 'Japan', 1990],\n");
        html.append("            [2076, 67.9, 20194354, 'North Korea', 1990],\n");
        html.append("            [12087, 72, 42972254, 'South Korea', 1990],\n");
        html.append("            [24021, 75.4, 3397534, 'New Zealand', 1990],\n");
        html.append("            [43296, 76.8, 4240375, 'Norway', 1990],\n");
        html.append("            [10088, 70.8, 38195258, 'Poland', 1990],\n");
        html.append("            [19349, 69.6, 147568552, 'Russia', 1990],\n");
        html.append("            [10670, 67.3, 53994605, 'Turkey', 1990],\n");
        html.append("            [26424, 75.7, 57110117, 'United Kingdom', 1990],\n");
        html.append("            [37062, 75.4, 252847810, 'United States', 1990]\n");
        html.append("          ],\n");
        html.append("          [\n");
        html.append("            [44056, 81.8, 23968973, 'Australia', 2015],\n");
        html.append("            [43294, 81.7, 35939927, 'Canada', 2015],\n");
        html.append("            [13334, 76.9, 1376048943, 'China', 2015],\n");
        html.append("            [21291, 78.5, 11389562, 'Cuba', 2015],\n");
        html.append("            [38923, 80.8, 5503457, 'Finland', 2015],\n");
        html.append("            [37599, 81.9, 64395345, 'France', 2015],\n");
        html.append("            [44053, 81.1, 80688545, 'Germany', 2015],\n");
        html.append("            [42182, 82.8, 329425, 'Iceland', 2015],\n");
        html.append("            [5903, 66.8, 1311050527, 'India', 2015],\n");
        html.append("            [36162, 83.5, 126573481, 'Japan', 2015],\n");
        html.append("            [1390, 71.4, 25155317, 'North Korea', 2015],\n");
        html.append("            [34644, 80.7, 50293439, 'South Korea', 2015],\n");
        html.append("            [34186, 80.6, 4528526, 'New Zealand', 2015],\n");
        html.append("            [64304, 81.6, 5210967, 'Norway', 2015],\n");
        html.append("            [24787, 77.3, 38611794, 'Poland', 2015],\n");
        html.append("            [23038, 73.13, 143456918, 'Russia', 2015],\n");
        html.append("            [19360, 76.5, 78665830, 'Turkey', 2015],\n");
        html.append("            [38225, 81.4, 64715810, 'United Kingdom', 2015],\n");
        html.append("            [53354, 79.1, 321773631, 'United States', 2015]\n");
        html.append("          ]\n");
        html.append("        ];\n");
        html.append("        \n");
        html.append("        var option = {\n");
        html.append("          backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [\n");
        html.append("            {\n");
        html.append("              offset: 0,\n");
        html.append("              color: '#f7f8fa'\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              offset: 1,\n");
        html.append("              color: '#cdd0d5'\n");
        html.append("            }\n");
        html.append("          ]),\n");
        html.append("          title: {\n");
        html.append("            text: 'GDP与生命期望关系分析',\n");
        html.append("            left: '5%',\n");
        html.append("            top: '3%',\n");
        html.append("            textStyle: {\n");
        html.append("              fontSize: 20,\n");
        html.append("              fontWeight: 'bold',\n");
        html.append("              color: '#2c3e50'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          legend: {\n");
        html.append("            right: '10%',\n");
        html.append("            top: '3%',\n");
        html.append("            data: ['1990年', '2015年'],\n");
        html.append("            textStyle: {\n");
        html.append("              color: '#34495e'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          grid: {\n");
        html.append("            left: '8%',\n");
        html.append("            top: '15%',\n");
        html.append("            right: '8%',\n");
        html.append("            bottom: '15%'\n");
        html.append("          },\n");
        html.append("          xAxis: {\n");
        html.append("            name: 'GDP per capita (US$)',\n");
        html.append("            nameLocation: 'middle',\n");
        html.append("            nameGap: 25,\n");
        html.append("            type: 'value',\n");
        html.append("            splitLine: {\n");
        html.append("              lineStyle: {\n");
        html.append("                type: 'dashed',\n");
        html.append("                color: '#bdc3c7'\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            axisLabel: {\n");
        html.append("              color: '#7f8c8d'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          yAxis: {\n");
        html.append("            name: 'Life Expectancy (years)',\n");
        html.append("            nameLocation: 'middle',\n");
        html.append("            nameGap: 35,\n");
        html.append("            type: 'value',\n");
        html.append("            splitLine: {\n");
        html.append("              lineStyle: {\n");
        html.append("                type: 'dashed',\n");
        html.append("                color: '#bdc3c7'\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            scale: true,\n");
        html.append("            axisLabel: {\n");
        html.append("              color: '#7f8c8d'\n");
        html.append("            }\n");
        html.append("          },\n");
        html.append("          series: [\n");
        html.append("            {\n");
        html.append("              name: '1990年',\n");
        html.append("              data: data[0],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 5e2;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(120, 36, 50, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: 'rgb(251, 118, 123)'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: 'rgb(204, 46, 72)'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            },\n");
        html.append("            {\n");
        html.append("              name: '2015年',\n");
        html.append("              data: data[1],\n");
        html.append("              type: 'scatter',\n");
        html.append("              symbolSize: function (data) {\n");
        html.append("                return Math.sqrt(data[2]) / 5e2;\n");
        html.append("              },\n");
        html.append("              emphasis: {\n");
        html.append("                focus: 'series',\n");
        html.append("                label: {\n");
        html.append("                  show: true,\n");
        html.append("                  formatter: function (param) {\n");
        html.append("                    return param.data[3];\n");
        html.append("                  },\n");
        html.append("                  position: 'top',\n");
        html.append("                  color: '#2c3e50',\n");
        html.append("                  fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("              },\n");
        html.append("              itemStyle: {\n");
        html.append("                shadowBlur: 10,\n");
        html.append("                shadowColor: 'rgba(25, 100, 150, 0.5)',\n");
        html.append("                shadowOffsetY: 5,\n");
        html.append("                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [\n");
        html.append("                  {\n");
        html.append("                    offset: 0,\n");
        html.append("                    color: 'rgb(129, 227, 238)'\n");
        html.append("                  },\n");
        html.append("                  {\n");
        html.append("                    offset: 1,\n");
        html.append("                    color: 'rgb(25, 183, 207)'\n");
        html.append("                  }\n");
        html.append("                ])\n");
        html.append("              }\n");
        html.append("            }\n");
        html.append("          ],\n");
        html.append("          tooltip: {\n");
        html.append("            trigger: 'item',\n");
        html.append("            backgroundColor: 'rgba(50, 50, 50, 0.9)',\n");
        html.append("            borderColor: '#ccc',\n");
        html.append("            borderWidth: 1,\n");
        html.append("            textStyle: {\n");
        html.append("              color: '#fff'\n");
        html.append("            },\n");
        html.append("            formatter: function(params) {\n");
        html.append("              return '<strong>' + params.data[3] + '</strong><br/>' +\n");
        html.append("                     '年份: ' + params.data[4] + '<br/>' +\n");
        html.append("                     'GDP per capita: $' + params.data[0].toLocaleString() + '<br/>' +\n");
        html.append("                     'Life Expectancy: ' + params.data[1] + ' years<br/>' +\n");
        html.append("                     'Population: ' + (params.data[2]/1000000).toFixed(1) + 'M';\n");
        html.append("            }\n");
        html.append("          }\n");
        html.append("        };\n");
        html.append("        \n");
        html.append("        chart.setOption(option);\n");
        html.append("        \n");
        html.append("        // 标记渲染完成\n");
        html.append("        window.chartReady = true;\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 使用专业模板生成PDF报告（基于Apache FOP）
     */
    @GetMapping("/pdf/template")
    public ResponseEntity<byte[]> generateTemplatePdf() {
        try {
            // 创建PDF生成请求
            PdfGenerateRequest request = new PdfGenerateRequest();
            request.setTitle("数据分析专业报告");
            request.setDescription("本报告使用Apache FOP专业模板生成，展示了企业级PDF报告的标准格式和布局设计");
            request.setFileName("professional-report.pdf");
            request.setIncludeWatermark(true);
            request.setWatermarkText("机密文档");
            
            // 创建图表数据
            ChartData regionChart = new ChartData();
            regionChart.setTitle("地区洞察分析");
            regionChart.setChartType("scatter");
            regionChart.setXAxisLabel("市场饱和度");
            regionChart.setYAxisLabel("销售坪效增长率");
            
            ChartData gdpChart = new ChartData();
            gdpChart.setTitle("GDP与生命期望关系分析");
            gdpChart.setChartType("scatter");
            gdpChart.setXAxisLabel("GDP per capita (US$)");
            gdpChart.setYAxisLabel("Life Expectancy (years)");
            
            request.setCharts(Arrays.asList(regionChart, gdpChart));
            
            // 使用现有方法生成PDF，暂时替代FOP实现
            byte[] pdfBytes = generateEnterpriseStylePdf(request);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=professional-template-report.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 使用自定义企业模板生成PDF报告
     */
    @GetMapping("/pdf/enterprise-template")
    public ResponseEntity<byte[]> generateEnterpriseTemplatePdf() {
        try {
            // 创建企业级PDF生成请求
            PdfGenerateRequest request = new PdfGenerateRequest();
            request.setTitle("企业数据分析报告 2024");
            request.setDescription("本报告采用企业级模板设计，包含完整的页眉页脚、公司Logo区域、标准化布局等专业元素");
            request.setFileName("enterprise-analysis-report.pdf");
            request.setIncludeWatermark(true);
            request.setWatermarkText("RBlog 企业版");
            
            // 创建多个图表数据
            ChartData marketChart = new ChartData();
            marketChart.setTitle("市场洞察分析");
            marketChart.setChartType("scatter");
            marketChart.setXAxisLabel("市场饱和度");
            marketChart.setYAxisLabel("销售坪效增长率");
            
            ChartData trendChart = new ChartData();
            trendChart.setTitle("发展趋势对比");
            trendChart.setChartType("scatter");
            trendChart.setXAxisLabel("GDP per capita");
            trendChart.setYAxisLabel("Life Expectancy");
            
            ChartData performanceChart = new ChartData();
            performanceChart.setTitle("综合绩效评估");
            performanceChart.setChartType("bar");
            performanceChart.setXAxisLabel("产品类别");
            performanceChart.setYAxisLabel("销售额（万元）");
            
            request.setCharts(Arrays.asList(marketChart, trendChart, performanceChart));
            
            // 使用现有方法生成企业级PDF，暂时替代FOP实现
            byte[] pdfBytes = generateEnterpriseStylePdf(request);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=enterprise-template-report.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 生成企业风格的PDF（使用iText实现）
     */
    private byte[] generateEnterpriseStylePdf(PdfGenerateRequest request) throws Exception {
        // 创建PDF文档
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        
        // 设置中文字体
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 20, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font subtitleFont = new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(baseFont, 10, com.itextpdf.text.Font.NORMAL);
        
        // 企业样式：添加页眉背景色
        Rectangle pageRect = document.getPageSize();
        PdfContentByte canvas = writer.getDirectContent();
        
        // 添加页眉背景
        canvas.saveState();
        canvas.setRGBColorFill(45, 85, 135); // 企业蓝色
        canvas.rectangle(0, pageRect.getHeight() - 80, pageRect.getWidth(), 80);
        canvas.fill();
        canvas.restoreState();
        
        // 添加Logo区域 (模拟)
        Paragraph logoArea = new Paragraph("🏢 RBlog 企业版", new com.itextpdf.text.Font(baseFont, 14, com.itextpdf.text.Font.BOLD, BaseColor.WHITE));
        logoArea.setAlignment(Element.ALIGN_LEFT);
        logoArea.setSpacingAfter(30);
        document.add(logoArea);
        
        // 添加首页装饰图片
        try {
            Image coverImage = Image.getInstance("https://cdn.jsdelivr.net/gh/Raxcl/blog-resource/img/mail.jpg");
            
            // 设置图片大小 - 适应页面宽度的70%
            float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float imageScale = (pageWidth * 0.7f) / coverImage.getWidth();
            coverImage.scalePercent(imageScale * 100);
            
            // 设置图片位置和边距
            coverImage.setAlignment(Element.ALIGN_CENTER);
            coverImage.setSpacingAfter(25);
            coverImage.setBorder(Rectangle.BOX);
            coverImage.setBorderWidth(2);
            coverImage.setBorderColor(BaseColor.LIGHT_GRAY);
            
            document.add(coverImage);
        } catch (Exception imgEx) {
            // 如果图片加载失败，添加替代文本
            Paragraph fallbackText = new Paragraph("📧 企业通讯中心", new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.BOLD, BaseColor.GRAY));
            fallbackText.setAlignment(Element.ALIGN_CENTER);
            fallbackText.setSpacingAfter(25);
            document.add(fallbackText);
        }
        
        // 添加主标题
        Paragraph mainTitle = new Paragraph(request.getTitle(), titleFont);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(15);
        document.add(mainTitle);
        
        // 添加副标题
        Paragraph subtitle = new Paragraph("Professional Analytics Report", new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(25);
        document.add(subtitle);
        
        // 添加描述
        Paragraph description = new Paragraph(request.getDescription(), contentFont);
        description.setAlignment(Element.ALIGN_JUSTIFIED);
        description.setSpacingAfter(20);
        document.add(description);
        
        // 添加生成信息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Paragraph generateInfo = new Paragraph("报告生成时间: " + sdf.format(new Date()), smallFont);
        generateInfo.setAlignment(Element.ALIGN_RIGHT);
        generateInfo.setSpacingAfter(30);
        document.add(generateInfo);
        
        // 添加图表部分
        if (request.getCharts() != null && !request.getCharts().isEmpty()) {
            int chartIndex = 1;
            for (ChartData chartData : request.getCharts()) {
                // 图表章节标题
                Paragraph sectionTitle = new Paragraph(
                    String.format("%02d %s", chartIndex, chartData.getTitle()), 
                    subtitleFont
                );
                sectionTitle.setSpacingBefore(30);
                sectionTitle.setSpacingAfter(15);
                document.add(sectionTitle);
                
                // 根据图表类型生成不同的图表
                byte[] chartImageBytes;
                switch (chartData.getChartType()) {
                    case "scatter":
                        if (chartIndex == 1) {
                            chartImageBytes = generateScatterChart();
                        } else {
                            chartImageBytes = generateGdpLifeExpectancyChart();
                        }
                        break;
                    case "bar":
                        chartImageBytes = generateBarChart();
                        break;
                    default:
                        chartImageBytes = generateBarChart();
                }
                
                Image chartImage = Image.getInstance(chartImageBytes);
                
                // 调整图片大小
                float scalePercent = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / chartImage.getWidth();
                if (scalePercent < 1.0f) {
                    chartImage.scalePercent(scalePercent * 100);
                }
                
                chartImage.setAlignment(Element.ALIGN_CENTER);
                chartImage.setSpacingAfter(20);
                document.add(chartImage);
                
                chartIndex++;
                
                // 如果不是最后一个图表，添加分页
                if (chartIndex <= request.getCharts().size()) {
                    document.newPage();
                }
            }
        }
        
        // 添加水印
        if (request.isIncludeWatermark()) {
            canvas = writer.getDirectContentUnder();
            canvas.saveState();
            
            // 设置水印样式
            canvas.setGState(new PdfGState() {{ setFillOpacity(0.1f); }});
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 50);
            canvas.setColorFill(BaseColor.GRAY);
            
            // 旋转并添加水印文字
            canvas.showTextAligned(Element.ALIGN_CENTER, 
                request.getWatermarkText() != null ? request.getWatermarkText() : "企业机密",
                pageRect.getWidth() / 2, pageRect.getHeight() / 2, 45);
                
            canvas.endText();
            canvas.restoreState();
        }
        
        // 添加页脚
        Paragraph footer = new Paragraph("© 2024 RBlog Enterprise. All rights reserved.", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
        
        document.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * 使用HTML模板生成品牌洞察PDF演示
     */
    @GetMapping("/pdf/brand-insight-template")
    public ResponseEntity<byte[]> generateBrandInsightTemplatePdf() {
        try {
            // 创建演示数据
            BrandInsightData data = createDemoBrandInsightData();
            
            // 使用模板渲染服务生成HTML
            String htmlContent;
            if (templateRenderService != null) {
                htmlContent = templateRenderService.renderBrandInsightTemplate(data);
            } else {
                // 备选方案：使用简单的HTML模板
                htmlContent = generateSimpleBrandInsightHtml(data);
            }
            
            // TODO: 集成HTML转PDF服务（需要添加Playwright依赖）
            // 暂时使用现有的PDF生成方式作为演示
            byte[] pdfBytes = generateBrandInsightPdfWithiText(data);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=brand-insight-template.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 创建演示用的品牌洞察数据
     */
    private BrandInsightData createDemoBrandInsightData() {
        BrandInsightData data = new BrandInsightData();
        data.setMerchantName("RBlog 企业版");
        data.setLogoUrl("https://cdn.jsdelivr.net/gh/Raxcl/blog-resource/img/mail.jpg");
        data.setBackgroundImageUrl("https://cdn.jsdelivr.net/gh/Raxcl/blog-resource/img/mail.jpg");
        data.setReportDate("2024年1月");
        data.setBrandValue("1.2亿元");
        data.setMarketShare(15.8);
        
        // 创建性能指标演示数据
        java.util.List<BrandInsightData.PerformanceMetric> metrics = java.util.Arrays.asList(
            new BrandInsightData.PerformanceMetric("品牌认知度", "78.5%", "+5.2%", "↗"),
            new BrandInsightData.PerformanceMetric("市场占有率", "15.8%", "+2.1%", "↗"),
            new BrandInsightData.PerformanceMetric("客户满意度", "92.3%", "+1.8%", "↗"),
            new BrandInsightData.PerformanceMetric("销售增长率", "23.7%", "+8.9%", "↗")
        );
        data.setPerformanceMetrics(metrics);
        
        // 图表数据
        data.setMarketChartLabels(java.util.Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月"));
        data.setMarketChartData(java.util.Arrays.asList(120.5, 135.2, 148.7, 162.1, 175.8, 189.3));
        
        return data;
    }
    
    /**
     * 生成简单的品牌洞察HTML（备选方案）
     */
    private String generateSimpleBrandInsightHtml(BrandInsightData data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>").append(data.getMerchantName()).append(" 品牌洞察报告</title>\n");
        html.append("    <style>\n");
        html.append("        @page { size: A4; margin: 20mm; }\n");
        html.append("        body { font-family: 'Microsoft YaHei', 'SimSun', Arial, sans-serif; }\n");
        html.append("        .cover { text-align: center; padding: 50px 0; }\n");
        html.append("        .title { font-size: 28px; font-weight: bold; margin-bottom: 20px; }\n");
        html.append("        .subtitle { font-size: 18px; color: #666; margin-bottom: 30px; }\n");
        html.append("        .metrics-table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        html.append("        .metrics-table th, .metrics-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
        html.append("        .metrics-table th { background-color: #f2f2f2; font-weight: bold; }\n");
        html.append("        .page-break { page-break-before: always; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 封面页
        html.append("    <div class=\"cover\">\n");
        html.append("        <div class=\"title\">").append(data.getMerchantName()).append("</div>\n");
        html.append("        <div class=\"subtitle\">品牌洞察报告</div>\n");
        html.append("        <p>报告期间：").append(data.getReportDate()).append("</p>\n");
        html.append("    </div>\n");
        
        // 品牌概览页
        html.append("    <div class=\"page-break\">\n");
        html.append("        <h1>品牌概览</h1>\n");
        html.append("        <p><strong>品牌价值：</strong>").append(data.getBrandValue()).append("</p>\n");
        html.append("        <p><strong>市场份额：</strong>").append(data.getMarketShare()).append("%</p>\n");
        
        // 性能指标表格
        if (data.getPerformanceMetrics() != null && !data.getPerformanceMetrics().isEmpty()) {
            html.append("        <h2>关键绩效指标</h2>\n");
            html.append("        <table class=\"metrics-table\">\n");
            html.append("            <thead>\n");
            html.append("                <tr><th>指标</th><th>当前值</th><th>上期对比</th><th>趋势</th></tr>\n");
            html.append("            </thead>\n");
            html.append("            <tbody>\n");
            
            for (BrandInsightData.PerformanceMetric metric : data.getPerformanceMetrics()) {
                html.append("                <tr>\n");
                html.append("                    <td>").append(metric.getName()).append("</td>\n");
                html.append("                    <td>").append(metric.getCurrentValue()).append("</td>\n");
                html.append("                    <td>").append(metric.getCompareValue()).append("</td>\n");
                html.append("                    <td>").append(metric.getTrend()).append("</td>\n");
                html.append("                </tr>\n");
            }
            
            html.append("            </tbody>\n");
            html.append("        </table>\n");
        }
        
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 使用iText生成品牌洞察PDF（作为HTML模板的备选方案）
     */
    private byte[] generateBrandInsightPdfWithiText(BrandInsightData data) throws Exception {
        // 创建PDF文档
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        
        // 设置中文字体
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 20, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font subtitleFont = new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);
        
        // 添加背景图片（封面）
        try {
            Image backgroundImage = Image.getInstance(data.getBackgroundImageUrl());
            backgroundImage.setAbsolutePosition(0, 0);
            backgroundImage.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            backgroundImage.setTransparency(new int[]{0, 128}); // 设置透明度
            document.add(backgroundImage);
        } catch (Exception e) {
            // 背景图加载失败时的处理
        }
        
        // 主标题
        Paragraph mainTitle = new Paragraph(data.getMerchantName(), titleFont);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(15);
        document.add(mainTitle);
        
        // 副标题
        Paragraph subtitle = new Paragraph("品牌洞察报告", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(25);
        document.add(subtitle);
        
        // 报告日期
        Paragraph reportDate = new Paragraph("报告期间: " + data.getReportDate(), contentFont);
        reportDate.setAlignment(Element.ALIGN_CENTER);
        reportDate.setSpacingAfter(40);
        document.add(reportDate);
        
        // 新页面
        document.newPage();
        
        // 品牌概览
        Paragraph overviewTitle = new Paragraph("品牌概览", subtitleFont);
        overviewTitle.setSpacingAfter(20);
        document.add(overviewTitle);
        
        Paragraph brandValue = new Paragraph("品牌价值: " + data.getBrandValue(), contentFont);
        brandValue.setSpacingAfter(10);
        document.add(brandValue);
        
        Paragraph marketShare = new Paragraph("市场份额: " + data.getMarketShare() + "%", contentFont);
        marketShare.setSpacingAfter(30);
        document.add(marketShare);
        
        // 性能指标表格
        if (data.getPerformanceMetrics() != null && !data.getPerformanceMetrics().isEmpty()) {
            Paragraph metricsTitle = new Paragraph("关键绩效指标", subtitleFont);
            metricsTitle.setSpacingAfter(15);
            document.add(metricsTitle);
            
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            
            // 表头
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase("指标", contentFont)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase("当前值", contentFont)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase("上期对比", contentFont)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase("趋势", contentFont)));
            
            // 数据行
            for (BrandInsightData.PerformanceMetric metric : data.getPerformanceMetrics()) {
                table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase(metric.getName(), contentFont)));
                table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase(metric.getCurrentValue(), contentFont)));
                table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase(metric.getCompareValue(), contentFont)));
                table.addCell(new com.itextpdf.text.pdf.PdfPCell(new Phrase(metric.getTrend(), contentFont)));
            }
            
            document.add(table);
        }
        
        document.close();
        return outputStream.toByteArray();
    }
    
    /**
     * HTML转PDF测试端点
     */
    @GetMapping("/pdf/html-test")
    public ResponseEntity<byte[]> generateHtmlTestPdf() {
        try {
            byte[] pdfBytes;
            
            if (htmlToPdfService != null) {
                pdfBytes = htmlToPdfService.generateTestPdf();
            } else {
                // 备选方案：使用iText生成简单PDF
                pdfBytes = generateSimpleTestPdf();
            }
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=html-test.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 生成简单测试PDF（备选方案）
     */
    private byte[] generateSimpleTestPdf() throws Exception {
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
        Paragraph title = new Paragraph("HTML转PDF功能测试", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // 添加内容
        Paragraph content = new Paragraph("HTML模板PDF生成系统测试成功！", contentFont);
        content.setSpacingAfter(15);
        document.add(content);
        
        // 添加时间戳
        Paragraph timestamp = new Paragraph("生成时间: " + 
            java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), contentFont);
        document.add(timestamp);
        
        document.close();
        return outputStream.toByteArray();
    }
} 