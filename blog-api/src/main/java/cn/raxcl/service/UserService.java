package cn.raxcl.service;

import cn.raxcl.entity.User;

public interface UserService {
	User findUserByUsernameAndPassword(String username, String password);
}
