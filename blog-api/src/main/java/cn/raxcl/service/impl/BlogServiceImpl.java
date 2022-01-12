package cn.raxcl.service.impl;

import cn.raxcl.aspect.AopProxy;
import cn.raxcl.constant.CommonConstant;
import cn.raxcl.constant.RedisKeyConstant;
import cn.raxcl.util.common.Result;
import cn.raxcl.entity.Blog;
import cn.raxcl.entity.Category;
import cn.raxcl.entity.Tag;
import cn.raxcl.entity.User;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.BlogMapper;
import cn.raxcl.model.dto.BlogDTO;
import cn.raxcl.model.dto.BlogViewDTO;
import cn.raxcl.model.dto.BlogVisibilityDTO;
import cn.raxcl.model.vo.*;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.CategoryService;
import cn.raxcl.service.RedisService;
import cn.raxcl.service.TagService;
import cn.raxcl.util.JacksonUtils;
import cn.raxcl.util.JwtUtils;
import cn.raxcl.util.StringUtils;
import cn.raxcl.util.markdown.MarkdownUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Raxcl
 * @Description: 博客文章业务层实现
 * @date 2022-01-07 15:53:39
 */
@Service
public class BlogServiceImpl implements BlogService, AopProxy<BlogServiceImpl> {
    @Value("${token.secretKey}")
    private String secretKey;

    //随机博客显示5条
    private static final int RANDOM_BLOG_LIMIT_NUM = 5;
    //最新推荐博客显示3条
    private static final int NEW_BLOG_PAGE_SIZE = 3;
    //每页显示5条博客简介
    private static final int PAGE_SIZE = 5;
    //博客简介列表排序方式
    private static final String ORDER_BY = "is_top desc, create_time desc";
    //私密博客提示
    private static final String PRIVATE_BLOG_DESCRIPTION = "此文章受密码保护！";

    private final BlogMapper blogMapper;
    private final TagService tagService;
    private final RedisService redisService;
    private final CategoryService categoryService;
    private final UserServiceImpl userService;

