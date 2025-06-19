# PDF生成API使用文档

## 概述

本服务提供了一个强大的PDF生成接口，支持根据用户数据生成包含各种图表的PDF报告。支持折线图、柱状图和饼图三种图表类型。

## 依赖说明

项目已添加以下依赖：
- `itext` 5.5.13.3 - PDF生成
- `itext-asian` 5.2.0 - 中文字体支持
- `jfreechart` 1.5.3 - 图表生成

## API接口

### 1. 生成PDF文件（下载）

**接口地址：** `POST /api/pdf/generate`

**请求体：**
```json
{
  "title": "用户数据分析报告",
  "description": "本报告包含了用户活跃度、销售数据和访问统计的详细分析。",
  "fileName": "用户数据报告",
  "includeWatermark": true,
  "watermarkText": "CONFIDENTIAL",
  "charts": [
    {
      "title": "用户活跃度趋势",
      "xAxisLabel": "日期",
      "yAxisLabel": "活跃用户数",
      "chartType": "line",
      "datasets": {
        "2023年": [
          {"label": "1月", "value": 1200.0},
          {"label": "2月", "value": 1350.0},
          {"label": "3月", "value": 1100.0}
        ],
        "2024年": [
          {"label": "1月", "value": 1500.0},
          {"label": "2月", "value": 1650.0},
          {"label": "3月", "value": 1400.0}
        ]
      }
    }
  ]
}
```

**响应：** PDF文件流（Content-Type: application/pdf）

### 2. PDF预览（在线查看）

**接口地址：** `POST /api/pdf/preview`

请求体格式与生成接口相同，但响应头设置为内联显示。

### 3. 测试接口

**接口地址：** `GET /api/test/sample-pdf`

无需参数，直接生成示例PDF报告用于测试。

## 数据结构说明

### ChartData（图表数据）

```java
{
  "title": "图表标题",
  "xAxisLabel": "X轴标签",
  "yAxisLabel": "Y轴标签", 
  "chartType": "图表类型：line(折线图)/bar(柱状图)/pie(饼图)",
  "datasets": {
    "数据系列名称": [
      {"label": "数据点标签", "value": 数值}
    ]
  }
}
```

### PdfGenerateRequest（PDF生成请求）

```java
{
  "title": "PDF标题（必填）",
  "description": "PDF描述（可选）",
  "fileName": "文件名（可选，默认使用时间戳）",
  "includeWatermark": false,
  "watermarkText": "水印文本",
  "charts": [] // ChartData数组（必填）
}
```

## 图表类型支持

### 1. 折线图 (line)
- 适用于趋势分析
- 支持多条数据线
- X轴为分类数据，Y轴为数值数据

### 2. 柱状图 (bar)
- 适用于数据对比
- 支持多个数据系列
- X轴为分类，Y轴为数值

### 3. 饼图 (pie)
- 适用于比例分析
- 只使用第一个数据系列
- 自动计算百分比

## 使用示例

### 使用curl测试

```bash
# 生成示例PDF
curl -X GET "http://localhost:8080/api/test/sample-pdf" -o sample.pdf

# 自定义数据生成PDF
curl -X POST "http://localhost:8080/api/pdf/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "销售报表",
    "description": "2024年第二季度销售数据分析",
    "charts": [
      {
        "title": "月度销售额",
        "xAxisLabel": "月份",
        "yAxisLabel": "销售额（万元）",
        "chartType": "bar",
        "datasets": {
          "销售额": [
            {"label": "4月", "value": 120.5},
            {"label": "5月", "value": 135.2},
            {"label": "6月", "value": 98.7}
          ]
        }
      }
    ]
  }' -o report.pdf
```

### JavaScript前端调用

```javascript
const requestData = {
  title: "数据分析报告",
  description: "用户行为分析",
  charts: [
    {
      title: "用户访问量",
      xAxisLabel: "日期",
      yAxisLabel: "访问量",
      chartType: "line",
      datasets: {
        "本周": [
          {label: "周一", value: 1200},
          {label: "周二", value: 1350},
          {label: "周三", value: 980}
        ]
      }
    }
  ]
};

fetch('/api/pdf/generate', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(requestData)
})
.then(response => response.blob())
.then(blob => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'report.pdf';
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
});
```

## 注意事项

1. **字体支持：** 已配置中文字体支持，可正常显示中文内容
2. **图片质量：** 图表生成尺寸为800x600像素，可根据需要调整
3. **文件大小：** 建议单个PDF包含的图表数量不超过10个
4. **数据量限制：** 单个图表建议数据点不超过100个
5. **水印功能：** 可选择是否添加水印及自定义水印文本

## 错误处理

API返回HTTP状态码：
- 200: 成功生成PDF
- 400: 请求参数错误
- 500: 服务器内部错误

建议在客户端添加适当的错误处理逻辑。 