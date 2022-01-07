package cn.raxcl.service;

import cn.raxcl.entity.Category;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 09:09:16
 */
public interface CategoryService {
	/**
	 * 获取博客分类列表
	 * @return List<Category>
	 */
	List<Category> getCategoryList();

	/**
	 * 获取分类名list
	 * @return List<Category>
	 */
	List<Category> getCategoryNameList();

	/**
	 * 保存分类
	 * @param category category
	 */
	void saveCategory(Category category);

	/**
	 * 根据id获取分类信息
	 * @param id id
	 * @return Category
	 */
	Category getCategoryById(Long id);

	/**
	 * 根据名称获取分类信息
	 * @param name name
	 * @return Category
	 */
	Category getCategoryByName(String name);

	/**
	 * 根据id删除分类信息
	 * @param id id
	 */
	void deleteCategoryById(Long id);

	/**
	 * 更新分类信息
	 * @param category category
	 */
	void updateCategory(Category category);
}
