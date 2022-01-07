package cn.raxcl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 访客更新DTO
 * @author Raxcl
 * @date 2022-01-07 09:26:49
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitLogUuidTimeDTO {
	private String uuid;
	private Date time;
	private Integer pv;
}
