# PDF报告生成模块

## 📋 项目概述

已成功为您的博客API项目创建了完整的PDF报告生成模块。该模块基于iText和JFreeChart库，支持生成包含多种图表类型的专业PDF报告。

## 🏗️ 已创建的文件结构

```
blog-api/
├── src/main/java/cn/raxcl/
│   ├── dto/                           # 数据传输对象
│   │   ├── ChartDataPoint.java        # 图表数据点
│   │   ├── ChartData.java             # 图表数据
│   │   └── PdfGenerateRequest.java    # PDF生成请求
│   ├── service/                       # 业务服务
│   │   ├── ChartService.java          # 图表生成服务
│   │   └── PdfReportService.java      # PDF报告生成服务
│   ├── controller/                    # 控制器
│   │   ├── PdfReportController.java   # PDF报告API控制器
│   │   └── PdfDemoController.java     # PDF演示控制器
│   └── test/                          # 测试类
│       └── PdfModuleTest.java         # 模块测试
├── API_USAGE.md                       # API使用文档
├── PDF_MODULE_GUIDE.md                # 模块使用指南
├── DEPLOYMENT_GUIDE.md                # 部署指南
└── test-pdf-module.sh                 # 测试脚本
```

## ✨ 核心功能

### 1. 支持的图表类型
- **柱状图 (bar)**: 适用于分类数据比较
- **折线图 (line)**: 适用于趋势数据展示
- **饼图 (pie)**: 适用于比例数据展示

### 2. PDF功能特性
- 中文字体支持 (STSong-Light)
- 自定义标题和描述
- 自动生成时间戳
- 可选水印功能
- 图表自动缩放适应页面

### 3. API接口
- `POST /api/pdf-report/generate` - 生成PDF报告
- `POST /api/pdf-report/preview` - 在线预览PDF
- `GET /api/pdf-report/sample` - 获取示例PDF
- `GET /api/demo/generate-pdf` - 简单演示接口

## 🚀 快速开始

### 1. 确保依赖已配置
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

### 2. 运行测试
```bash
# 方式1: 使用测试脚本
./test-pdf-module.sh

# 方式2: 启动Spring Boot应用
mvn spring-boot:run

# 方式3: 单独测试模块
mvn compile exec:java -Dexec.mainClass="cn.raxcl.test.PdfModuleTest"
```

### 3. 测试API接口
```bash
# 获取示例PDF
curl -X GET http://localhost:8080/api/pdf-report/sample -o sample.pdf

# 生成自定义PDF
curl -X POST http://localhost:8080/api/pdf-report/generate \
  -H "Content-Type: application/json" \
  -d '{
    "title": "销售报告",
    "description": "2024年销售数据分析",
    "fileName": "sales-report.pdf",
    "includeWatermark": true,
    "watermarkText": "机密文档",
    "charts": [{
      "title": "销售额对比",
      "chartType": "bar",
      "xAxisLabel": "产品",
      "yAxisLabel": "销售额(万元)",
      "dataPoints": [
        {"label": "产品A", "value": 120.0},
        {"label": "产品B", "value": 85.0}
      ]
    }]
  }' \
  -o custom-report.pdf
```

## 📚 使用示例

### Java代码示例
```java
// 创建服务实例
ChartService chartService = new ChartService();
PdfReportService pdfService = new PdfReportService(chartService);

// 创建请求对象
PdfGenerateRequest request = new PdfGenerateRequest();
request.setTitle("数据分析报告");
request.setDescription("这是一个示例报告");
request.setIncludeWatermark(true);

// 创建图表数据
ChartData chart = new ChartData();
chart.setTitle("用户增长");
chart.setChartType("line");
chart.setXAxisLabel("月份");
chart.setYAxisLabel("用户数");
chart.setDataPoints(Arrays.asList(
    new ChartDataPoint("1月", 100.0),
    new ChartDataPoint("2月", 150.0),
    new ChartDataPoint("3月", 200.0)
));

request.setCharts(Arrays.asList(chart));

// 生成PDF
byte[] pdfBytes = pdfService.generatePdfReport(request);

// 保存文件
Files.write(Paths.get("report.pdf"), pdfBytes);
```

### JavaScript前端调用示例
```javascript
const generatePDF = async () => {
  const requestData = {
    title: "销售报告",
    description: "2024年第一季度销售数据",
    fileName: "sales-report.pdf",
    includeWatermark: true,
    watermarkText: "机密文档",
    charts: [{
      title: "销售数据",
      chartType: "bar",
      xAxisLabel: "产品",
      yAxisLabel: "销售额",
      dataPoints: [
        { label: "产品A", value: 120 },
        { label: "产品B", value: 85 }
      ]
    }]
  };

  try {
    const response = await fetch('/api/pdf-report/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestData)
    });

    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'report.pdf';
      a.click();
    }
  } catch (error) {
    console.error('PDF生成失败:', error);
  }
};
```

## ⚙️ 配置选项

### 字体配置
修改 `PdfReportService.java` 中的字体设置：
```java
BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
```

### 图表样式
修改 `ChartService.java` 中的样式设置：
```java
private void setChartStyle(JFreeChart chart) {
    chart.getTitle().setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
    // 自定义样式...
}
```

## 🔧 故障排除

### 常见问题及解决方案

1. **编译错误**
   - 确保Maven依赖正确配置
   - 检查Java版本 (需要Java 8+)

2. **字体问题**
   - 安装STSong-Light字体
   - 或使用系统默认字体

3. **图表不显示**
   - 检查图表数据格式
   - 验证chartType值是否正确

4. **PDF文件损坏**
   - 检查是否有异常抛出
   - 确保PDF数据完整传输

## 📈 性能优化建议

1. **内存管理**
   - 处理大数据量时考虑分页
   - 及时释放图表资源

2. **缓存策略**
   - 缓存常用图表模板
   - 使用对象池优化性能

3. **异步处理**
   - 大文件生成使用异步处理
   - 提供进度反馈机制

## 🚀 扩展功能

### 计划中的功能
- 表格支持
- 更多图表类型 (散点图、雷达图等)
- PDF模板系统
- 批量生成功能
- Excel导出功能

### 自定义扩展
- 继承现有服务类添加功能
- 实现自定义图表渲染器
- 扩展PDF元素类型

## 📝 文档链接

- [API使用文档](./API_USAGE.md) - 详细的API接口说明
- [模块使用指南](./PDF_MODULE_GUIDE.md) - 完整的使用指南
- [部署指南](./DEPLOYMENT_GUIDE.md) - 生产环境部署说明

## 🤝 支持与反馈

如果在使用过程中遇到问题或有改进建议，请：

1. 查看相关文档
2. 运行测试脚本进行诊断
3. 检查日志输出
4. 联系开发团队

---

**版本**: v1.0.0  
**最后更新**: 2024-01-01  
**兼容性**: Java 8+, Spring Boot 2.x+

✅ **项目状态**: PDF模块已完成开发，可以直接使用或集成到现有项目中。 