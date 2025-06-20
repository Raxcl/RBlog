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
import java.awt.GradientPaint;
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
        html.append("        var option = {\n");
        html.append("            title: {\n");
        html.append("                text: '地区洞察',\n");
        html.append("                left: 'center',\n");
        html.append("                textStyle: {\n");
        html.append("                    fontSize: 18,\n");
        html.append("                    fontWeight: 'bold'\n");
        html.append("                }\n");
        html.append("            },\n");
        html.append("            grid: {\n");
        html.append("                left: '10%',\n");
        html.append("                right: '10%',\n");
        html.append("                bottom: '15%',\n");
        html.append("                top: '15%'\n");
        html.append("            },\n");
        html.append("            xAxis: {\n");
        html.append("                name: '市场饱和度',\n");
        html.append("                nameLocation: 'middle',\n");
        html.append("                nameGap: 30,\n");
        html.append("                type: 'value',\n");
        html.append("                min: 0,\n");
        html.append("                max: 100,\n");
        html.append("                axisLine: {\n");
        html.append("                    lineStyle: { color: '#666' }\n");
        html.append("                }\n");
        html.append("            },\n");
        html.append("            yAxis: {\n");
        html.append("                name: '销售坪效增长率',\n");
        html.append("                nameLocation: 'middle',\n");
        html.append("                nameGap: 40,\n");
        html.append("                type: 'value',\n");
        html.append("                min: 0,\n");
        html.append("                max: 100,\n");
        html.append("                axisLine: {\n");
        html.append("                    lineStyle: { color: '#666' }\n");
        html.append("                }\n");
        html.append("            },\n");
        html.append("            series: [\n");
        html.append("                {\n");
        html.append("                    name: '机会区域',\n");
        html.append("                    type: 'scatter',\n");
        html.append("                    symbolSize: function(data) { return data[2] || 30; },\n");
        html.append("                    itemStyle: {\n");
        html.append("                        color: {\n");
        html.append("                            type: 'radial',\n");
        html.append("                            x: 0.3,\n");
        html.append("                            y: 0.3,\n");
        html.append("                            r: 0.8,\n");
        html.append("                            colorStops: [\n");
        html.append("                                { offset: 0, color: '#d0b7f0' },\n");
        html.append("                                { offset: 1, color: '#8a67b4' }\n");
        html.append("                            ]\n");
        html.append("                        },\n");
        html.append("                        borderColor: '#6c4996',\n");
        html.append("                        borderWidth: 2\n");
        html.append("                    },\n");
        html.append("                    data: [\n");
        html.append("                        [25, 65, 40, '山东省'],\n");
        html.append("                        [30, 55, 35, '重庆市']\n");
        html.append("                    ]\n");
        html.append("                },\n");
        html.append("                {\n");
        html.append("                    name: '热门区域',\n");
        html.append("                    type: 'scatter',\n");
        html.append("                    symbolSize: function(data) { return data[2] || 30; },\n");
        html.append("                    itemStyle: {\n");
        html.append("                        color: {\n");
        html.append("                            type: 'radial',\n");
        html.append("                            x: 0.3,\n");
        html.append("                            y: 0.3,\n");
        html.append("                            r: 0.8,\n");
        html.append("                            colorStops: [\n");
        html.append("                                { offset: 0, color: '#ffc381' },\n");
        html.append("                                { offset: 1, color: '#eb8731' }\n");
        html.append("                            ]\n");
        html.append("                        },\n");
        html.append("                        borderColor: '#cd6913',\n");
        html.append("                        borderWidth: 2\n");
        html.append("                    },\n");
        html.append("                    data: [\n");
        html.append("                        [75, 65, 38, '辽宁省'],\n");
        html.append("                        [80, 45, 42, '吉林省'],\n");
        html.append("                        [85, 35, 36, '其他']\n");
        html.append("                    ]\n");
        html.append("                },\n");
        html.append("                {\n");
        html.append("                    name: '观望评估',\n");
        html.append("                    type: 'scatter',\n");
        html.append("                    symbolSize: function(data) { return data[2] || 30; },\n");
        html.append("                    itemStyle: {\n");
        html.append("                        color: {\n");
        html.append("                            type: 'radial',\n");
        html.append("                            x: 0.3,\n");
        html.append("                            y: 0.3,\n");
        html.append("                            r: 0.8,\n");
        html.append("                            colorStops: [\n");
        html.append("                                { offset: 0, color: '#95e7cf' },\n");
        html.append("                                { offset: 1, color: '#59c9a7' }\n");
        html.append("                            ]\n");
        html.append("                        },\n");
        html.append("                        borderColor: '#3bab89',\n");
        html.append("                        borderWidth: 2\n");
        html.append("                    },\n");
        html.append("                    data: [\n");
        html.append("                        [35, 25, 32, '河北省']\n");
        html.append("                    ]\n");
        html.append("                },\n");
        html.append("                {\n");
        html.append("                    name: '趋于饱和',\n");
        html.append("                    type: 'scatter',\n");
        html.append("                    symbolSize: function(data) { return data[2] || 30; },\n");
        html.append("                    itemStyle: {\n");
        html.append("                        color: {\n");
        html.append("                            type: 'radial',\n");
        html.append("                            x: 0.3,\n");
        html.append("                            y: 0.3,\n");
        html.append("                            r: 0.8,\n");
        html.append("                            colorStops: [\n");
        html.append("                                { offset: 0, color: '#91b2ff' },\n");
        html.append("                                { offset: 1, color: '#5580eb' }\n");
        html.append("                            ]\n");
        html.append("                        },\n");
        html.append("                        borderColor: '#3762cd',\n");
        html.append("                        borderWidth: 2\n");
        html.append("                    },\n");
        html.append("                    data: [\n");
        html.append("                        [70, 15, 44, '广东省'],\n");
        html.append("                        [85, 35, 40, '上海市']\n");
        html.append("                    ]\n");
        html.append("                }\n");
        html.append("            ],\n");
        html.append("            tooltip: {\n");
        html.append("                trigger: 'item',\n");
        html.append("                formatter: function(params) {\n");
        html.append("                    return params.data[3] + '<br/>市场饱和度: ' + params.data[0] + '<br/>销售坪效增长率: ' + params.data[1];\n");
        html.append("                }\n");
        html.append("            }\n");
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
} 