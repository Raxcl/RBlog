package cn.raxcl.service.impl;

import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.Moment;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.MomentMapper;
import cn.raxcl.service.MomentService;
import cn.raxcl.util.markdown.MarkdownUtils;

import java.util.List;

/**
 * 博客动态业务层实现
 * @author Raxcl
 * @date 2022-01-07 13:20:06
 */
@Service
public class MomentServiceImpl implements MomentService {
	private final MomentMapper momentMapper;
	/**
	 * 每页显示5条动态
	 */
	private static final int PAGE_SIZE = 5;
	/**
	 * 动态列表排序方式
	 */
	private static final String ORDER_BY = "create_time desc";
	/**
	 * 私密动态提示
	 */
	private static final String PRIVATE_MOMENT_CONTENT = "<p>此条为私密动态，仅发布者可见！</p>";

	public MomentServiceImpl(MomentMapper momentMapper) {
		this.momentMapper = momentMapper;
	}

	@Override
	public List<Moment> getMomentList() {
		return momentMapper.getMomentList();
	}

	@Override
	public List<Moment> getMomentVOList(Integer pageNum, boolean adminIdentity) {
		PageMethod.startPage(pageNum, PAGE_SIZE, ORDER_BY);
		List<Moment> moments = momentMapper.getMomentList();
		for (Moment moment : moments) {
			if (adminIdentity || Boolean.TRUE.equals(moment.getPublished())) {
				moment.setContent(MarkdownUtils.markdownToHtmlExtensions(moment.getContent()));
			} else {
				moment.setContent(PRIVATE_MOMENT_CONTENT);
			}
		}
		return moments;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addLikeByMomentId(Long momentId) {
		if (momentMapper.addLikeByMomentId(momentId) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateMomentPublishedById(Long momentId, Boolean published) {
		if (momentMapper.updateMomentPublishedById(momentId, published) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Override
	public Moment getMomentById(Long id) {
		Moment moment = momentMapper.getMomentById(id);
		if (moment == null) {
			throw new NotFoundException("动态不存在");
		}
		return moment;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteMomentById(Long id) {
		if (momentMapper.deleteMomentById(id) != 1) {
			throw new PersistenceException("删除失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveMoment(Moment moment) {
		if (momentMapper.saveMoment(moment) != 1) {
			throw new PersistenceException("动态添加失败");
		}
	}

	@Override
	public void updateMoment(Moment moment) {
		if (momentMapper.updateMoment(moment) != 1) {
			throw new PersistenceException("动态修改失败");
		}
	}
}
