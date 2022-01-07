package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Description: 分页结果
 * @author Raxcl
 * @date 2022-01-07 09:33:46
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PageResultVO<T> {
	/**
	 * 总页数
	 */
	private Integer totalPage;

	/**
	 * 数据
	 */
	private List<T> list;

	public PageResultVO(Integer totalPage, List<T> list) {
		this.totalPage = totalPage;
		this.list = list;
	}
}
