package cn.raxcl.service;

import cn.raxcl.entity.Tag;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 15:22:59
 */
public interface TagService {
	/**
	 * 获取博客标签列表
	 * @return List<Tag>
	 */
	List<Tag> getTagList();

	/**
	 * 获取所有标签List不查询id
	 * @return List<Tag>
	 */
	List<Tag> getTagListNotId();

	/**
	 * 按博客id查询List
	 * @param blogId blogId
	 * @return List<Tag>
	 */
	List<Tag> getTagListByBlogId(Long blogId);

	/**
	 * 添加标签
	 * @param tag tag
	 */
	void saveTag(Tag tag);

	/**
	 * 按id查询标签
	 * @param id id
	 * @return Tag
	 */
	Tag getTagById(Long id);

	/**
	 * 查询标签
	 * @param name name
	 * @return Tag
	 */
	Tag getTagByName(String name);

	/**
	 * 按id删除标签
	 * @param id id
	 */
	void deleteTagById(Long id);

	/**
	 * 更新标签
	 * @param tag tag
	 */
	void updateTag(Tag tag);
}
