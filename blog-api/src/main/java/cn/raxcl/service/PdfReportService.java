package cn.raxcl.service;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.PdfGenerateRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PDF报告生成服务
 * 
 * @author raxcl
 * @date 2024-01-01
 */
@Service
public class PdfReportService {
    
    private final ChartService chartService;
    
    public PdfReportService() {
        this.chartService = new ChartService();
    }
    
    public PdfReportService(ChartService chartService) {
        this.chartService = chartService;
    }
    
    /**
     * 生成PDF报告
     * 
     * @param request PDF生成请求
     * @return PDF字节数组
     * @throws Exception 生成异常
     */
    public byte[] generatePdfReport(PdfGenerateRequest request) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // 设置中文字体
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);
        Font smallFont = new Font(baseFont, 10, Font.NORMAL);
        
        // 添加标题
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            Paragraph title = new Paragraph(request.getTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
        }
        
        // 添加描述
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            Paragraph description = new Paragraph(request.getDescription(), normalFont);
            description.setAlignment(Element.ALIGN_LEFT);
            description.setSpacingAfter(20);
            document.add(description);
        }
        
        // 添加生成时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Paragraph time = new Paragraph("生成时间: " + sdf.format(new Date()), smallFont);
        time.setAlignment(Element.ALIGN_RIGHT);
        time.setSpacingAfter(30);
        document.add(time);
        
        // 添加图表
        if (request.getCharts() != null && !request.getCharts().isEmpty()) {
            for (ChartData chartData : request.getCharts()) {
                addChart(document, chartData, titleFont);
            }
        }
        
        // 添加水印
        if (request.isIncludeWatermark()) {
            addWatermark(writer, request.getWatermarkText(), baseFont);
        }
        
        document.close();
        writer.close();
        
        return baos.toByteArray();
    }
    
    /**
     * 添加图表到PDF
     */
    private void addChart(Document document, ChartData chartData, Font titleFont) throws Exception {
        // 添加图表标题
        if (chartData.getTitle() != null && !chartData.getTitle().isEmpty()) {
            Paragraph chartTitle = new Paragraph(chartData.getTitle(), titleFont);
            chartTitle.setAlignment(Element.ALIGN_LEFT);
            chartTitle.setSpacingAfter(10);
            document.add(chartTitle);
        }
        
        // 生成图表图片
        byte[] chartImageBytes = chartService.generateChart(chartData);
        Image chartImage = Image.getInstance(chartImageBytes);
        
        // 缩放图片以适应页面
        float maxWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float maxHeight = 300;
        
        if (chartImage.getWidth() > maxWidth) {
            float scale = maxWidth / chartImage.getWidth();
            chartImage.scalePercent(scale * 100);
        }
        
        if (chartImage.getScaledHeight() > maxHeight) {
            float scale = maxHeight / chartImage.getScaledHeight();
            chartImage.scalePercent(chartImage.getScaledWidth() * scale, maxHeight);
        }
        
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.setSpacingAfter(20);
        document.add(chartImage);
    }
    
    /**
     * 添加水印
     */
    private void addWatermark(PdfWriter writer, String watermarkText, BaseFont baseFont) throws Exception {
        PdfContentByte canvas = writer.getDirectContent();
        
        String text = (watermarkText != null && !watermarkText.isEmpty()) ? 
                      watermarkText : "PDF报告系统";
        
        canvas.saveState();
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(0.3f);
        canvas.setGState(gState);
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 40);
        canvas.setTextMatrix(200, 400);
        canvas.showTextAligned(Element.ALIGN_CENTER, text, 300, 400, 45);
        canvas.endText();
        canvas.restoreState();
    }
} 