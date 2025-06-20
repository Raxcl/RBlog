package cn.raxcl.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML转PDF服务
 * 使用iText库进行高质量的HTML到PDF转换
 * @author raxcl
 */
@Service
public class HtmlToPdfService {
    
    /**
     * 转换HTML内容为PDF
     * 改进版本，更好地处理CSS样式和HTML结构
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        try {
            return generateAdvancedPdfContent(htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
            // 发生错误时返回错误PDF
            return generateErrorPdf("PDF生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成测试PDF
     */
    public byte[] generateTestPdf() throws IOException {
        String testHtml = generateTestHtml();
        return convertHtmlToPdf(testHtml);
    }
    
    /**
     * 生成测试HTML内容
     */
    private String generateTestHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>测试报告</title>\n" +
                "    <style>\n" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; }\n" +
                "        .content { padding: 20px; }\n" +
                "        table { width: 100%; border-collapse: collapse; }\n" +
                "        th, td { border: 1px solid #ddd; padding: 8px; }\n" +
                "        th { background: #f2f2f2; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"header\">\n" +
                "        <h1>RBlog 测试报告</h1>\n" +
                "        <p>生成时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "        <h2>功能测试结果</h2>\n" +
                "        <table>\n" +
                "            <tr><th>测试项目</th><th>结果</th><th>备注</th></tr>\n" +
                "            <tr><td>HTML解析</td><td>通过</td><td>正常</td></tr>\n" +
                "            <tr><td>CSS处理</td><td>通过</td><td>基础样式支持</td></tr>\n" +
                "            <tr><td>中文支持</td><td>通过</td><td>使用SimSun字体</td></tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    
    /**
     * 高级PDF内容生成 - 更好地处理HTML和CSS
     */
    private byte[] generateAdvancedPdfContent(String htmlContent) {
        try {
            // 创建PDF文档
            Document document = new Document(PageSize.A4, 20, 20, 30, 30);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            
            document.open();

            // 设置中文字体
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 24, Font.BOLD, new BaseColor(52, 62, 80));
            Font headerFont = new Font(baseFont, 18, Font.BOLD, new BaseColor(52, 62, 80));
            Font subHeaderFont = new Font(baseFont, 14, Font.BOLD, new BaseColor(108, 117, 125));
            Font contentFont = new Font(baseFont, 12, Font.NORMAL, new BaseColor(33, 33, 33));
            Font smallFont = new Font(baseFont, 10, Font.NORMAL, new BaseColor(108, 117, 125));

            // 解析HTML内容
            String title = extractTitle(htmlContent);
            String companyName = extractCompanyName(htmlContent);
            
            // 添加头部区域 - 模拟CSS渐变背景效果
            addStyledHeader(document, title, companyName, titleFont, contentFont);
            
            // 添加内容部分
            addAdvancedContentFromHtml(document, htmlContent, headerFont, subHeaderFont, contentFont);
            
            // 添加表格
            addAdvancedTablesFromHtml(document, htmlContent, headerFont, contentFont);
            
            // 添加页脚
            addStyledFooter(document, smallFont);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return generateErrorPdf("生成PDF时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 添加样式化的头部
     */
    private void addStyledHeader(Document document, String title, String companyName, Font titleFont, Font contentFont) throws DocumentException {
        // 创建表格模拟背景色效果
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new BaseColor(102, 126, 234)); // 模拟渐变主色
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(20);
        
        // 添加标题内容
        Paragraph headerParagraph = new Paragraph();
        headerParagraph.setAlignment(Element.ALIGN_CENTER);
        
        if (companyName != null && !companyName.trim().isEmpty()) {
            Chunk companyChunk = new Chunk(companyName + "\n", new Font(titleFont.getBaseFont(), 20, Font.BOLD, BaseColor.WHITE));
            headerParagraph.add(companyChunk);
        }
        
        if (title != null && !title.trim().isEmpty()) {
            Chunk titleChunk = new Chunk(title + "\n", new Font(titleFont.getBaseFont(), 16, Font.NORMAL, BaseColor.WHITE));
            headerParagraph.add(titleChunk);
        }
        
        Chunk dateChunk = new Chunk("生成时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                                   new Font(contentFont.getBaseFont(), 12, Font.NORMAL, BaseColor.WHITE));
        headerParagraph.add(dateChunk);
        
        headerCell.addElement(headerParagraph);
        headerTable.addCell(headerCell);
        
        document.add(headerTable);
        document.add(new Paragraph(" ")); // 添加间距
    }
    
    /**
     * 添加高级内容解析
     */
    private void addAdvancedContentFromHtml(Document document, String htmlContent, Font headerFont, Font subHeaderFont, Font contentFont) throws DocumentException {
        // 解析section内容
        Pattern sectionPattern = Pattern.compile("<div[^>]*class=[\"']section[\"'][^>]*>(.*?)</div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher sectionMatcher = sectionPattern.matcher(htmlContent);
        
        while (sectionMatcher.find()) {
            String sectionContent = sectionMatcher.group(1);
            
            // 解析section标题
            Pattern titlePattern = Pattern.compile("<[hH][1-6][^>]*[^>]*class=[\"'][^\"']*section-title[^\"']*[\"'][^>]*>(.*?)</[hH][1-6]>", Pattern.DOTALL);
            Matcher titleMatcher = titlePattern.matcher(sectionContent);
            
            if (titleMatcher.find()) {
                String sectionTitle = titleMatcher.group(1).replaceAll("<[^>]*>", "").trim();
                if (!sectionTitle.isEmpty()) {
                    Paragraph titleParagraph = new Paragraph(sectionTitle, headerFont);
                    titleParagraph.setSpacingBefore(15);
                    titleParagraph.setSpacingAfter(10);
                    document.add(titleParagraph);
                }
            }
            
            // 解析overview-grid（品牌概览卡片）
            Pattern gridPattern = Pattern.compile("<div[^>]*class=[\"']overview-grid[\"'][^>]*>(.*?)</div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher gridMatcher = gridPattern.matcher(sectionContent);
            
            if (gridMatcher.find()) {
                addOverviewCards(document, gridMatcher.group(1), subHeaderFont, contentFont);
            }
            
            // 解析普通段落
            Pattern paragraphPattern = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher paragraphMatcher = paragraphPattern.matcher(sectionContent);
            
            while (paragraphMatcher.find()) {
                String paragraphText = paragraphMatcher.group(1).replaceAll("<[^>]*>", "").trim();
                if (!paragraphText.isEmpty()) {
                    Paragraph paragraph = new Paragraph(paragraphText, contentFont);
                    paragraph.setSpacingAfter(8);
                    document.add(paragraph);
                }
            }
        }
    }
    
    /**
     * 添加概览卡片
     */
    private void addOverviewCards(Document document, String gridContent, Font labelFont, Font valueFont) throws DocumentException {
        Pattern cardPattern = Pattern.compile("<div[^>]*class=[\"']overview-card[\"'][^>]*>(.*?)</div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher cardMatcher = cardPattern.matcher(gridContent);
        
        PdfPTable cardTable = new PdfPTable(2);
        cardTable.setWidthPercentage(100);
        cardTable.setSpacingBefore(10);
        cardTable.setSpacingAfter(15);
        
        int cardCount = 0;
        while (cardMatcher.find() && cardCount < 2) {
            String cardContent = cardMatcher.group(1);
            
            // 提取标签和值
            Pattern labelPattern = Pattern.compile("<div[^>]*class=[\"']overview-label[\"'][^>]*>(.*?)</div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Pattern valuePattern = Pattern.compile("<div[^>]*class=[\"']overview-value[\"'][^>]*>(.*?)</div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            
            Matcher labelMatcher = labelPattern.matcher(cardContent);
            Matcher valueMatcher = valuePattern.matcher(cardContent);
            
            String label = labelMatcher.find() ? labelMatcher.group(1).replaceAll("<[^>]*>", "").trim() : "";
            String value = valueMatcher.find() ? valueMatcher.group(1).replaceAll("<[^>]*>", "").trim() : "";
            
            if (!label.isEmpty() || !value.isEmpty()) {
                PdfPCell cardCell = new PdfPCell();
                cardCell.setBackgroundColor(new BaseColor(248, 249, 250));
                cardCell.setBorder(Rectangle.BOX);
                cardCell.setBorderColor(new BaseColor(222, 226, 230));
                cardCell.setPadding(15);
                
                Paragraph cardParagraph = new Paragraph();
                cardParagraph.setAlignment(Element.ALIGN_CENTER);
                
                if (!label.isEmpty()) {
                    cardParagraph.add(new Chunk(label + "\n", new Font(labelFont.getBaseFont(), 11, Font.NORMAL, new BaseColor(108, 117, 125))));
                }
                if (!value.isEmpty()) {
                    cardParagraph.add(new Chunk(value, new Font(valueFont.getBaseFont(), 18, Font.BOLD, new BaseColor(52, 62, 80))));
                }
                
                cardCell.addElement(cardParagraph);
                cardTable.addCell(cardCell);
                cardCount++;
            }
        }
        
        if (cardCount > 0) {
            // 如果只有一个卡片，添加空单元格
            if (cardCount == 1) {
                cardTable.addCell(new PdfPCell());
            }
            document.add(cardTable);
        }
    }
    
    /**
     * 添加高级表格处理
     */
    private void addAdvancedTablesFromHtml(Document document, String htmlContent, Font headerFont, Font contentFont) throws DocumentException {
        Pattern tablePattern = Pattern.compile("<table[^>]*class=[\"'][^\"']*metrics-table[^\"']*[\"'][^>]*>(.*?)</table>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(htmlContent);

        while (tableMatcher.find()) {
            String tableContent = tableMatcher.group(1);
            
            // 解析表头
            Pattern theadPattern = Pattern.compile("<thead[^>]*>(.*?)</thead>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher theadMatcher = theadPattern.matcher(tableContent);
            
            // 解析表体
            Pattern tbodyPattern = Pattern.compile("<tbody[^>]*>(.*?)</tbody>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher tbodyMatcher = tbodyPattern.matcher(tableContent);
            
            if (theadMatcher.find() && tbodyMatcher.find()) {
                String thead = theadMatcher.group(1);
                String tbody = tbodyMatcher.group(1);
                
                // 计算列数
                Pattern thPattern = Pattern.compile("<th[^>]*>", Pattern.CASE_INSENSITIVE);
                Matcher thMatcher = thPattern.matcher(thead);
                int columnCount = 0;
                while (thMatcher.find()) {
                    columnCount++;
                }
                
                if (columnCount > 0) {
                    PdfPTable table = new PdfPTable(columnCount);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10);
                    table.setSpacingAfter(15);
                    
                    // 添加表头
                    Pattern thContentPattern = Pattern.compile("<th[^>]*>(.*?)</th>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    Matcher thContentMatcher = thContentPattern.matcher(thead);
                    
                    while (thContentMatcher.find()) {
                        String headerText = thContentMatcher.group(1).replaceAll("<[^>]*>", "").trim();
                        PdfPCell headerCell = new PdfPCell(new Phrase(headerText, new Font(headerFont.getBaseFont(), 12, Font.BOLD, BaseColor.WHITE)));
                        headerCell.setBackgroundColor(new BaseColor(102, 126, 234));
                        headerCell.setPadding(12);
                        headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(headerCell);
                    }
                    
                    // 添加表体行
                    Pattern trPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    Matcher trMatcher = trPattern.matcher(tbody);
                    
                    int rowIndex = 0;
                    while (trMatcher.find()) {
                        String rowContent = trMatcher.group(1);
                        
                        Pattern tdPattern = Pattern.compile("<td[^>]*>(.*?)</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                        Matcher tdMatcher = tdPattern.matcher(rowContent);
                        
                        while (tdMatcher.find()) {
                            String cellText = tdMatcher.group(1).replaceAll("<[^>]*>", "").trim();
                            PdfPCell cell = new PdfPCell(new Phrase(cellText, contentFont));
                            cell.setPadding(10);
                            
                            // 交替行背景色
                            if (rowIndex % 2 == 1) {
                                cell.setBackgroundColor(new BaseColor(248, 249, 250));
                            }
                            
                            table.addCell(cell);
                        }
                        rowIndex++;
                    }
                    
                    document.add(table);
                }
            }
        }
    }
    
    /**
     * 添加样式化的页脚
     */
    private void addStyledFooter(Document document, Font footerFont) throws DocumentException {
        // 添加一些间距
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        // 创建页脚表格
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        
        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(new BaseColor(44, 62, 80));
        footerCell.setBorder(Rectangle.NO_BORDER);
        footerCell.setPadding(15);
        
        Paragraph footerParagraph = new Paragraph();
        footerParagraph.setAlignment(Element.ALIGN_CENTER);
        
        footerParagraph.add(new Chunk("© 2024 RBlog 企业版. 保留所有权利.\n", new Font(footerFont.getBaseFont(), 10, Font.NORMAL, BaseColor.WHITE)));
        footerParagraph.add(new Chunk("生成时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                                     new Font(footerFont.getBaseFont(), 9, Font.NORMAL, new BaseColor(200, 200, 200))));
        
        footerCell.addElement(footerParagraph);
        footerTable.addCell(footerCell);
        
        document.add(footerTable);
    }
    
    /**
     * 从HTML中提取标题
     */
    private String extractTitle(String htmlContent) {
        // 优先从title标签提取
        Pattern titlePattern = Pattern.compile("<title[^>]*>(.*?)</title>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher titleMatcher = titlePattern.matcher(htmlContent);
        if (titleMatcher.find()) {
            String title = titleMatcher.group(1).replaceAll("<[^>]*>", "").trim();
            if (!title.isEmpty()) {
                return title;
            }
        }
        
        // 从report-title class提取
        Pattern reportTitlePattern = Pattern.compile("<[^>]+class=[\"'][^\"']*report-title[^\"']*[\"'][^>]*>(.*?)</[^>]+>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher reportTitleMatcher = reportTitlePattern.matcher(htmlContent);
        if (reportTitleMatcher.find()) {
            return reportTitleMatcher.group(1).replaceAll("<[^>]*>", "").trim();
        }
        
        // 从h1标签提取
        Pattern h1Pattern = Pattern.compile("<h1[^>]*>(.*?)</h1>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher h1Matcher = h1Pattern.matcher(htmlContent);
        if (h1Matcher.find()) {
            return h1Matcher.group(1).replaceAll("<[^>]*>", "").trim();
        }
        
        return "品牌洞察报告";
    }
    
    /**
     * 从HTML中提取公司名称
     */
    private String extractCompanyName(String htmlContent) {
        // 从company-name class提取
        Pattern companyPattern = Pattern.compile("<[^>]+class=[\"'][^\"']*company-name[^\"']*[\"'][^>]*>(.*?)</[^>]+>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher companyMatcher = companyPattern.matcher(htmlContent);
        if (companyMatcher.find()) {
            return companyMatcher.group(1).replaceAll("<[^>]*>", "").trim();
        }
        
        return null;
    }
    
    /**
     * 生成错误PDF
     */
    private byte[] generateErrorPdf(String errorMessage) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font font = new Font(baseFont, 12, Font.NORMAL);

            Paragraph paragraph = new Paragraph("PDF生成错误", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("错误信息: " + errorMessage, font));
            document.add(new Paragraph("时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), font));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
} 