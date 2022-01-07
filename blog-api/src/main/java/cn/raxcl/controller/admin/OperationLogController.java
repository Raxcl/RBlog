package cn.raxcl.controller.admin;

import cn.raxcl.constant.CommonConstant;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.OperationLog;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.OperationLogService;

/**
 * @Description: 操作日志后台管理
 * @author Raxcl
 * @date 2022-01-07 13:26:10
 */
@RestController
@RequestMapping("/admin")
public class OperationLogController {
	private final OperationLogService operationLogService;

	public OperationLogController(OperationLogService operationLogService) {
		this.operationLogService = operationLogService;
	}

	/**
	 * 分页查询操作日志列表
	 * @param date     按操作时间查询
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @return Result
	 */
	@GetMapping("/operationLogs")
	public Result operationLogs(@RequestParam(defaultValue = "") String[] date,
	                            @RequestParam(defaultValue = "1") Integer pageNum,
	                            @RequestParam(defaultValue = "10") Integer pageSize) {
		//TODO 重复代码
		String startDate = null;
		String endDate = null;
		if (date.length == CommonConstant.TWO) {
			startDate = date[0];
			endDate = date[1];
		}
		String orderBy = "create_time desc";
		PageMethod.startPage(pageNum, pageSize, orderBy);
		PageInfo<OperationLog> pageInfo = new PageInfo<>(operationLogService.getOperationLogListByDate(startDate, endDate));
		return Result.success("请求成功", pageInfo);
	}

	/**
	 * 按id删除操作日志
	 *
	 * @param id 日志id
	 * @return Result
	 */
	@DeleteMapping("/operationLog")
	public Result delete(@RequestParam Long id) {
		operationLogService.deleteOperationLogById(id);
		return Result.success("删除成功");
	}
}
