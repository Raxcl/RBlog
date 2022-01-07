package cn.raxcl.service;

import cn.raxcl.entity.Visitor;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 15:41:45
 */
public interface VisitorService {
	/**
	 * 查询访客
	 * @param startDate startDate
	 * @param endDate endDate
	 * @return List<Visitor>
	 */
	List<Visitor> getVisitorListByDate(String startDate, String endDate);

	/**
	 * 查询昨天的所有新增访客的ip来源
	 * @return List<String>
	 */
	List<String> getNewVisitorIpSourceByYesterday();

	/**
	 * 查询是否存在某个uuid
	 * @param uuid uuid
	 * @return boolean
	 */
	boolean hasUuid(String uuid);


	/**
	 * 添加访客
	 * @param visitor visitor
	 */
	void saveVisitor(Visitor visitor);

	/**
	 * 更新访客pv和最后访问时间
	 * @param dto dto
	 */
	void updatePvAndLastTimeByUuid(VisitLogUuidTimeDTO dto);

	/**
	 * 删除访客
	 * @param id id
	 * @param uuid uuid
	 */
	void deleteVisitor(Long id, String uuid);
}
