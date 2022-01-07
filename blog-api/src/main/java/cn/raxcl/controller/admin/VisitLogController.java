package cn.raxcl.controller.admin;

import cn.raxcl.constant.CommonConstant;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.VisitLog;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.VisitLogService;

/**
 * @Description: 访问日志后台管理
 * @author Raxcl
 * @date 2022-01-07 15:17:23
 */
@RestController
@RequestMapping("/admin")
public class VisitLogController {
	private final VisitLogService visitLogService;

	public VisitLogController(VisitLogService visitLogService) {
		this.visitLogService = visitLogService;
	}

	/**
	 * 分页查询访问日志列表
	 *
	 * @param uuid     按访客标识码模糊查询
	 * @param date     按访问时间查询
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @return Result
	 */
	@GetMapping("/visitLogs")
	public Result visitLogs(@RequestParam(defaultValue = "") String uuid,
	                        @RequestParam(defaultValue = "") String[] date,
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
		PageInfo<VisitLog> pageInfo = new PageInfo<>(visitLogService.getVisitLogListByUuidAndDate(StringUtils.trim(uuid), startDate, endDate));
		return Result.success("请求成功", pageInfo);
	}

	/**
	 * 按id删除访问日志
	 *
	 * @param id 日志id
	 * @return Result
	 */
	@DeleteMapping("/visitLog")
	public Result delete(@RequestParam Long id) {
		visitLogService.deleteVisitLogById(id);
		return Result.success("删除成功");
	}
}
