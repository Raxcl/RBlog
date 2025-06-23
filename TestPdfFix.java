import java.io.*;
import java.lang.reflect.Method;

public class TestPdfFix {
    public static void main(String[] args) {
        try {
            // 测试OpenHtmlToPdf是否可用
            System.out.println("测试OpenHtmlToPdf库可用性...");
            
            // 通过反射检查OpenHtmlToPdf类是否存在
            Class<?> rendererBuilderClass = Class.forName("com.openhtmltopdf.pdfboxout.PdfRendererBuilder");
            Object builder = rendererBuilderClass.newInstance();
            
            System.out.println("✓ OpenHtmlToPdf库可用");
            
            // 创建简单的HTML内容
            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>测试PDF</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        h1 { color: #333; }
                    </style>
                </head>
                <body>
                    <h1>PDF生成测试</h1>
                    <p>这是一个测试PDF文档。</p>
                    <p>如果您看到这个内容，说明OpenHtmlToPdf正常工作。</p>
                </body>
                </html>
                """;
            
            // 创建输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // 设置HTML内容
            Method withHtmlContentMethod = rendererBuilderClass.getMethod("withHtmlContent", String.class, String.class);
            withHtmlContentMethod.invoke(builder, html, null);
            
            // 设置输出流
            Method toStreamMethod = rendererBuilderClass.getMethod("toStream", OutputStream.class);
            toStreamMethod.invoke(builder, outputStream);
            
            // 运行渲染
            Method runMethod = rendererBuilderClass.getMethod("run");
            runMethod.invoke(builder);
            
            // 检查生成的PDF
            byte[] pdfBytes = outputStream.toByteArray();
            if (pdfBytes.length > 0) {
                System.out.println("✓ PDF生成成功，大小: " + pdfBytes.length + " 字节");
                
                // 保存到文件
                try (FileOutputStream fos = new FileOutputStream("test-output.pdf")) {
                    fos.write(pdfBytes);
                    System.out.println("✓ PDF已保存到 test-output.pdf");
                }
            } else {
                System.out.println("✗ PDF生成失败，输出为空");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ OpenHtmlToPdf库未找到: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ PDF生成测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 