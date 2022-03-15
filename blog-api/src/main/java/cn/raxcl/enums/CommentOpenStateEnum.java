package cn.raxcl.enums;

/**
 * 评论开放状态枚举类
 * @author Raxcl
 * @date 2022-03-15 14:47:06
 */
public enum CommentOpenStateEnum {
    /**
     * 博客状态不存在，或博客未公开
     */
    NOT_FOUND,
    /**
     * 评论正常开放
     */
    OPEN,
    /**
     * 评论已关闭
     */
    CLOSE,
    /**
     * 评论所在页面需要密码
     */
    PASSWORD,
}
