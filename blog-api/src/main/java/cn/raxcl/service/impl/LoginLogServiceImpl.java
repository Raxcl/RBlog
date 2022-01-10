package cn.raxcl.service.impl;

import cn.raxcl.common.LogService;
import cn.raxcl.model.temp.LogDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.LoginLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.LoginLogMapper;
import cn.raxcl.service.LoginLogService;

import java.util.List;

/**
 * @Description: 登录日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 12:33:13
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {
	private final LoginLogMapper loginLogMapper;
	private final LogService logService;


	public LoginLogServiceImpl(LoginLogMapper loginLogMapper, LogService logService) {
		this.loginLogMapper = loginLogMapper;
		this.logService = logService;
	}

	@Override
	public List<LoginLog> getLoginLogListByDate(String startDate, String endDate) {
		return loginLogMapper.getLoginLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveLoginLog(LoginLog log) {
		LogDTO logDTO = logService.saveLog(log.getIp(), log.getUserAgent());
		BeanUtils.copyProperties(logDTO, log);
		if (loginLogMapper.saveLoginLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteLoginLogById(Long id) {
		if (loginLogMapper.deleteLoginLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
