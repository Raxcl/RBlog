package cn.raxcl.model.temp;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author c-long.chan
 * 2022-01-10 11:42:42
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PageDTO {
    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 排序方式
     */
    private String orderBy;

}
