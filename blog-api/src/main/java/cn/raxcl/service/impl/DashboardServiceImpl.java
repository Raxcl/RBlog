package cn.raxcl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.raxcl.entity.Category;
import cn.raxcl.entity.CityVisitor;
import cn.raxcl.entity.Tag;
import cn.raxcl.entity.VisitRecord;
import cn.raxcl.mapper.BlogMapper;
import cn.raxcl.mapper.CategoryMapper;
import cn.raxcl.mapper.CityVisitorMapper;
import cn.raxcl.mapper.CommentMapper;
import cn.raxcl.mapper.TagMapper;
import cn.raxcl.mapper.VisitLogMapper;
import cn.raxcl.mapper.VisitRecordMapper;
import cn.raxcl.model.vo.CategoryBlogCount;
import cn.raxcl.model.vo.TagBlogCount;
import cn.raxcl.service.DashboardService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 仪表盘业务层实现
 * @author Raxcl
 * @date 2020-10-08
 */
@Service
public class DashboardServiceImpl implements DashboardService {
	@Autowired
	BlogMapper blogMapper;
	@Autowired
	CommentMapper commentMapper;
	@Autowired
	CategoryMapper categoryMapper;
	@Autowired
	TagMapper tagMapper;
	@Autowired
	VisitLogMapper visitLogMapper;
	@Autowired
	VisitRecordMapper visitRecordMapper;
	@Autowired
	CityVisitorMapper cityVisitorMapper;
	//查询最近30天的记录
	private static final int visitRecordLimitNum = 30;

	@Override
	public int countVisitLogByToday() {
		return visitLogMapper.countVisitLogByToday();
	}

	@Override
	public int getBlogCount() {
		return blogMapper.countBlog();
	}

	@Override
	public int getCommentCount() {
		return commentMapper.countComment();
	}

	@Override
	public Map<String, List> getCategoryBlogCountMap() {
		//查询分类id对应的博客数量
		List<CategoryBlogCount> categoryBlogCountList = blogMapper.getCategoryBlogCountList();
		//查询所有分类的id和名称
		List<Category> categoryList = categoryMapper.getCategoryList();
		//所有分类名称的List
		List<String> legend = new ArrayList<>();
		for (Category category : categoryList) {
			legend.add(category.getName());
		}
		//分类对应的博客数量List
		List<CategoryBlogCount> series = new ArrayList<>();
		if (categoryBlogCountList.size() == categoryList.size()) {
			Map<Long, String> m = new HashMap<>();
			for (Category c : categoryList) {
				m.put(c.getId(), c.getName());
			}
			for (CategoryBlogCount c : categoryBlogCountList) {
				c.setName(m.get(c.getId()));
				series.add(c);
			}
		} else {
			Map<Long, Integer> m = new HashMap<>();
			for (CategoryBlogCount c : categoryBlogCountList) {
				m.put(c.getId(), c.getValue());
			}
			for (Category c : categoryList) {
				CategoryBlogCount categoryBlogCount = new CategoryBlogCount();
				categoryBlogCount.setName(c.getName());
				Integer count = m.get(c.getId());
				if (count == null) {
					categoryBlogCount.setValue(0);
				} else {
					categoryBlogCount.setValue(count);
				}
				series.add(categoryBlogCount);
			}
		}
		Map<String, List> map = new HashMap<>();
		map.put("legend", legend);
		map.put("series", series);
		return map;
	}

	@Override
	public Map<String, List> getTagBlogCountMap() {
		//查询标签id对应的博客数量
		List<TagBlogCount> tagBlogCountList = tagMapper.getTagBlogCount();
		//查询所有标签的id和名称
		List<Tag> tagList = tagMapper.getTagList();
		//所有标签名称的List
		List<String> legend = new ArrayList<>();
		for (Tag tag : tagList) {
			legend.add(tag.getName());
		}
		//标签对应的博客数量List
		List<TagBlogCount> series = new ArrayList<>();
		if (tagBlogCountList.size() == tagList.size()) {
			Map<Long, String> m = new HashMap<>();
			for (Tag t : tagList) {
				m.put(t.getId(), t.getName());
			}
			for (TagBlogCount t : tagBlogCountList) {
				t.setName(m.get(t.getId()));
				series.add(t);
			}
		} else {
			Map<Long, Integer> m = new HashMap<>();
			for (TagBlogCount t : tagBlogCountList) {
				m.put(t.getId(), t.getValue());
			}
			for (Tag t : tagList) {
				TagBlogCount tagBlogCount = new TagBlogCount();
				tagBlogCount.setName(t.getName());
				Integer count = m.get(t.getId());
				if (count == null) {
					tagBlogCount.setValue(0);
				} else {
					tagBlogCount.setValue(count);
				}
				series.add(tagBlogCount);
			}
		}
		Map<String, List> map = new HashMap<>();
		map.put("legend", legend);
		map.put("series", series);
		return map;
	}

	@Override
	public Map<String, List> getVisitRecordMap() {
		List<VisitRecord> visitRecordList = visitRecordMapper.getVisitRecordListByLimit(visitRecordLimitNum);
		List<String> date = new ArrayList<>(visitRecordList.size());
		List<Integer> pv = new ArrayList<>(visitRecordList.size());
		List<Integer> uv = new ArrayList<>(visitRecordList.size());
		for (int i = visitRecordList.size() - 1; i >= 0; i--) {
			VisitRecord visitRecord = visitRecordList.get(i);
			date.add(visitRecord.getDate());
			pv.add(visitRecord.getPv());
			uv.add(visitRecord.getUv());
		}
		Map<String, List> map = new HashMap<>();
		map.put("date", date);
		map.put("pv", pv);
		map.put("uv", uv);
		return map;
	}

	@Override
	public List<CityVisitor> getCityVisitorList() {
		return cityVisitorMapper.getCityVisitorList();
	}
}
