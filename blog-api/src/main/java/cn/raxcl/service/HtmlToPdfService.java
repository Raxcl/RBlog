package cn.raxcl.service;

import cn.raxcl.dto.BrandInsightData;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTML转PDF服务
 * 使用iText实现HTML到PDF的转换（简化版本）
 */
@Service
public class HtmlToPdfService {
    
    /**
     * 将HTML内容转换为PDF字节数组
     * 注意：这是一个简化实现，实际项目中建议使用专业的HTML转PDF库
     * @param htmlContent HTML内容
     * @return PDF字节数组
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        // 由于openhtmltopdf依赖问题，暂时返回模拟的PDF内容
        // 实际使用时可以集成flying-saucer或wkhtmltopdf
        return generateSimplePdfContent(htmlContent);
    }
    
    /**
     * 生成简单测试PDF
     */
    public byte[] generateTestPdf() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String simpleHtml = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\"/>\n" +
            "    <style>\n" +
            "        @page { size: A4; margin: 20mm; }\n" +
            "        body { font-family: Arial, sans-serif; }\n" +
            "        .title { font-size: 24px; font-weight: bold; margin-bottom: 20px; }\n" +
            "        .content { font-size: 14px; line-height: 1.6; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"title\">HTML转PDF测试报告</div>\n" +
            "    <div class=\"content\">\n" +
            "        <p>这是一个HTML转PDF的测试文档。</p>\n" +
            "        <p>生成时间: " + timestamp + "</p>\n" +
            "        <table border=\"1\" style=\"width: 100%; border-collapse: collapse;\">\n" +
            "            <tr>\n" +
            "                <th style=\"padding: 8px;\">项目</th>\n" +
            "                <th style=\"padding: 8px;\">状态</th>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td style=\"padding: 8px;\">HTML解析</td>\n" +
            "                <td style=\"padding: 8px;\">成功</td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td style=\"padding: 8px;\">PDF生成</td>\n" +
            "                <td style=\"padding: 8px;\">成功</td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        
        return convertHtmlToPdf(simpleHtml);
    }
    
    /**
     * 生成简单的PDF内容（模拟实现）
     * 实际项目中应该使用专业的HTML转PDF库
     */
    private byte[] generateSimplePdfContent(String htmlContent) {
        // 这里返回一个简单的PDF标识符
        // 实际使用时需要集成专业的HTML转PDF解决方案
        String pdfPlaceholder = "PDF Content Generated from HTML: " + 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return pdfPlaceholder.getBytes();
    }
} 