    public BlogServiceImpl(BlogMapper blogMapper, TagService tagService, RedisService redisService, CategoryService categoryService, UserServiceImpl userService) {
        this.blogMapper = blogMapper;
        this.tagService = tagService;
        this.redisService = redisService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /**
     * 项目启动时，保存所有博客的浏览量到Redis
     */
    @PostConstruct
    private void saveBlogViewsToRedis() {
        String redisKey = RedisKeyConstant.BLOG_VIEWS_MAP;
        //Redis中没有存储博客浏览量的Hash
        if (!redisService.hasKey(redisKey)) {
            //从数据库中读取并存入Redis
            Map<Long, Integer> blogViewsMap = getBlogViewsMap();
            redisService.saveMapToHash(redisKey, blogViewsMap);
        }
    }

    @Override
    public List<Blog> getListByTitleAndCategoryId(String title, Integer categoryId) {
        return blogMapper.getListByTitleAndCategoryId(title, categoryId);
    }

    @Override
    public List<SearchBlogVO> getSearchBlogListByQueryAndIsPublished(String query) {
        List<SearchBlogVO> searchBlogVOS = blogMapper.getSearchBlogListByQueryAndIsPublished(query);
        for (SearchBlogVO searchBlogVO : searchBlogVOS) {
            String content = searchBlogVO.getContent();
            int contentLength = content.length();
            int index = content.indexOf(query) - 10;
            index = Math.max(index, 0);
            int end = index + 21;//以关键字字符串为中心返回21个字
            end = Math.min(end, contentLength - 1);
            searchBlogVO.setContent(content.substring(index, end));
        }
        return searchBlogVOS;
    }

    @Override
    public List<Blog> getIdAndTitleList() {
        return blogMapper.getIdAndTitleList();
    }

    @Override
    public List<NewBlogVO> getNewBlogListByIsPublished() {
        String redisKey = RedisKeyConstant.NEW_BLOG_LIST;
        List<NewBlogVO> newBlogVOListFromRedis = redisService.getListByValue(redisKey);
        if (newBlogVOListFromRedis != null) {
            return newBlogVOListFromRedis;
        }
        PageMethod.startPage(1, NEW_BLOG_PAGE_SIZE);
        List<NewBlogVO> newBlogVOList = blogMapper.getNewBlogListByIsPublished();
        for (NewBlogVO newBlogVO : newBlogVOList) {
            if (!"".equals(newBlogVO.getPassword())) {
                newBlogVO.setPrivacy(true);
                newBlogVO.setPassword("");
            } else {
                newBlogVO.setPrivacy(false);
            }
        }
        redisService.saveListToValue(redisKey, newBlogVOList);
        return newBlogVOList;
    }

    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByIsPublished(Integer pageNum) {
        String redisKey = RedisKeyConstant.HOME_BLOG_INFO_LIST;
        //redis已有当前页缓存
        PageResultVO<BlogInfoVO> pageResultVOFromRedis = redisService.getBlogInfoPageResultByHash(redisKey, pageNum);
        if (pageResultVOFromRedis != null) {
            setBlogViewsFromRedisToPageResult(pageResultVOFromRedis);
            return pageResultVOFromRedis;
        }
        //redis没有缓存，从数据库查询，并添加缓存
        PageMethod.startPage(pageNum, PAGE_SIZE, ORDER_BY);
        List<BlogInfoVO> blogInfoVOS = processBlogInfosPassword(blogMapper.getBlogInfoListByIsPublished());
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfoVOS);
        PageResultVO<BlogInfoVO> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResultVO);
        //添加首页缓存
        redisService.saveKvToHash(redisKey, pageNum, pageResultVO);
        return pageResultVO;
    }

    /**
     * 将pageResult中博客对象的浏览量设置为Redis中的最新值
     *
     * @param pageResultVO pageResult
     */
    private void setBlogViewsFromRedisToPageResult(PageResultVO<BlogInfoVO> pageResultVO) {
        String redisKey = RedisKeyConstant.BLOG_VIEWS_MAP;
        List<BlogInfoVO> blogInfoVOS = pageResultVO.getList();
        for (int i = 0; i < blogInfoVOS.size(); i++) {
            BlogInfoVO blogInfoVO = JacksonUtils.convertValue(blogInfoVOS.get(i), BlogInfoVO.class);
            Long blogId = blogInfoVO.getId();
            int view = (int) redisService.getValueByHashKey(redisKey, blogId);
            blogInfoVO.setViews(view);
            blogInfoVOS.set(i, blogInfoVO);
        }
    }

    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum) {
        PageMethod.startPage(pageNum, PAGE_SIZE, ORDER_BY);
        List<BlogInfoVO> blogInfoVOS = processBlogInfosPassword(blogMapper.getBlogInfoListByCategoryNameAndIsPublished(categoryName));
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfoVOS);
        PageResultVO<BlogInfoVO> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResultVO);
        return pageResultVO;
    }

    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum) {
        PageMethod.startPage(pageNum, PAGE_SIZE, ORDER_BY);
        List<BlogInfoVO> blogInfoVOS = processBlogInfosPassword(blogMapper.getBlogInfoListByTagNameAndIsPublished(tagName));
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfoVOS);
        PageResultVO<BlogInfoVO> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResultVO);
        return pageResultVO;
    }

    private List<BlogInfoVO> processBlogInfosPassword(List<BlogInfoVO> blogInfoVOS) {
        for (BlogInfoVO blogInfoVO : blogInfoVOS) {
            if (!"".equals(blogInfoVO.getPassword())) {
                blogInfoVO.setPrivacy(true);
                blogInfoVO.setPassword("");
                blogInfoVO.setDescription(PRIVATE_BLOG_DESCRIPTION);
            } else {
                blogInfoVO.setPrivacy(false);
                blogInfoVO.setDescription(MarkdownUtils.markdownToHtmlExtensions(blogInfoVO.getDescription()));
            }
            blogInfoVO.setTags(tagService.getTagListByBlogId(blogInfoVO.getId()));
        }
        return blogInfoVOS;
    }

    @Override
    public Map<String, Object> getArchiveBlogAndCountByIsPublished() {
        String redisKey = RedisKeyConstant.ARCHIVE_BLOG_MAP;
        Map<String, Object> mapFromRedis = redisService.getMapByValue(redisKey);
        if (mapFromRedis != null) {
            return mapFromRedis;
        }
        Map<String, Object> map = new HashMap<>(16);
        List<String> groupYearMonth = blogMapper.getGroupYearMonthByIsPublished();
        Map<String, List<ArchiveBlogVO>> archiveBlogMap = new LinkedHashMap<>();
        for (String s : groupYearMonth) {
            List<ArchiveBlogVO> archiveBlogVOS = blogMapper.getArchiveBlogListByYearMonthAndIsPublished(s);
            for (ArchiveBlogVO archiveBlogVO : archiveBlogVOS) {
                if (!"".equals(archiveBlogVO.getPassword())) {
                    archiveBlogVO.setPrivacy(true);
                    archiveBlogVO.setPassword("");
                } else {
                    archiveBlogVO.setPrivacy(false);
                }
            }
            archiveBlogMap.put(s, archiveBlogVOS);
        }
        Integer count = countBlogByIsPublished();
        map.put("blogMap", archiveBlogMap);
        map.put("count", count);
        redisService.saveMapToValue(redisKey, map);
        return map;
    }

    @Override
    public List<RandomBlogVO> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend() {
        List<RandomBlogVO> randomBlogVOS = blogMapper.getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend(RANDOM_BLOG_LIMIT_NUM);
        for (RandomBlogVO randomBlogVO : randomBlogVOS) {
            if (!"".equals(randomBlogVO.getPassword())) {
                randomBlogVO.setPrivacy(true);
                randomBlogVO.setPassword("");
            } else {
                randomBlogVO.setPrivacy(false);
            }
        }
        return randomBlogVOS;
    }

    private Map<Long, Integer> getBlogViewsMap() {
        List<BlogViewDTO> blogViewDTOList = blogMapper.getBlogViewsList();
        Map<Long, Integer> blogViewsMap = new HashMap<>(16);
        for (BlogViewDTO blogViewDTO : blogViewDTOList) {
            blogViewsMap.put(blogViewDTO.getId(), blogViewDTO.getViews());
        }
        return blogViewsMap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBlogById(Long id) {
        if (blogMapper.deleteBlogById(id) != 1) {
            throw new NotFoundException("该博客不存在");
        }
        deleteBlogRedisCache();
        redisService.deleteByHashKey(RedisKeyConstant.BLOG_VIEWS_MAP, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBlogTagByBlogId(Long blogId) {
        if (blogMapper.deleteBlogTagByBlogId(blogId) == 0) {
            throw new PersistenceException("维护博客标签关联表失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBlog(BlogDTO blogDTO) {
        if (blogMapper.saveBlog(blogDTO) != 1) {
            throw new PersistenceException("添加博客失败");
        }
        redisService.saveKvToHash(RedisKeyConstant.BLOG_VIEWS_MAP, blogDTO.getId(), 0);
        deleteBlogRedisCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBlogTag(Long blogId, Long tagId) {
        if (blogMapper.saveBlogTag(blogId, tagId) != 1) {
            throw new PersistenceException("维护博客标签关联表失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogRecommendById(Long blogId, Boolean recommend) {
        if (blogMapper.updateBlogRecommendById(blogId, recommend) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogVisibilityById(Long blogId, BlogVisibilityDTO blogVisibilityDTO) {
        if (blogMapper.updateBlogVisibilityById(blogId, blogVisibilityDTO) != 1) {
            throw new PersistenceException("操作失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstant.HOME_BLOG_INFO_LIST);
        redisService.deleteCacheByKey(RedisKeyConstant.NEW_BLOG_LIST);
        redisService.deleteCacheByKey(RedisKeyConstant.ARCHIVE_BLOG_MAP);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogTopById(Long blogId, Boolean top) {
        if (blogMapper.updateBlogTopById(blogId, top) != 1) {
            throw new PersistenceException("操作失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstant.HOME_BLOG_INFO_LIST);
    }

    private void updateViewsToRedis(Long blogId) {
        redisService.incrementByHashKey(RedisKeyConstant.BLOG_VIEWS_MAP, blogId, 1);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateViews(Long blogId, Integer views) {
        if (blogMapper.updateViews(blogId, views) != 1) {
            throw new PersistenceException("更新失败");
        }
    }

    @Override
    public Blog getBlogById(Long id) {
        Blog blog = blogMapper.getBlogById(id);
        if (blog == null) {
            throw new NotFoundException("博客不存在");
        }
        //将浏览量设置为Redis中的最新值
        int view = (int) redisService.getValueByHashKey(RedisKeyConstant.BLOG_VIEWS_MAP, blog.getId());
        blog.setViews(view);
        return blog;
    }

    @Override
    public String getTitleByBlogId(Long id) {
        return blogMapper.getTitleByBlogId(id);
    }

    private BlogDetailVO getBlogByIdAndIsPublished(Long id) {
        BlogDetailVO blog = blogMapper.getBlogByIdAndIsPublished(id);
        if (blog == null) {
            throw new NotFoundException("该博客不存在");
        }
        blog.setContent(MarkdownUtils.markdownToHtmlExtensions(blog.getContent()));
        //将浏览量设置为Redis中的最新值
        int view = (int) redisService.getValueByHashKey(RedisKeyConstant.BLOG_VIEWS_MAP, blog.getId());
        blog.setViews(view);
        return blog;
    }

    @Override
    public String getBlogPassword(Long blogId) {
        return blogMapper.getBlogPassword(blogId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlog(BlogDTO blogDTO) {
        if (blogMapper.updateBlog(blogDTO) != 1) {
            throw new PersistenceException("更新博客失败");
        }
        deleteBlogRedisCache();
        redisService.saveKvToHash(RedisKeyConstant.BLOG_VIEWS_MAP, blogDTO.getId(), blogDTO.getViews());
    }

    private int countBlogByIsPublished() {
        return blogMapper.countBlogByIsPublished();
    }

    @Override
    public int countBlogByCategoryId(Long categoryId) {
        return blogMapper.countBlogByCategoryId(categoryId);
    }

    @Override
    public int countBlogByTagId(Long tagId) {
        return blogMapper.countBlogByTagId(tagId);
    }

    @Override
    public Boolean getCommentEnabledByBlogId(Long blogId) {
        return blogMapper.getCommentEnabledByBlogId(blogId);
    }

    @Override
    public Boolean getPublishedByBlogId(Long blogId) {
        return blogMapper.getPublishedByBlogId(blogId);
    }

    @Override
    public Result getResult(BlogDTO blogDTO, String type) {
        //验证普通字段
        if (StringUtils.isEmpty(blogDTO.getTitle(), blogDTO.getFirstPicture(), blogDTO.getContent(), blogDTO.getDescription())
                || blogDTO.getWords() == null || blogDTO.getWords() < 0) {
            throw new NotFoundException("参数有误");
        }
        //处理分类
        categoryCheck(blogDTO);
        //处理标签
        List<Tag> tags = tagCheck(blogDTO);
        Date date = new Date();
        if (blogDTO.getReadTime() == null || blogDTO.getReadTime() < 0) {
            //粗略计算阅读时长
            blogDTO.setReadTime((int) Math.round(blogDTO.getWords() / 200.0));
        }
        if (blogDTO.getViews() == null || blogDTO.getViews() < 0) {
            blogDTO.setViews(0);
        }
        if (CommonConstant.SAVE.equals(type)) {
            blogDTO.setCreateTime(date);
            blogDTO.setUpdateTime(date);
            User user = new User();
            //个人博客默认只有一个作者
            user.setId((long) 1);
            blogDTO.setUser(user);
            self().saveBlog(blogDTO);
            //关联博客和标签(维护 blog_tag 表)
            for (Tag t : tags) {
                self().saveBlogTag(blogDTO.getId(), t.getId());
            }
            return Result.success("添加成功");
        } else {
            blogDTO.setUpdateTime(date);
            self().updateBlog(blogDTO);
            //关联博客和标签(维护 blog_tag 表)
            self().deleteBlogTagByBlogId(blogDTO.getId());
            for (Tag t : tags) {
                self().saveBlogTag(blogDTO.getId(), t.getId());
            }
            return Result.success("更新成功");
        }
    }

    @Override
    public BlogDetailVO getBlog(Long id, String jwt) {
        BlogDetailVO blog = getBlogByIdAndIsPublished(id);
        //对密码保护的文章校验Token
        if (!"".equals(blog.getPassword())) {
            if (!JwtUtils.judgeTokenIsExist(jwt)) {
                throw new NotFoundException("此文章受密码保护，请验证密码！");
            }
                String subject;
                try {
                    subject = JwtUtils.getTokenBody(jwt, secretKey).getSubject();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NotFoundException("Token已失效，请重新验证密码！");
                }
                if (subject.startsWith("admin:")) {//博主身份Token
                    String username = subject.replace("admin:", "");
                    User admin = (User) userService.loadUserByUsername(username);
                    if (admin == null) {
                        throw new NotFoundException("博主身份Token已失效，请重新登录！");
                    }
                } else {//经密码验证后的Token
                    Long tokenBlogId = Long.parseLong(subject);
                    //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
                    if (!tokenBlogId.equals(id)) {
                        throw new NotFoundException("Token不匹配，请重新验证密码！");
                    }
                }
            blog.setPassword("");
        }
        updateViewsToRedis(id);
        return blog;
    }

    private List<Tag> tagCheck(BlogDTO blogDTO) {
        List<Object> tagList = blogDTO.getTagList();
        List<Tag> tags = new ArrayList<>();
        for (Object t : tagList) {
            if (t instanceof Integer) {//选择了已存在的标签
                Tag tag = tagService.getTagById(((Integer) t).longValue());
                tags.add(tag);
            } else if (t instanceof String) {//添加新标签
                //查询标签是否已存在
                Tag tag1 = tagService.getTagByName((String) t);
                if (tag1 != null) {
                    throw new NotFoundException("不可添加已存在的标签");
                }
                Tag tag = new Tag();
                tag.setName((String) t);
                tagService.saveTag(tag);
                tags.add(tag);
            } else {
                throw new NotFoundException("标签不正确");
            }
        }
        return tags;
    }

    private void categoryCheck(BlogDTO blogDTO) {
        Object cate = blogDTO.getCate();
        if (cate == null) {
            throw new NotFoundException("分类不能为空");
        }
        if (cate instanceof Integer) {//选择了已存在的分类
            Category c = categoryService.getCategoryById(((Integer) cate).longValue());
            blogDTO.setCategory(c);
        } else if (cate instanceof String) {//添加新分类
            //查询分类是否已存在
            Category category = categoryService.getCategoryByName((String) cate);
            if (category != null) {
                throw new NotFoundException("不可添加已存在的分类");
            }
            Category c = new Category();
            c.setName((String) cate);
            categoryService.saveCategory(c);
            blogDTO.setCategory(c);
        } else {
            throw new NotFoundException("分类不正确");
        }
    }

    /**
     * 删除首页缓存、最新推荐缓存、归档页面缓存、博客浏览量缓存
     */
    private void deleteBlogRedisCache() {
        redisService.deleteCacheByKey(RedisKeyConstant.HOME_BLOG_INFO_LIST);
        redisService.deleteCacheByKey(RedisKeyConstant.NEW_BLOG_LIST);
        redisService.deleteCacheByKey(RedisKeyConstant.ARCHIVE_BLOG_MAP);
    }
}
