package cn.raxcl.controller.admin;

import cn.raxcl.common.CommonService;
import cn.raxcl.model.temp.PageDTO;
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
 * 操作日志后台管理
 * @author Raxcl
 * @date 2022-01-07 13:26:10
 */
@RestController
@RequestMapping("/admin")
public class OperationLogController {
	private final OperationLogService operationLogService;
	private final CommonService commonService;

	public OperationLogController(OperationLogService operationLogService, CommonService commonService) {
		this.operationLogService = operationLogService;
		this.commonService = commonService;
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

		PageDTO pageDTO = commonService.pageBefore(date);
		PageMethod.startPage(pageNum, pageSize, pageDTO.getOrderBy());
		PageInfo<OperationLog> pageInfo = new PageInfo<>(operationLogService.getOperationLogListByDate(pageDTO.getStartDate(), pageDTO.getEndDate()));
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
