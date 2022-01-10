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
import cn.raxcl.entity.ExceptionLog;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.ExceptionLogService;

/**
 * @Description: 异常日志后台管理
 * @author Raxcl
 * @date 2022-01-07 15:52:14
 */
@RestController
@RequestMapping("/admin")
public class ExceptionLogController {
	private final ExceptionLogService exceptionLogService;
	private final CommonService commonService;

	public ExceptionLogController(ExceptionLogService exceptionLogService, CommonService commonService) {
		this.exceptionLogService = exceptionLogService;
		this.commonService = commonService;
	}

	/**
	 * 分页查询异常日志列表
	 *
	 * @param date     按操作时间查询
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @return Result
	 */
	@GetMapping("/exceptionLogs")
	public Result exceptionLogs(@RequestParam(defaultValue = "") String[] date,
	                            @RequestParam(defaultValue = "1") Integer pageNum,
	                            @RequestParam(defaultValue = "10") Integer pageSize) {
		PageDTO pageDTO = commonService.pageBefore(date);
		PageMethod.startPage(pageNum, pageSize, pageDTO.getOrderBy());
		PageInfo<ExceptionLog> pageInfo = new PageInfo<>(exceptionLogService.getExceptionLogListByDate(pageDTO.getStartDate(), pageDTO.getEndDate()));
		return Result.success("请求成功", pageInfo);
	}

	/**
	 * 按id删除异常日志
	 *
	 * @param id 日志id
	 * @return Result
	 */
	@DeleteMapping("/exceptionLog")
	public Result delete(@RequestParam Long id) {
		exceptionLogService.deleteExceptionLogById(id);
		return Result.success("删除成功");
	}
}
