# PDF报告生成模块使用指南

## 概述

PDF报告生成模块是一个基于iText和JFreeChart的强大工具，能够生成包含各种图表的专业PDF报告。模块支持柱状图、折线图和饼图，并提供中文字体支持、水印功能等特性。

## 模块结构

```
src/main/java/cn/raxcl/
├── dto/
│   ├── ChartData.java              # 图表数据传输对象
│   ├── ChartDataPoint.java         # 图表数据点
│   └── PdfGenerateRequest.java     # PDF生成请求
├── service/
│   ├── ChartService.java           # 图表生成服务
│   └── PdfReportService.java       # PDF报告生成服务
├── controller/
│   ├── PdfReportController.java    # PDF报告API控制器
│   └── PdfDemoController.java      # PDF演示控制器
└── test/
    └── PdfModuleTest.java          # 模块测试类
```

## 核心依赖

项目已配置以下核心依赖：

```xml
<!-- PDF生成 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>

<!-- 中文字体支持 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-asian</artifactId>
    <version>5.2.0</version>
</dependency>

<!-- 图表生成 -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.3</version>
</dependency>
```

## 快速开始

### 1. 基本用法

```java
import cn.raxcl.dto.*;
import cn.raxcl.service.*;

// 创建服务实例
ChartService chartService = new ChartService();
PdfReportService pdfReportService = new PdfReportService(chartService);

// 创建PDF生成请求
PdfGenerateRequest request = new PdfGenerateRequest();
request.setTitle("销售报告");
request.setDescription("2024年第一季度销售数据分析报告");
request.setFileName("sales-report.pdf");
request.setIncludeWatermark(true);
request.setWatermarkText("机密文档");

// 创建图表数据
ChartData chartData = new ChartData();
chartData.setTitle("销售额对比");
chartData.setChartType("bar");
chartData.setXAxisLabel("产品");
chartData.setYAxisLabel("销售额(万元)");
chartData.setDataPoints(Arrays.asList(
    new ChartDataPoint("产品A", 120.0),
    new ChartDataPoint("产品B", 85.0),
    new ChartDataPoint("产品C", 95.0)
));

request.setCharts(Arrays.asList(chartData));

// 生成PDF
byte[] pdfBytes = pdfReportService.generatePdfReport(request);

// 保存文件
Files.write(Paths.get("report.pdf"), pdfBytes);
```

### 2. Spring Boot集成

将控制器注册为Spring Bean：

```java
@RestController
@RequestMapping("/api/pdf-report")
public class PdfReportController {
    
    private final PdfReportService pdfReportService;
    
    public PdfReportController() {
        this.pdfReportService = new PdfReportService();
    }
    
    // ... 其他方法
}
```

## API接口

### 1. 生成PDF报告

**接口地址：** `POST /api/pdf-report/generate`

**请求体：**
```json
{
    "title": "销售报告",
    "description": "2024年第一季度销售数据分析",
    "fileName": "sales-report.pdf",
    "includeWatermark": true,
    "watermarkText": "机密文档",
    "charts": [
        {
            "title": "销售额对比",
            "chartType": "bar",
            "xAxisLabel": "产品",
            "yAxisLabel": "销售额(万元)",
            "dataPoints": [
                {"label": "产品A", "value": 120.0},
                {"label": "产品B", "value": 85.0},
                {"label": "产品C", "value": 95.0}
            ]
        }
    ]
}
```

**响应：** PDF文件二进制流

### 2. 在线预览PDF

**接口地址：** `POST /api/pdf-report/preview`

**请求体：** 与生成接口相同

**响应：** PDF文件，Content-Disposition为inline

### 3. 生成示例PDF

**接口地址：** `GET /api/pdf-report/sample`

**响应：** 包含示例数据的PDF文件

### 4. 演示接口

**接口地址：** `GET /api/demo/generate-pdf`

**响应：** 简单的演示PDF文件

## 支持的图表类型

### 1. 柱状图 (bar)
适用于分类数据的比较，如销售额对比、用户数量统计等。

```json
{
    "title": "销售额对比",
    "chartType": "bar",
    "xAxisLabel": "产品",
    "yAxisLabel": "销售额(万元)",
    "dataPoints": [
        {"label": "产品A", "value": 120.0},
        {"label": "产品B", "value": 85.0}
    ]
}
```

