package cn.raxcl.service;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.ChartDataPoint;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 基于ECharts的现代化图表生成服务
 * 需要Node.js环境和puppeteer
 * 
 * @author raxcl
 * @date 2024-01-01
 */
@Service
public class EChartsService {
    
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String ECHARTS_SCRIPT_TEMPLATE = "const puppeteer = require('puppeteer');\n" +
        "const fs = require('fs');\n" +
        "\n" +
        "(async () => {\n" +
        "  const browser = await puppeteer.launch({\n" +
        "    headless: 'new',\n" +
        "    args: ['--no-sandbox', '--disable-setuid-sandbox']\n" +
        "  });\n" +
        "  \n" +
        "  const page = await browser.newPage();\n" +
        "  await page.setViewport({ width: 800, height: 600 });\n" +
        "  \n" +
        "  const htmlContent = `\n" +
        "    <!DOCTYPE html>\n" +
        "    <html>\n" +
        "    <head>\n" +
        "        <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n" +
        "    </head>\n" +
        "    <body>\n" +
        "        <div id=\"chart\" style=\"width: 800px; height: 600px;\"></div>\n" +
        "        <script>\n" +
        "            const chart = echarts.init(document.getElementById('chart'));\n" +
        "            const option = %s;\n" +
        "            chart.setOption(option);\n" +
        "            \n" +
        "            // 等待图表渲染完成\n" +
        "            setTimeout(() => {\n" +
        "                const base64 = chart.getDataURL();\n" +
        "                window.chartData = base64;\n" +
        "            }, 1000);\n" +
        "        </script>\n" +
        "    </body>\n" +
        "    </html>\n" +
        "  `;\n" +
        "  \n" +
        "  await page.setContent(htmlContent);\n" +
        "  \n" +
        "  // 等待图表渲染完成\n" +
        "  await page.waitForTimeout(2000);\n" +
        "  \n" +
        "  // 获取图表数据\n" +
        "  const chartData = await page.evaluate(() => window.chartData);\n" +
        "  \n" +
        "  // 保存到文件\n" +
        "  const base64Data = chartData.replace(/^data:image\\/png;base64,/, '');\n" +
        "  fs.writeFileSync('%s', base64Data, 'base64');\n" +
        "  \n" +
        "  await browser.close();\n" +
        "})();";
    
    /**
     * 生成图表图片
     */
    public byte[] generateChart(ChartData chartData) throws IOException, InterruptedException {
        // 检查Node.js环境
        if (!isNodeJsAvailable()) {
            throw new RuntimeException("Node.js环境不可用，请安装Node.js和puppeteer");
        }
        
        // 生成ECharts配置
        String echartsOption = generateEChartsOption(chartData);
        
        // 创建临时文件
        String tempId = String.valueOf(System.currentTimeMillis());
        String scriptPath = TEMP_DIR + "/echarts_script_" + tempId + ".js";
        String imagePath = TEMP_DIR + "/chart_" + tempId + ".png";
        
        try {
            // 生成JavaScript脚本
            String script = String.format(ECHARTS_SCRIPT_TEMPLATE, echartsOption, imagePath);
            Files.write(Paths.get(scriptPath), script.getBytes());
            
            // 执行Node.js脚本
            ProcessBuilder processBuilder = new ProcessBuilder("node", scriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("ECharts生成失败: " + output.toString());
            }
            
            // 读取生成的图片
            Path imageFilePath = Paths.get(imagePath);
            if (!Files.exists(imageFilePath)) {
                throw new RuntimeException("图片文件生成失败");
            }
            
            return Files.readAllBytes(imageFilePath);
            
        } finally {
            // 清理临时文件
            try {
                Files.deleteIfExists(Paths.get(scriptPath));
                Files.deleteIfExists(Paths.get(imagePath));
            } catch (IOException e) {
                // 忽略清理错误
            }
        }
    }
    
    /**
     * 生成ECharts配置选项
     */
    private String generateEChartsOption(ChartData chartData) {
        StringBuilder option = new StringBuilder();
        option.append("{\n");
        
        // 标题
        if (chartData.getTitle() != null && !chartData.getTitle().isEmpty()) {
            option.append("  title: {\n");
            option.append("    text: '").append(escapeJson(chartData.getTitle())).append("',\n");
            option.append("    left: 'center',\n");
            option.append("    textStyle: { fontSize: 16, fontWeight: 'bold' }\n");
            option.append("  },\n");
        }
        
        // 工具提示
        option.append("  tooltip: { trigger: 'item' },\n");
        
        // 图例
        option.append("  legend: { top: '10%' },\n");
        
        // 根据图表类型生成配置
        switch (chartData.getChartType().toLowerCase()) {
            case "bar":
                generateBarChartOption(option, chartData);
                break;
            case "line":
                generateLineChartOption(option, chartData);
                break;
            case "pie":
                generatePieChartOption(option, chartData);
                break;
            default:
                throw new IllegalArgumentException("不支持的图表类型: " + chartData.getChartType());
        }
        
        option.append("}");
        return option.toString();
    }
    
