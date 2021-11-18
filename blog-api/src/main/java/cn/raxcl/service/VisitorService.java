package cn.raxcl.service;

import org.springframework.scheduling.annotation.Async;
import cn.raxcl.entity.Visitor;
import cn.raxcl.model.dto.VisitLogUuidTime;

import java.util.List;

public interface VisitorService {
	List<Visitor> getVisitorListByDate(String startDate, String endDate);

	List<String> getNewVisitorIpSourceByYesterday();

	boolean hasUUID(String uuid);

	@Async
	void saveVisitor(Visitor visitor);

	void updatePVAndLastTimeByUUID(VisitLogUuidTime dto);

	void deleteVisitor(Long id, String uuid);
}
