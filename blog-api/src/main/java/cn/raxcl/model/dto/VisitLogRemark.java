package cn.raxcl.model.dto;

import lombok.*;

/**
 * 访问日志备注
 * @author Raxcl
 * @date 2022/3/16 15:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitLogRemark {
    /**
     * 访问内容
     */
    private String content;

    /**
     * 备注
     */
    private String remark;
}
