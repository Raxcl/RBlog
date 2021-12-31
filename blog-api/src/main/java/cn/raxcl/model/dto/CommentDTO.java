package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 评论DTO
 * @author Raxcl
 * @date 2021-12-31 15:16:54
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO {
	private Long id;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 头像(图片路径)
	 */
	private String avatar;
	/**
	 * 评论时间
	 */
	private Date createTime;
	/**
	 * 个人网站
	 */
	private String website;
	/**
	 * 评论者ip地址
	 */
	private String ip;
	/**
	 * 公开或回收站
	 */
	private Boolean published;
	/**
	 * 博主回复
	 */
	private Boolean adminComment;
	/**
	 * 0普通文章，1关于我页面
	 */
	private Integer page;
	/**
	 * 接收邮件提醒
	 */
	private Boolean notice;
	/**
	 * 父评论id
	 */
	private Long parentCommentId;
	/**
	 * 所属的文章id
	 */
	private Long blogId;
	/**
	 * 如果评论昵称为QQ号，则将昵称和头像置为QQ昵称和QQ头像，并将此字段置为QQ号备份
	 */
	private String qq;
}
