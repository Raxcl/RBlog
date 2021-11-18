package cn.raxcl.service;

import org.springframework.scheduling.annotation.Async;
import cn.raxcl.entity.LoginLog;

import java.util.List;

public interface LoginLogService {
	List<LoginLog> getLoginLogListByDate(String startDate, String endDate);

	@Async
	void saveLoginLog(LoginLog log);

	void deleteLoginLogById(Long id);
}
