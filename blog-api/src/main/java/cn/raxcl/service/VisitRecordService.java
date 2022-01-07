package cn.raxcl.service;

import cn.raxcl.entity.VisitRecord;

/**
 * @author c-long.chan
 * @date 2022-01-07 16:01:14
 */
public interface VisitRecordService {
	/**
	 * 添加访问记录
	 * @param visitRecord visitRecord
	 */
	void saveVisitRecord(VisitRecord visitRecord);
}
