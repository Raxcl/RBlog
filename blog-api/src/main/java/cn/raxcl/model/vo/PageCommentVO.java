package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 页面评论
 * @author Raxcl
 * @date 2022-01-07 09:33:43
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PageCommentVO {
	private Long id;
	/**
	 * 昵称
	 */
	private String nickname;
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
	 * 博主回复
	 */
	private Boolean adminComment;
	/**
	 * 父评论id
	 */
	private String parentCommentId;
	/**
	 * 父评论昵称
	 */
	private String parentCommentNickname;
	/**
	 * 回复该评论的评论
	 */
	private List<PageCommentVO> replyComments;
}