    /**
     * 生成柱状图配置
     */
    private void generateBarChartOption(StringBuilder option, ChartData chartData) {
        // X轴
        option.append("  xAxis: {\n");
        option.append("    type: 'category',\n");
        option.append("    name: '").append(escapeJson(chartData.getXAxisLabel())).append("',\n");
        option.append("    data: [");
        for (int i = 0; i < chartData.getDataPoints().size(); i++) {
            if (i > 0) option.append(", ");
            option.append("'").append(escapeJson(chartData.getDataPoints().get(i).getLabel())).append("'");
        }
        option.append("]\n");
        option.append("  },\n");
        
        // Y轴
        option.append("  yAxis: {\n");
        option.append("    type: 'value',\n");
        option.append("    name: '").append(escapeJson(chartData.getYAxisLabel())).append("'\n");
        option.append("  },\n");
        
        // 数据系列
        option.append("  series: [{\n");
        option.append("    type: 'bar',\n");
        option.append("    data: [");
        for (int i = 0; i < chartData.getDataPoints().size(); i++) {
            if (i > 0) option.append(", ");
            option.append(chartData.getDataPoints().get(i).getValue());
        }
        option.append("],\n");
        option.append("    itemStyle: {\n");
        option.append("      color: '#5470c6'\n");
        option.append("    }\n");
        option.append("  }]\n");
    }
    
    /**
     * 生成折线图配置
     */
    private void generateLineChartOption(StringBuilder option, ChartData chartData) {
        // X轴
        option.append("  xAxis: {\n");
        option.append("    type: 'category',\n");
        option.append("    name: '").append(escapeJson(chartData.getXAxisLabel())).append("',\n");
        option.append("    data: [");
        for (int i = 0; i < chartData.getDataPoints().size(); i++) {
            if (i > 0) option.append(", ");
            option.append("'").append(escapeJson(chartData.getDataPoints().get(i).getLabel())).append("'");
        }
        option.append("]\n");
        option.append("  },\n");
        
        // Y轴
        option.append("  yAxis: {\n");
        option.append("    type: 'value',\n");
        option.append("    name: '").append(escapeJson(chartData.getYAxisLabel())).append("'\n");
        option.append("  },\n");
        
        // 数据系列
        option.append("  series: [{\n");
        option.append("    type: 'line',\n");
        option.append("    data: [");
        for (int i = 0; i < chartData.getDataPoints().size(); i++) {
            if (i > 0) option.append(", ");
            option.append(chartData.getDataPoints().get(i).getValue());
        }
        option.append("],\n");
        option.append("    smooth: true,\n");
        option.append("    lineStyle: { color: '#91cc75' },\n");
        option.append("    itemStyle: { color: '#91cc75' }\n");
        option.append("  }]\n");
    }
    
    /**
     * 生成饼图配置
     */
    private void generatePieChartOption(StringBuilder option, ChartData chartData) {
        option.append("  series: [{\n");
        option.append("    type: 'pie',\n");
        option.append("    radius: '60%',\n");
        option.append("    center: ['50%', '50%'],\n");
        option.append("    data: [");
        
        for (int i = 0; i < chartData.getDataPoints().size(); i++) {
            ChartDataPoint point = chartData.getDataPoints().get(i);
            if (i > 0) option.append(", ");
            option.append("{\n");
            option.append("      name: '").append(escapeJson(point.getLabel())).append("',\n");
            option.append("      value: ").append(point.getValue()).append("\n");
            option.append("    }");
        }
        
        option.append("],\n");
        option.append("    emphasis: {\n");
        option.append("      itemStyle: {\n");
        option.append("        shadowBlur: 10,\n");
        option.append("        shadowOffsetX: 0,\n");
        option.append("        shadowColor: 'rgba(0, 0, 0, 0.5)'\n");
        option.append("      }\n");
        option.append("    }\n");
        option.append("  }]\n");
    }
    
    /**
     * 检查Node.js是否可用
     */
    private boolean isNodeJsAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("node", "--version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * JSON字符串转义
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
} 