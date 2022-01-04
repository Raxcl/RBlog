package cn.raxcl.service;

import cn.raxcl.entity.User;

/**
 * @author c-long.chan
 * 2022-01-04 19:05:36
 */
public interface UserService {
	/**
	 * 根据用户名和密码查询用户信息
	 * @param username 用户名
	 * @param password 密码
	 * @return User
	 */
	User findUserByUsernameAndPassword(String username, String password);
}
