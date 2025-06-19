package cn.raxcl.dto;

import java.util.List;

/**
 * PDF生成请求
 * @author raxcl
 */
public class PdfGenerateRequest {
    private String title;
    private String description;
    private String fileName;
    private boolean includeWatermark;
    private String watermarkText;
    private List<ChartData> charts;

    public PdfGenerateRequest() {}

    public PdfGenerateRequest(String title, String description, String fileName, boolean includeWatermark, String watermarkText, List<ChartData> charts) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.includeWatermark = includeWatermark;
        this.watermarkText = watermarkText;
        this.charts = charts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIncludeWatermark() {
        return includeWatermark;
    }

    public void setIncludeWatermark(boolean includeWatermark) {
        this.includeWatermark = includeWatermark;
    }

    public String getWatermarkText() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }

    public List<ChartData> getCharts() {
        return charts;
    }

    public void setCharts(List<ChartData> charts) {
        this.charts = charts;
    }
} 