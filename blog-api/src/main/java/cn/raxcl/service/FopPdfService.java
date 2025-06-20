package cn.raxcl.service;

import cn.raxcl.dto.ChartData;
import cn.raxcl.dto.PdfGenerateRequest;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 基于Apache FOP的PDF生成服务
 * 
 * @author raxcl
 * @date 2024-01-01
 */
@Service
public class FopPdfService {
    
    @Autowired
    private ChartService chartService;
    
    /**
     * 生成PDF报告
     * 
     * @param request PDF生成请求
     * @return PDF字节数组
     * @throws Exception 生成异常
     */
//    public byte[] generatePdfReport(PdfGenerateRequest request) throws Exception {
//        // 生成XSL-FO内容
//        String xslFoContent = generateXslFo(request);
//
//        // 使用FOP生成PDF
//        FopFactory fopFactory = FopFactory.newInstance();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);
//        TransformerFactory factory = TransformerFactory.newInstance();
//        Transformer transformer = factory.newTransformer();
//
//        Result result = new SAXResult(fop.getDefaultHandler());
//        transformer.transform(new StreamSource(new StringReader(xslFoContent)), result);
//
//        return outputStream.toByteArray();
//    }
    
    /**
     * 生成XSL-FO内容
     */
    private String generateXslFo(PdfGenerateRequest request) throws Exception {
        StringBuilder xslFo = new StringBuilder();
        
        // XSL-FO文档头
        xslFo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xslFo.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");
        
        // 布局主模板
        xslFo.append("  <fo:layout-master-set>\n");
        xslFo.append("    <fo:simple-page-master master-name=\"simple\" page-height=\"29.7cm\" page-width=\"21cm\" margin=\"2cm\">\n");
        xslFo.append("      <fo:region-body/>\n");
        xslFo.append("    </fo:simple-page-master>\n");
        xslFo.append("  </fo:layout-master-set>\n");
        
        // 页面内容
        xslFo.append("  <fo:page-sequence master-reference=\"simple\">\n");
        xslFo.append("    <fo:flow flow-name=\"xsl-region-body\">\n");
        
        // 添加标题
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            xslFo.append("      <fo:block font-size=\"18pt\" font-weight=\"bold\" text-align=\"center\" margin-bottom=\"20pt\">\n");
            xslFo.append("        ").append(escapeXml(request.getTitle())).append("\n");
            xslFo.append("      </fo:block>\n");
        }
        
        // 添加描述
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            xslFo.append("      <fo:block font-size=\"12pt\" margin-bottom=\"15pt\">\n");
            xslFo.append("        ").append(escapeXml(request.getDescription())).append("\n");
            xslFo.append("      </fo:block>\n");
        }
        
        // 添加生成时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        xslFo.append("      <fo:block font-size=\"10pt\" text-align=\"right\" margin-bottom=\"20pt\">\n");
        xslFo.append("        生成时间: ").append(sdf.format(new Date())).append("\n");
        xslFo.append("      </fo:block>\n");
        
        // 添加图表
        if (request.getCharts() != null && !request.getCharts().isEmpty()) {
            for (ChartData chartData : request.getCharts()) {
                addChartToXslFo(xslFo, chartData);
            }
        }
        
        // 添加水印（如果需要）
        if (request.isIncludeWatermark()) {
            addWatermarkToXslFo(xslFo, request.getWatermarkText());
        }
        
        xslFo.append("    </fo:flow>\n");
        xslFo.append("  </fo:page-sequence>\n");
        xslFo.append("</fo:root>\n");
        
        return xslFo.toString();
    }
    
    /**
     * 添加图表到XSL-FO
     */
    private void addChartToXslFo(StringBuilder xslFo, ChartData chartData) throws Exception {
        // 图表标题
        if (chartData.getTitle() != null && !chartData.getTitle().isEmpty()) {
            xslFo.append("      <fo:block font-size=\"14pt\" font-weight=\"bold\" margin-top=\"20pt\" margin-bottom=\"10pt\">\n");
            xslFo.append("        ").append(escapeXml(chartData.getTitle())).append("\n");
            xslFo.append("      </fo:block>\n");
        }
        
        // 生成图表图像并嵌入
        byte[] chartImageBytes = chartService.generateChart(chartData);
        String base64Image = Base64.getEncoder().encodeToString(chartImageBytes);
        
        xslFo.append("      <fo:block text-align=\"center\" margin-bottom=\"20pt\">\n");
        xslFo.append("        <fo:external-graphic src=\"data:image/png;base64,").append(base64Image).append("\" ");
        xslFo.append("content-width=\"15cm\" content-height=\"scale-to-fit\"/>\n");
        xslFo.append("      </fo:block>\n");
    }
    
    /**
     * 添加水印到XSL-FO
     */
    private void addWatermarkToXslFo(StringBuilder xslFo, String watermarkText) {
        String text = (watermarkText != null && !watermarkText.isEmpty()) ? 
                      watermarkText : "PDF报告系统";
        
        // 注意：FOP的水印实现较复杂，这里提供简化版本
        xslFo.append("      <fo:block position=\"absolute\" top=\"50%\" left=\"50%\" ");
        xslFo.append("font-size=\"48pt\" color=\"#CCCCCC\" text-align=\"center\">\n");
        xslFo.append("        ").append(escapeXml(text)).append("\n");
        xslFo.append("      </fo:block>\n");
    }
    
    /**
     * XML转义
     */
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
} 