### 2. 折线图 (line)
适用于趋势数据的展示，如用户增长、销售趋势等。

```json
{
    "title": "用户增长趋势",
    "chartType": "line",
    "xAxisLabel": "月份",
    "yAxisLabel": "用户数(万)",
    "dataPoints": [
        {"label": "1月", "value": 10.0},
        {"label": "2月", "value": 15.0},
        {"label": "3月", "value": 22.0}
    ]
}
```

### 3. 饼图 (pie)
适用于比例数据的展示，如市场份额、流量来源分布等。

```json
{
    "title": "流量来源分布",
    "chartType": "pie",
    "dataPoints": [
        {"label": "搜索引擎", "value": 45.0},
        {"label": "直接访问", "value": 30.0},
        {"label": "社交媒体", "value": 25.0}
    ]
}
```

## 测试和验证

### 运行测试类

```bash
# 编译并运行测试
cd /path/to/project
mvn compile exec:java -Dexec.mainClass="cn.raxcl.test.PdfModuleTest"
```

### 使用curl测试API

```bash
# 生成示例PDF
curl -X GET http://localhost:8080/api/pdf-report/sample \
  -H "Accept: application/pdf" \
  -o sample-report.pdf

# 生成自定义PDF
curl -X POST http://localhost:8080/api/pdf-report/generate \
  -H "Content-Type: application/json" \
  -H "Accept: application/pdf" \
  -d '{
    "title": "测试报告",
    "description": "这是一个测试报告",
    "fileName": "test.pdf",
    "includeWatermark": true,
    "watermarkText": "测试水印",
    "charts": [
      {
        "title": "销售数据",
        "chartType": "bar",
        "xAxisLabel": "产品",
        "yAxisLabel": "销售额",
        "dataPoints": [
          {"label": "产品A", "value": 100},
          {"label": "产品B", "value": 150}
        ]
      }
    ]
  }' \
  -o custom-report.pdf
```

## 配置说明

### 字体设置
模块使用STSong-Light字体支持中文显示。如需使用其他字体，可修改PdfReportService中的字体配置：

```java
BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
```

### 图表样式
可在ChartService中自定义图表样式：

```java
private void setChartStyle(JFreeChart chart) {
    chart.getTitle().setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
    // 自定义其他样式...
}
```

### 页面布局
可在PdfReportService中调整页面布局：

```java
Document document = new Document(PageSize.A4, 50, 50, 50, 50); // 边距设置
```

## 注意事项

1. **字体文件**：确保系统中有STSong-Light字体，或使用其他支持中文的字体
2. **内存使用**：处理大量图表时注意内存使用情况
3. **文件大小**：图表质量设置会影响最终PDF文件大小
4. **线程安全**：服务类是线程安全的，可在多线程环境中使用
5. **异常处理**：生产环境中需要完善异常处理和日志记录

## 扩展功能

### 添加新图表类型
在ChartService的createChart方法中添加新的图表类型：

```java
case "scatter":
    return createScatterChart(chartData);
```

### 自定义PDF样式
继承PdfReportService并重写相关方法：

```java
public class CustomPdfReportService extends PdfReportService {
    @Override
    protected void addChart(Document document, ChartData chartData, Font titleFont) {
        // 自定义图表添加逻辑
    }
}
```

### 添加更多元素
扩展PdfGenerateRequest以支持更多PDF元素：

```java
public class ExtendedPdfRequest extends PdfGenerateRequest {
    private List<TableData> tables;
    private List<ImageData> images;
    // ...
}
```

## 故障排除

### 常见问题

1. **编译错误**：确保所有依赖已正确添加到pom.xml
2. **字体问题**：检查字体文件是否存在，或使用系统默认字体
3. **图表不显示**：检查ChartData数据是否正确设置
4. **PDF损坏**：确保PDF生成过程中没有异常抛出

### 调试方法

1. 启用详细日志记录
2. 单独测试图表生成功能
3. 检查PDF二进制数据是否完整
4. 使用PDF查看器验证生成的文件

## 版本历史

- v1.0.0: 初始版本，支持基本PDF生成和三种图表类型
- 计划v1.1.0: 添加表格支持、更多图表类型、模板功能

## 联系方式

如有问题或建议，请联系开发团队或提交Issue。 