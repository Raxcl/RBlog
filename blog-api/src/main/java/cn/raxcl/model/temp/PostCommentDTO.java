package cn.raxcl.model.temp;

import cn.raxcl.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * @author c-long.chan
 * 2022-01-05 19:28:16
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
public class PostCommentDTO {
    /**
     * 是否访客的评论
     */
    private Boolean isVisitorComment;

    String subject;

    User admin;

    /**
     * 文章状态
     */
    Integer judgeResult;
}
