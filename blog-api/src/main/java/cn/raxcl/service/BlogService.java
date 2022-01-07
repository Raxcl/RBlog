package cn.raxcl.service;

import cn.raxcl.entity.Blog;
import cn.raxcl.model.dto.BlogDTO;
import cn.raxcl.model.dto.BlogVisibility;
import cn.raxcl.model.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @author c-long.chan
 * 2022-01-06 19:26:40
 */
public interface BlogService {
	/**
	 * 获取博客文章列表
	 * @param title 获取博客文章列表
	 * @param categoryId 按分类id查询
	 * @return List<Blog>
	 */
	List<Blog> getListByTitleAndCategoryId(String title, Integer categoryId);

	/**
	 * 按关键字根据文章内容搜索公开且无密码保护的博客文章
	 * @param query 关键字字符串
	 * @return List<SearchBlog>
	 */
	List<SearchBlog> getSearchBlogListByQueryAndIsPublished(String query);

	/**
	 * 获取所有博客id和title 供评论分类的选择
	 * @return List<Blog>
	 */
	List<Blog> getIdAndTitleList();

	/**
	 * 获取博客list
	 * @return List<NewBlog>
	 */
	List<NewBlog> getNewBlogListByIsPublished();

	/**
	 * 按置顶、创建时间排序 分页查询博客简要信息列表
	 * @param pageNum 页码
	 * @return PageResult<BlogInfo>
	 */
	PageResult<BlogInfo> getBlogInfoListByIsPublished(Integer pageNum);

	/**
	 * 根据分类name分页查询公开博客列表
	 * @param categoryName 分类name
	 * @param pageNum 页码
	 * @return PageResult<BlogInfo>
	 */
	PageResult<BlogInfo> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum);

	/**
	 * 根据标签name分页查询公开博客列表
	 *
	 * @param tagName 标签name
	 * @param pageNum 页码
	 * @return PageResult<BlogInfo>
	 */
	PageResult<BlogInfo> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum);

	/**
	 * 按年月分组归档公开博客 统计公开博客总数
	 *
	 * @return Map<String, Object>
	 */
	Map<String, Object> getArchiveBlogAndCountByIsPublished();

	/**
	 * 获取站点配置信息、最新推荐博客、分类列表、标签云、随机博客
	 *
	 * @return List<RandomBlog>
	 */
	List<RandomBlog> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();

	/**
	 * 删除博客
	 * @param id id
	 */
	void deleteBlogById(Long id);

	/**
	 * 删除博客标签
	 * @param blogId blogId
	 */
	void deleteBlogTagByBlogId(Long blogId);

	/**
	 * 保存博客
	 * @param blogDTO blogDTO
	 */
	void saveBlog(BlogDTO blogDTO);

	/**
	 * 保存博客标签
	 * @param blogId blogId
	 * @param tagId tagId
	 */
	void saveBlogTag(Long blogId, Long tagId);

	/**
	 * 更新博客推荐状态
	 * @param blogId 博客id
	 * @param recommend 是否推荐
	 */
	void updateBlogRecommendById(Long blogId, Boolean recommend);

	/**
	 * 更新博客可见性状态
	 * @param blogId 博客id
	 * @param blogVisibility 博客可见性DTO
	 */
	void updateBlogVisibilityById(Long blogId, BlogVisibility blogVisibility);

	/**
	 * 更新博客置顶状态
	 * @param blogId 博客id
	 * @param top 是否置顶
	 */
	void updateBlogTopById(Long blogId, Boolean top);

	/**
	 * 从Redis同步博客文章浏览量到数据库
	 * @param blogId 博客id
	 * @param views views
	 */
	void updateViews(Long blogId, Integer views);

	/**
	 * 按id获取博客详情
	 * @param id 博客id
	 * @return Blog
	 */
	Blog getBlogById(Long id);

	/**
	 * 根据博客id获取标题
	 * @param id 博客id
	 * @return String
	 */
	String getTitleByBlogId(Long id);

	/**
	 * 获取博客密码
	 * @param blogId 博客id
	 * @return String
	 */
	String getBlogPassword(Long blogId);

	/**
	 * 更新博客
	 * @param blogDTO 博客dto
	 */
	void updateBlog(BlogDTO blogDTO);

	/**
	 * 按id删除分类
	 * @param categoryId 分类id
	 * @return int
	 */
	int countBlogByCategoryId(Long categoryId);

	/**
	 * 按id删除标签
	 * @param tagId 标签id
	 * @return int
	 */
	int countBlogByTagId(Long tagId);

	/**
	 * 获取是否可评论
	 * @param blogId 博客id
	 * @return Boolean
	 */
	Boolean getCommentEnabledByBlogId(Long blogId);

	/**
	 * 获取是否发版
	 * @param blogId 博客id
	 * @return Boolean
	 */
	Boolean getPublishedByBlogId(Long blogId);

	/**
	 * 执行博客添加或更新操作：校验参数是否合法，添加分类、标签，维护博客标签关联表
	 *
	 * @param blogDTO 博客文章DTO
	 * @param type 添加或更新
	 * @return Result
	 */
	Result getResult(BlogDTO blogDTO, String type);

	/**
	 * 按id获取公开博客详情
	 *
	 * @param id  博客id
	 * @param jwt 密码保护文章的访问Token
	 * @return Result
	 */
	BlogDetail getBlog(Long id, String jwt);
}
