package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.About;

import java.util.List;

/**
 * @Description: 关于我持久层接口
 * @author Raxcl
 * @date 2022-01-07 08:58:08
 */
@Mapper
@Repository
public interface AboutMapper {
	/**
	 * 获取关于我list
	 * @return List<About>
	 */
	List<About> getList();

	/**
	 * 更新关于我页面
	 * @param nameEn nameEn
	 * @param value value
	 * @return int
	 */
	int updateAbout(String nameEn, String value);

	/**
	 * 判断评论是否已关闭
	 * @return String
	 */
	String getAboutCommentEnabled();
}
