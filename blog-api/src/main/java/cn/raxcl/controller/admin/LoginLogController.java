package cn.raxcl.controller.admin;

import cn.raxcl.constant.CommonConstant;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.LoginLog;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.LoginLogService;

/**
 * @Description: 登录日志后台管理
 * @author Raxcl
 * @date 2022-01-07 12:29:13
 */
@RestController
@RequestMapping("/admin")
public class LoginLogController {
	private final LoginLogService loginLogService;

	public LoginLogController(LoginLogService loginLogService) {
		this.loginLogService = loginLogService;
	}

	/**
	 * 分页查询登录日志列表
	 *
	 * @param date     按操作时间查询
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @return Result
	 */
	@GetMapping("/loginLogs")
	public Result loginLogs(@RequestParam(defaultValue = "") String[] date,
	                        @RequestParam(defaultValue = "1") Integer pageNum,
	                        @RequestParam(defaultValue = "10") Integer pageSize) {
		String startDate = null;
		String endDate = null;
		if (date.length == CommonConstant.TWO) {
			startDate = date[0];
			endDate = date[1];
		}
		String orderBy = "create_time desc";
		PageMethod.startPage(pageNum, pageSize, orderBy);
		PageInfo<LoginLog> pageInfo = new PageInfo<>(loginLogService.getLoginLogListByDate(startDate, endDate));
		return Result.success("请求成功", pageInfo);
	}

	/**
	 * 按id删除登录日志
	 *
	 * @param id 日志id
	 * @return Result
	 */
	@DeleteMapping("/loginLog")
	public Result delete(@RequestParam Long id) {
		loginLogService.deleteLoginLogById(id);
		return Result.success("删除成功");
	}
}
