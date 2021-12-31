package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.Category;

import java.util.List;

/**
 * @Description: 博客分类持久层接口
 * @author Raxcl
 * @date 2020-07-29
 */
@Mapper
@Repository
public interface CategoryMapper {
	List<Category> getCategoryList();

	List<Category> getCategoryNameList();

	int saveCategory(Category category);

	Category getCategoryById(Long id);

	Category getCategoryByName(String name);

	int deleteCategoryById(Long id);

	int updateCategory(Category category);
}
