package cn.raxcl.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * PDF生成请求实体类
 * @author raxcl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfGenerateRequest {
    
    /**
     * PDF文件标题
     */
    @NotBlank(message = "PDF标题不能为空")
    private String title;
    
    /**
     * PDF内容描述
     */
    private String description;
    
    /**
     * 图表数据列表
     */
    @NotNull(message = "图表数据不能为空")
    private List<ChartData> charts;
    
    /**
     * 用户ID（可选，用于权限控制）
     */
    private Long userId;
    
    /**
     * 文件名（不包含扩展名）
     */
    private String fileName;
    
    /**
     * 是否包含水印
     */
    private Boolean includeWatermark = false;
    
    /**
     * 水印文本
     */
    private String watermarkText;
} 