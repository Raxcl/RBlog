package cn.raxcl.mapper;

import cn.raxcl.model.dto.BlogDTO;
import cn.raxcl.model.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.Blog;
import cn.raxcl.model.dto.BlogViewDTO;
import cn.raxcl.model.dto.BlogVisibilityDTO;
import cn.raxcl.model.vo.ArchiveBlogVO;

import java.util.List;

/**
 * @Description: 博客文章持久层接口
 * @author Raxcl
 * @date 2022-01-06 19:51:38
 */
@Mapper
@Repository
public interface BlogMapper {
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
	List<SearchBlogVO> getSearchBlogListByQueryAndIsPublished(String query);

	/**
	 * 获取所有博客id和title 供评论分类的选择
	 * @return List<Blog>
	 */
	List<Blog> getIdAndTitleList();

	/**
	 * 获取博客list
	 * @return List<NewBlog>
	 */
	List<NewBlogVO> getNewBlogListByIsPublished();

	/**
	 * 按置顶、创建时间排序 分页查询博客简要信息列表
	 * @return List<BlogInfo>
	 */
	List<BlogInfoVO> getBlogInfoListByIsPublished();

	/**
	 * 根据分类name分页查询公开博客列表
	 * @param categoryName 分类name
	 * @return List<BlogInfo>
	 */
	List<BlogInfoVO> getBlogInfoListByCategoryNameAndIsPublished(String categoryName);

	/**
	 * 根据标签name分页查询公开博客列表
	 *
	 * @param tagName 标签name
	 * @return List<BlogInfo>
	 */
	List<BlogInfoVO> getBlogInfoListByTagNameAndIsPublished(String tagName);

	/**
	 * 获取分组后是否发版数据
	 * @return List<String>
	 */
	List<String> getGroupYearMonthByIsPublished();

	/**
	 * 按年月分组归档公开博客 统计公开博客总数
	 * @param yearMonth yearMonth
	 * @return List<ArchiveBlog>
	 */
	List<ArchiveBlogVO> getArchiveBlogListByYearMonthAndIsPublished(String yearMonth);

	/**
	 * 获取站点配置信息、最新推荐博客、分类列表、标签云、随机博客
	 * @param limitNum limitNum
	 * @return List<RandomBlog>
	 */
	List<RandomBlogVO> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend(Integer limitNum);

	/**
	 * 获取博客信息list
	 * @return List<BlogView>
	 */
	List<BlogViewDTO> getBlogViewsList();

	/**
	 * 删除博客
	 * @param id id
	 * @return int
	 */
	int deleteBlogById(Long id);

	/**
	 * 删除博客标签
	 * @param blogId blogId
	 * @return int
	 */
	int deleteBlogTagByBlogId(Long blogId);

	/**
	 * 保存博客
	 * @param blogDTO blogDTO
	 * @return int
	 */
	int saveBlog(BlogDTO blogDTO);

	/**
	 * 保存博客标签
	 * @param blogId blogId
	 * @param tagId tagId
	 * @return int
	 */
	int saveBlogTag(Long blogId, Long tagId);

	/**
	 * 更新博客推荐状态
	 * @param blogId 博客id
	 * @param recommend 是否推荐
	 * @return int
	 */
	int updateBlogRecommendById(Long blogId, Boolean recommend);

	/**
	 * 更新博客可见性状态
	 * @param blogId 博客id
	 * @param bv 博客可见性DTO
	 * @return int
	 */
	int updateBlogVisibilityById(Long blogId, BlogVisibilityDTO bv);

	/**
	 * 更新博客置顶状态
	 * @param blogId 博客id
	 * @param top 是否置顶
	 * @return int
	 */
	int updateBlogTopById(Long blogId, Boolean top);

	/**
	 * 从Redis同步博客文章浏览量到数据库
	 * @param blogId 博客id
	 * @param views views
	 * @return int
	 */
	int updateViews(Long blogId, Integer views);

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
	 * 获取博客是否发版
	 * @param id 博客id
	 * @return BlogDetail
	 */
	BlogDetailVO getBlogByIdAndIsPublished(Long id);

	/**
	 * 获取博客密码
	 * @param blogId 博客id
	 * @return String
	 */
	String getBlogPassword(Long blogId);

	/**
	 * 更新博客
	 * @param blogDTO 博客dto
	 * @return int
	 */
	int updateBlog(BlogDTO blogDTO);

	/**
	 * 博客总计
	 * @return int
	 */
	int countBlog();

	/**
	 * 可发版博客总计
	 * @return int
	 */
	int countBlogByIsPublished();

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
	 *  获取博客分类总计list
	 * @return List<CategoryBlogCount>
	 */
	List<CategoryBlogCountVO> getCategoryBlogCountList();
}
