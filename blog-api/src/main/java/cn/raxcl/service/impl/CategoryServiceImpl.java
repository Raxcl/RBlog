package cn.raxcl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.constant.RedisKeyConstant;
import cn.raxcl.entity.Category;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.CategoryMapper;
import cn.raxcl.service.CategoryService;
import cn.raxcl.service.RedisService;

import java.util.List;

/**
 * 博客分类业务层实现
 * @author Raxcl
 * @date 2022-01-07 09:09:27
 */
@Service
public class CategoryServiceImpl implements CategoryService {
	private final CategoryMapper categoryMapper;
	private final RedisService redisService;

	public CategoryServiceImpl(CategoryMapper categoryMapper, RedisService redisService) {
		this.categoryMapper = categoryMapper;
		this.redisService = redisService;
	}

	@Override
	public List<Category> getCategoryList() {
		return categoryMapper.getCategoryList();
	}

	@Override
	public List<Category> getCategoryNameList() {
		String redisKey = RedisKeyConstant.CATEGORY_NAME_LIST;
		List<Category> categoryListFromRedis = redisService.getListByValue(redisKey);
		if (categoryListFromRedis != null) {
			return categoryListFromRedis;
		}
		List<Category> categoryList = categoryMapper.getCategoryNameList();
		redisService.saveListToValue(redisKey, categoryList);
		return categoryList;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveCategory(Category category) {
		if (categoryMapper.saveCategory(category) != 1) {
			throw new PersistenceException("分类添加失败");
		}
		redisService.deleteCacheByKey(RedisKeyConstant.CATEGORY_NAME_LIST);
	}

	@Override
	public Category getCategoryById(Long id) {
		Category category = categoryMapper.getCategoryById(id);
		if (category == null) {
			throw new NotFoundException("分类不存在");
		}
		return category;
	}

	@Override
	public Category getCategoryByName(String name) {
		return categoryMapper.getCategoryByName(name);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteCategoryById(Long id) {
		if (categoryMapper.deleteCategoryById(id) != 1) {
			throw new PersistenceException("删除分类失败");
		}
		redisService.deleteCacheByKey(RedisKeyConstant.CATEGORY_NAME_LIST);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateCategory(Category category) {
		if (categoryMapper.updateCategory(category) != 1) {
			throw new PersistenceException("分类更新失败");
		}
		redisService.deleteCacheByKey(RedisKeyConstant.CATEGORY_NAME_LIST);
		//修改了分类名，可能有首页文章关联了分类，也要更新首页缓存
		redisService.deleteCacheByKey(RedisKeyConstant.HOME_BLOG_INFO_LIST);
	}
}
