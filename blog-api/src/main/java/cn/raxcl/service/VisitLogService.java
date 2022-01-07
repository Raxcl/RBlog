package cn.raxcl.service;

import org.springframework.scheduling.annotation.Async;
import cn.raxcl.entity.VisitLog;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;

import java.util.List;

public interface VisitLogService {
	List<VisitLog> getVisitLogListByUUIDAndDate(String uuid, String startDate, String endDate);

	List<VisitLogUuidTimeDTO> getUUIDAndCreateTimeByYesterday();

	@Async
	void saveVisitLog(VisitLog log);

	void deleteVisitLogById(Long id);
}
