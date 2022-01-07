package cn.raxcl.service.impl;

import org.springframework.stereotype.Service;
import cn.raxcl.entity.VisitRecord;
import cn.raxcl.mapper.VisitRecordMapper;
import cn.raxcl.service.VisitRecordService;

/**
 * @Description: 访问记录业务层实现
 * @author Raxcl
 * @date 2022-01-07 16:01:50
 */
@Service
public class VisitRecordServiceImpl implements VisitRecordService {
	private final VisitRecordMapper visitRecordMapper;

	public VisitRecordServiceImpl(VisitRecordMapper visitRecordMapper) {
		this.visitRecordMapper = visitRecordMapper;
	}

	@Override
	public void saveVisitRecord(VisitRecord visitRecord) {
		visitRecordMapper.saveVisitRecord(visitRecord);
	}
}
