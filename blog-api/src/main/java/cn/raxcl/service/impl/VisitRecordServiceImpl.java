package cn.raxcl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.raxcl.entity.VisitRecord;
import cn.raxcl.mapper.VisitRecordMapper;
import cn.raxcl.service.VisitRecordService;

/**
 * @Description: 访问记录业务层实现
 * @author Raxcl
 * @date 2021-02-23
 */
@Service
public class VisitRecordServiceImpl implements VisitRecordService {
	@Autowired
	VisitRecordMapper visitRecordMapper;

	@Override
	public void saveVisitRecord(VisitRecord visitRecord) {
		visitRecordMapper.saveVisitRecord(visitRecord);
	}
}
