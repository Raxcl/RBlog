package cn.raxcl.util.common;

import cn.raxcl.exception.NotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 数据校验
 * @author Raxcl
 * @date 2022-01-11 10:36:19
 */
public class ValidatorUtils {
	private ValidatorUtils(){}
	private static final Validator VALIDATOR;

	static {
		VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * 校验对象
	 *
	 * @param object 待校验对象
	 * @param groups 待校验的组
	 * @throws RuntimeException 校验不通过，则报BusinessException异常
	 */
	public static void validateEntity(Object object, Class<?>... groups) throws NotFoundException {
		Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
			throw new NotFoundException(constraint.getMessage());
		}
	}
}
