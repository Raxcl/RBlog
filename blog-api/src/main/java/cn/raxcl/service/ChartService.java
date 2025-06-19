package cn.raxcl.service;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.ChartDataPoint;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 图表服务
 * @author raxcl
 */
@Service
public class ChartService {
    
    /**
     * 生成图表图片
     */
    public byte[] generateChart(ChartData chartData) throws IOException {
        JFreeChart chart = createChart(chartData);
        setChartStyle(chart);
        
        BufferedImage bufferedImage = chart.createBufferedImage(800, 600);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(outputStream, bufferedImage);
        
        return outputStream.toByteArray();
    }
    
    /**
     * 创建图表
     */
    private JFreeChart createChart(ChartData chartData) {
        switch (chartData.getChartType().toLowerCase()) {
            case "bar":
                return createBarChart(chartData);
            case "line":
                return createLineChart(chartData);
            case "pie":
                return createPieChart(chartData);
            default:
                throw new IllegalArgumentException("不支持的图表类型: " + chartData.getChartType());
        }
    }
    
    /**
     * 创建柱状图
     */
    private JFreeChart createBarChart(ChartData chartData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ChartDataPoint point : chartData.getDataPoints()) {
            dataset.addValue(point.getValue(), "数据系列", point.getLabel());
        }

        JFreeChart chart = ChartFactory.createBarChart(
            chartData.getTitle(),
            chartData.getXAxisLabel(),
            chartData.getYAxisLabel(),
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);
        
        return chart;
    }
    
    /**
     * 创建折线图
     */
    private JFreeChart createLineChart(ChartData chartData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ChartDataPoint point : chartData.getDataPoints()) {
            dataset.addValue(point.getValue(), "数据系列", point.getLabel());
        }

        JFreeChart chart = ChartFactory.createLineChart(
            chartData.getTitle(),
            chartData.getXAxisLabel(),
            chartData.getYAxisLabel(),
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        return chart;
    }
    
    /**
     * 创建饼图
     */
    private JFreeChart createPieChart(ChartData chartData) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (ChartDataPoint point : chartData.getDataPoints()) {
            dataset.setValue(point.getLabel(), point.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
            chartData.getTitle(),
            dataset,
            true,
            true,
            false
        );
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
    /**
     * 设置图表样式
     */
    private void setChartStyle(JFreeChart chart) {
        // 设置中文字体
        Font font = new Font("宋体", Font.PLAIN, 12);
        Font titleFont = new Font("宋体", Font.BOLD, 16);
        
        chart.getTitle().setFont(titleFont);
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(font);
        }
        
        // 对于分类图表，设置坐标轴字体
        if (chart.getCategoryPlot() != null) {
            chart.getCategoryPlot().getDomainAxis().setLabelFont(font);
            chart.getCategoryPlot().getDomainAxis().setTickLabelFont(font);
            chart.getCategoryPlot().getRangeAxis().setLabelFont(font);
            chart.getCategoryPlot().getRangeAxis().setTickLabelFont(font);
        } else if (chart.getXYPlot() != null) {
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.getDomainAxis().setLabelFont(font);
            plot.getDomainAxis().setTickLabelFont(font);
            plot.getRangeAxis().setLabelFont(font);
            plot.getRangeAxis().setTickLabelFont(font);
        } else if (chart.getPlot() instanceof PiePlot) {
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelFont(font);
        }
    }
} 