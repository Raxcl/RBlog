package cn.raxcl.service;

import cn.raxcl.entity.Moment;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 13:14:42
 */
public interface MomentService {
	/**
	 * 查询动态列表
	 * @return List<Moment>
	 */
	List<Moment> getMomentList();

	/**
	 * 查询动态vo列表
	 * @param pageNum pageNum
	 * @param adminIdentity adminIdentity
	 * @return List<Moment>
	 */
	List<Moment> getMomentVOList(Integer pageNum, boolean adminIdentity);

	/**
	 * 给动态点赞
	 * @param momentId momentId
	 */
	void addLikeByMomentId(Long momentId);

	/**
	 * 更新动态公开状态
	 * @param momentId 动态id
	 * @param published 是否公开
	 */
	void updateMomentPublishedById(Long momentId, Boolean published);

	/**
	 * 根据id查询动态
	 * @param id 动态id
	 * @return Moment
	 */
	Moment getMomentById(Long id);

	/**
	 * 删除动态
	 * @param id 动态id
	 */
	void deleteMomentById(Long id);

	/**
	 * 发布动态
	 * @param moment 动态实体
	 */
	void saveMoment(Moment moment);

	/**
	 * 更新动态
	 * @param moment 动态实体
	 */
	void updateMoment(Moment moment);
}
